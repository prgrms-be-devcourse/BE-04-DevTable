package com.mdh.owner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mdh.common", "com.mdh.owner"})
public class OwnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OwnerApplication.class, args);
    }

}
