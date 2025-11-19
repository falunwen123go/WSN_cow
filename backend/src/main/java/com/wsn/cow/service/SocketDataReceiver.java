package com.wsn.cow.service;

import com.wsn.cow.entity.SensorData;
import com.wsn.cow.util.DateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket数据接收服务
 * 监听8888端口，接收Python模拟器发送的传感器数据
 */
@Service
public class SocketDataReceiver {
    
    private static final Logger logger = LoggerFactory.getLogger(SocketDataReceiver.class);
    
    @Value("${socket.server.port:8888}")
    private int serverPort;
    
    @Value("${socket.server.enabled:true}")
    private boolean enabled;
    
    @Autowired
    private SensorDataService sensorDataService;
    
    @Autowired
    private DeviceControlService deviceControlService;
    
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private volatile boolean running = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        if (!enabled) {
            logger.info("Socket数据接收服务已禁用");
            return;
        }
        
        executorService = Executors.newFixedThreadPool(10);
        
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(serverPort);
                running = true;
                logger.info("Socket数据接收服务启动成功，监听端口: {}", serverPort);
                
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        logger.info("接收到客户端连接: {}", clientSocket.getRemoteSocketAddress());
                        
                        // 提交到线程池处理
                        executorService.submit(() -> handleClient(clientSocket));
                        
                    } catch (IOException e) {
                        if (running) {
                            logger.error("接受客户端连接失败", e);
                        }
                    }
                }
                
            } catch (IOException e) {
                logger.error("启动Socket服务器失败", e);
            }
        }).start();
    }
    
    public void stop() {
        running = false;
        
        if (executorService != null) {
            executorService.shutdownNow();
        }
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                logger.info("Socket数据接收服务已停止");
            } catch (IOException e) {
                logger.error("关闭ServerSocket失败", e);
            }
        }
    }
    
    /**
     * 处理客户端连接
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // 解析JSON数据包
                    parseAndSaveData(line);
                } catch (Exception e) {
                    logger.error("解析数据包失败: {}", line, e);
                }
            }
            
        } catch (IOException e) {
            logger.error("读取客户端数据失败", e);
        } finally {
            try {
                clientSocket.close();
                logger.info("客户端连接关闭: {}", clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                logger.error("关闭客户端Socket失败", e);
            }
        }
    }
    
    /**
     * 解析并保存数据
     */
    private void parseAndSaveData(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            
            // 解析ZigBee数据包
            if (rootNode.has("payload")) {
                JsonNode payload = rootNode.get("payload");
                
                // 提取节点ID
                String nodeId = payload.get("node_id").asText();
                
                // 提取时间戳
                String timestampStr = payload.get("timestamp").asText();
                Date collectTime = DateUtils.parseDateTime(timestampStr);
                if (collectTime == null) {
                    collectTime = new Date();
                }
                
                // 提取传感器数据
                JsonNode dataNode = payload.get("data");
                Double temperature = dataNode.get("temperature").asDouble();
                Double humidity = dataNode.get("humidity").asDouble();
                Double nh3 = dataNode.get("nh3").asDouble();
                Double h2s = dataNode.get("h2s").asDouble();
                Double milkYield = dataNode.has("milk_yield") ? dataNode.get("milk_yield").asDouble() : null;
                
                // 构建SensorData对象
                SensorData sensorData = new SensorData();
                sensorData.setNodeId(nodeId);
                sensorData.setTemperature(temperature);
                sensorData.setHumidity(humidity);
                sensorData.setNh3Concentration(nh3);
                sensorData.setH2sConcentration(h2s);
                sensorData.setMilkYield(milkYield);
                sensorData.setCollectTime(collectTime);
                
                // 保存数据（包含验证和告警检测）
                sensorDataService.saveSensorData(sensorData);
                
                // 执行自动控制逻辑
                try {
                    deviceControlService.checkAndControlByNH3(sensorData);
                    deviceControlService.checkAndControlByH2S(sensorData);
                    deviceControlService.checkAndControlByTemperature(sensorData);
                } catch (Exception e) {
                    logger.error("执行自动控制失败: nodeId={}", nodeId, e);
                }
                
                logger.info("接收并保存传感器数据: nodeId={}, temp={}℃, humi={}%, NH3={}ppm, H2S={}ppm", 
                           nodeId, temperature, humidity, nh3, h2s);
                
            } else {
                logger.warn("数据包格式错误，缺少payload字段: {}", jsonData);
            }
            
        } catch (Exception e) {
            logger.error("解析JSON数据失败: {}", jsonData, e);
        }
    }
    
    /**
     * 检查服务是否运行
     */
    public boolean isRunning() {
        return running;
    }
}
