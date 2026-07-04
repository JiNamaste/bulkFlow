package com.BulkFlow.bulkFlow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BulkFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulkFlowApplication.class, args);
	}

}
