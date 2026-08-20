package com.parkinglot.parkinglot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParkinglotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkinglotApplication.class, args);
    }
}