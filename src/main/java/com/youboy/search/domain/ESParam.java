package com.youboy.search.domain;

import org.elasticsearch.client.Client;

public class ESParam {
	
	private Client client;
	//批量操作数
	private int batchSize;
	//批量未满batchSize条时，在forceBulkTime之后强制插入
	private long forceBulkTime;
	
	public ESParam(Client client,int batchSize, long forceBulkTime){
		this.client = client;
		this.batchSize = batchSize;
		this.forceBulkTime = forceBulkTime;
	}
	
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
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
