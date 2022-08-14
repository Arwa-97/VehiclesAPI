package com.udacity.pricingmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Creates a Spring Boot Application to run the Pricing Service.
 * TODO: Convert the application from a REST API to a microservice.
 */
@SpringBootApplication
@EnableEurekaServer
public class PricingMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricingMicroserviceApplication.class, args);
    }

}
