package com.youboy.search;

import com.youboy.search.domain.ESParam;
import com.youboy.search.domain.RabbitMqParam;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Consumer extends AbstractConsumer {

    public static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    // 用于记录日志
    public static AtomicLong mqDeleteNum = new AtomicLong(0);
    public static AtomicLong mqIndexNum = new AtomicLong(0);
    public static AtomicLong esDeleteNum = new AtomicLong(0);
    public static AtomicLong esIndexNum = new AtomicLong(0);
    private Client client;
    private ThreadPoolExecutor executor;

    private void init() {
        initESClient();
    }

    private void initESClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", getClusterName()).put("client.transport.sniff", true).build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(getEsHost(), 9300));
    }

    public void work() {


        try {
            init();
            int threadInitNum = getCompanyConsumerNum() + getProductConsumerNum() + getPurchaseConsumerNum();
            executor = new ThreadPoolExecutor(threadInitNum, Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
            RabbitMqParam rabbitMqParam = new RabbitMqParam(getRabbitHost(), getRabbitPort(), getUsername(), getPassword());
            ESParam esParam = new ESParam(client, getBatchSize(), getForceBulkTime());
            // 公司consumers
            if (getCompanyConsumerNum() > 0) {
                for (int i = 0; i < getCompanyConsumerNum(); i++) {
                    executor.execute(new ConsumerTask(getCompanyQueue(), esParam, rabbitMqParam));
                }
            }
            // 产品consumers
            if (getProductConsumerNum() > 0) {
                for (int i = 0; i < getProductConsumerNum(); i++) {
                    executor.execute(new ConsumerTask(getProductQueue(), esParam, rabbitMqParam));
                }
            }
            // 求购consumers
            if (getPurchaseConsumerNum() > 0) {
                for (int i = 0; i < getPurchaseConsumerNum(); i++) {
                    executor.execute(new ConsumerTask(getPurchaseQueue(), esParam, rabbitMqParam));
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }

    }

}
