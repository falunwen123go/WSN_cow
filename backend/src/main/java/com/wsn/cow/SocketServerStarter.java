package com.wsn.cow;

import com.wsn.cow.service.SocketDataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Socket服务器独立启动器
 * 用于测试数据接收功能
 */
public class SocketServerStarter {
    
    private static final Logger logger = LoggerFactory.getLogger(SocketServerStarter.class);
    
    @Configuration
    @ComponentScan(basePackages = {
        "com.wsn.cow.config",
        "com.wsn.cow.service", 
        "com.wsn.cow.mapper"
    })
    public static class AppConfig {
    }
    
    public static void main(String[] args) {
        try {
            logger.info("正在启动Socket数据接收服务器...");
            
            // 加载Spring容器
            AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class);
            
            // 获取SocketDataReceiver Bean
            SocketDataReceiver receiver = context.getBean(SocketDataReceiver.class);
            
            logger.info("Socket服务器已启动，正在监听端口 8888...");
            logger.info("按 Ctrl+C 停止服务器");
            
            // 添加关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("正在关闭Socket服务器...");
                receiver.stop();
                context.close();
                logger.info("Socket服务器已关闭");
            }));
            
            // 保持主线程运行
            Thread.currentThread().join();
            
        } catch (Exception e) {
            logger.error("启动Socket服务器失败", e);
            System.exit(1);
        }
    }
}
