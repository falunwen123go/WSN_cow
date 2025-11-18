package com.wsn.cow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 * Spring Boot会自动配置DataSource、SqlSessionFactory和TransactionManager
 */
@Configuration
@EnableTransactionManagement
public class MyBatisConfig {
    // Spring Boot自动配置，无需手动配置Bean
}
