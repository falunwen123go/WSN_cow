package com.wsn.cow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用主类
 * 
 * @author WSN Team
 * @since 2024-01-18
 */
@SpringBootApplication
@MapperScan("com.wsn.cow.mapper")
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
