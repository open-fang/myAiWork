package com.auth.letter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.auth.letter.mapper")
public class AuthLetterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthLetterApplication.class, args);
    }
}