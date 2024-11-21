package com.epam.edp.demo;

import com.epam.edp.demo.config.RsaConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(RsaConfigurationProperties.class)
public class  GymappBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymappBackendApplication.class, args);
    }
}
