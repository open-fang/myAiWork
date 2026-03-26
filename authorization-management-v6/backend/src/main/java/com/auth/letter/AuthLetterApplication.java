package com.auth.letter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 授权书管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.auth.letter.dao")
public class AuthLetterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthLetterApplication.class, args);
    }
}