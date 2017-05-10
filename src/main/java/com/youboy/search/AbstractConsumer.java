package com.youboy.search;

public abstract class AbstractConsumer {
	
	private String productQueue = "productindex_queue";
	private String companyQueue = "company_queue";
	private String purchaseQueue = "purchase_queue";
	private int productConsumerNum = 1;
	private int companyConsumerNum = 1;
	private int purchaseConsumerNum = 1;
	
	private String clusterName = "cluster1";
	private String esHost = "10.0.8.39";
	
	private String rabbitHost = "10.0.10.3";
	private int rabbitPort = 4443;
	private String username = "rabbit";
	private String password = "rabbit";
	
	//批量操作数
	private int batchSize = 100;
	//批量未满100条时，在forceBulkTime之后强制插入
	private long forceBulkTime = 30*1000;
	
	public String getProductQueue() {
		return productQueue;
	}
	public void setProductQueue(String productQueue) {
		this.productQueue = productQueue;
	}
	public String getCompanyQueue() {
		return companyQueue;
	}
	public void setCompanyQueue(String companyQueue) {
		this.companyQueue = companyQueue;
	}
	public String getPurchaseQueue() {
		return purchaseQueue;
	}
	public void setPurchaseQueue(String purchaseQueue) {
		this.purchaseQueue = purchaseQueue;
	}
	public int getProductConsumerNum() {
		return productConsumerNum;
	}
	public void setProductConsumerNum(int productConsumerNum) {
		this.productConsumerNum = productConsumerNum;
	}
	public int getCompanyConsumerNum() {
		return companyConsumerNum;
	}
	public void setCompanyConsumerNum(int companyConsumerNum) {
		this.companyConsumerNum = companyConsumerNum;
	}
	public int getPurchaseConsumerNum() {
		return purchaseConsumerNum;
	}
	public void setPurchaseConsumerNum(int purchaseConsumerNum) {
		this.purchaseConsumerNum = purchaseConsumerNum;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public String getEsHost() {
		return esHost;
	}
	public void setEsHost(String esHost) {
		this.esHost = esHost;
	}
	public String getRabbitHost() {
		return rabbitHost;
	}
	public void setRabbitHost(String rabbitHost) {
		this.rabbitHost = rabbitHost;
	}
	public int getRabbitPort() {
		return rabbitPort;
	}
	public void setRabbitPort(int rabbitPort) {
		this.rabbitPort = rabbitPort;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public long getForceBulkTime() {
		return forceBulkTime;
	}
	public void setForceBulkTime(long forceBulkTime) {
		this.forceBulkTime = forceBulkTime;
	}

}
