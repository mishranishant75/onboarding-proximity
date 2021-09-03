package com.example.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class SpringFeignServiceApplication {
	@Autowired
	BeanFactory beanFactory;

	public static void main(String[] args) {
		SpringApplication.run(SpringFeignServiceApplication.class, args);
	}
	
	@Bean
	public ExecutorService traceableExecutorService() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		return new TraceableExecutorService(beanFactory, executorService);
	}

}
