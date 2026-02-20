package com.careconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CareConnectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareConnectApplication.class, args);
    }
}
