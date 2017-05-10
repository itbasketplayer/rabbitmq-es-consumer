package com.youboy.search.domain;

public class RabbitMqParam {

	private String rabbitHost;
	private int rabbitPort;
	private String username;
	private String password;
	
	public RabbitMqParam(String rabbitHost,int rabbitPort, String username, String password){
		this.rabbitHost = rabbitHost;
		this.rabbitPort = rabbitPort;
		this.username = username;
		this.password = password;
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

}
