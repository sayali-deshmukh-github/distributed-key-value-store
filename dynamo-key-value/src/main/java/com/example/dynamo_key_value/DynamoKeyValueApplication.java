package com.example.dynamo_key_value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.dynamo_key_value.config.ClusterProperties;

@SpringBootApplication
@EnableConfigurationProperties(ClusterProperties.class)
public class DynamoKeyValueApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamoKeyValueApplication.class, args);
	}

}
