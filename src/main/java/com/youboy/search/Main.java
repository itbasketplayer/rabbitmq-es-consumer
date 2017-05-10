package com.youboy.search;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Main {
	
	private static ApplicationContext context;
	private static String contextFile = "classpath:application.xml";
	
	public static void init(){
		try {
			context = new FileSystemXmlApplicationContext(contextFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		init();
		Consumer consumer = (Consumer) context.getBean("consumer");
		consumer.work();
	}

}
