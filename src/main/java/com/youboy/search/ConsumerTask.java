package com.youboy.search;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.youboy.search.domain.ESParam;
import com.youboy.search.domain.EsRequestBean;
import com.youboy.search.domain.EsRequestBean.RequestType;
import com.youboy.search.domain.RabbitMqParam;

/**
 * @Title:()
 * @Desription:(索引做批量，删除不做批量)
 * @Company:youboy
 * @ClassName:ConsumerTask.java
 * @Author:binko
 * @CreateDate:2015年7月27日
 * @UpdateUser:Administrator
 * @Version:0.1
 */
public class ConsumerTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerTask.class);

	private volatile List<IndexRequestBuilder> indexBuffer = new LinkedList<IndexRequestBuilder>();
	// 缓存进队列的时候，上一次buffer的时间
	private volatile AtomicLong indexLastTime = new AtomicLong(0);

	private String queueName;
	private RabbitMqParam rabbitMqParam;
	private ESParam esParam;

	private Client client;
	private BulkRequestBuilder bulkRequest;

	public ConsumerTask(String queueName, ESParam esParam, RabbitMqParam rabbitMqParam) {
		this.queueName = queueName;
		this.esParam = esParam;
		this.rabbitMqParam = rabbitMqParam;
		// 初始化批量索引请求
		this.bulkRequest = esParam.getClient().prepareBulk();
		// 初始化client
		this.client = esParam.getClient();
	}


	public void run() {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitMqParam.getRabbitHost());
		factory.setPort(rabbitMqParam.getRabbitPort());
		factory.setUsername(rabbitMqParam.getUsername());
		factory.setPassword(rabbitMqParam.getPassword());
		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(queueName, true, false, false, null);
			// 同一时间给一个queue不超过4条信息
			// channel.basicQos(4);
			// ack之后再发送下一条，同一时间只处理一条
			channel.basicQos(0, 1, false);
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, false, consumer);
			String message = "";
			
			//开启forceTime监控
			Timer timer = new Timer();
	        timer.schedule(new ForceTimeWatcher(), 0, esParam.getForceBulkTime());
			while (true) {
				Delivery delivery;
				delivery = consumer.nextDelivery();
				message = new String(delivery.getBody());
				EsRequestBean esRequestBean = new EsRequestBean();
				esRequestBean = esRequestBean.JsonToBean(message);
				if (esRequestBean != null) {
					if (RequestType.DELETE.equals(esRequestBean.getRequestType())) {
						client.prepareDelete(esRequestBean.getIndex(), esRequestBean.getType(), esRequestBean.getId()).execute().actionGet();
						// 接收信息之后即ack，不管es是否插入成功（事实上bulk无法每条返回）
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
						
						Consumer.mqDeleteNum.addAndGet(1);
						if (Consumer.mqDeleteNum.get() % 50 == 0) {
							logger.info("接收mq删除数：" + Consumer.mqDeleteNum.get());
						}
						Consumer.esDeleteNum.addAndGet(1);
						if (Consumer.esDeleteNum.get() % 50 == 0) {
							logger.info("es删除数：" + Consumer.esDeleteNum.get());
						}
					} else {
						IndexRequestBuilder indexRequest = client.prepareIndex(esRequestBean.getIndex(), esRequestBean.getType(), esRequestBean.getId()).setSource(esRequestBean.getIndexValue());
						indexBuffer.add(indexRequest);
						
						// 达到批量立即提交
						if (indexBuffer.size() > esParam.getBatchSize()) {
							bulkIndexEs();
						}
						// 接收信息之后即ack，不管es是否插入成功（事实上bulk无法每条返回）
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
						Consumer.mqIndexNum.addAndGet(1);
						if (Consumer.mqIndexNum.get() % 50 == 0) {
							logger.info("接收mq索引数：" + Consumer.mqIndexNum.get());
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("处理mq的while异常.\texc:", e);
		} finally {
			try {
				if (channel != null && channel.isOpen()) {
					channel.close();
				}
				if (connection != null && connection.isOpen()) {
					connection.close();
				}
			} catch (IOException e) {
				logger.error("channel关闭失败.\texc:" + e);
			}
		}

	}
	//批量插入es
	public void bulkIndexEs(){
		if(indexBuffer.size()>0){
		final List<IndexRequestBuilder> nowHandlerBuffer = indexBuffer;
		// 生成新的一个buffer对象，接收新的信息
		indexBuffer = new LinkedList<IndexRequestBuilder>();
		for (IndexRequestBuilder request : nowHandlerBuffer) {
			bulkRequest.add(request);
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			logger.error("批量索引有错误：", bulkResponse.buildFailureMessage());
		}
		// 批量完成更新上一次时间
		indexLastTime.set(System.currentTimeMillis());
		Consumer.esIndexNum.addAndGet(nowHandlerBuffer.size());
		logger.info("es 插入成功数据：" + Consumer.esIndexNum.get());
		}
	}
	
	//批量提交时，未到达batch的数量按forcetime来消费，要有一个定时线程来监控这个
	class ForceTimeWatcher extends TimerTask{

		@Override
		public void run() {
			Long now = System.currentTimeMillis();
			if((now - indexLastTime.get())>esParam.getForceBulkTime()){
				bulkIndexEs();
			}
			logger.info("ForceTimeWatcher:"+(now - indexLastTime.get()));
		}
		
	}

}

