package com.auth.letter.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置
 */
@Configuration
@MapperScan("com.auth.letter.dao")
public class MyBatisConfig {
}