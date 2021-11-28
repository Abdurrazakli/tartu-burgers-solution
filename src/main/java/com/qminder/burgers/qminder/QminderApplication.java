package com.qminder.burgers.qminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class QminderApplication {

    public static void main(String[] args) {
        SpringApplication.run(QminderApplication.class, args);
    }

}
