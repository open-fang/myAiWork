package com.auth.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Authorization Management System V7 Application
 */
@SpringBootApplication
@MapperScan("com.auth.management.mapper")
public class AuthManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthManagementApplication.class, args);
    }
}