package com.fabiankevin.reactive.reactiverest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableScheduling
public class ReactiveRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveRestApplication.class, args);
    }

}
