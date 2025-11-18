package com.wsn.cow.service.impl;

import com.wsn.cow.entity.NodeInfo;
import com.wsn.cow.mapper.NodeInfoMapper;
import com.wsn.cow.service.NodeInfoService;
import com.wsn.cow.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 节点信息服务实现类
 */
@Service
public class NodeInfoServiceImpl implements NodeInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(NodeInfoServiceImpl.class);
    
    // 离线判断阈值：5分钟未通信则认为离线
    private static final int OFFLINE_THRESHOLD_MINUTES = 5;
    
    @Autowired
    private NodeInfoMapper nodeInfoMapper;
    
    @Override
    public List<NodeInfo> getAllNodes() {
        return nodeInfoMapper.selectAll();
    }
    
    @Override
    public NodeInfo getNodeByNodeId(String nodeId) {
        return nodeInfoMapper.selectByNodeId(nodeId);
    }
    
    @Override
    @Transactional
    public void updateNodeCommInfo(String nodeId, Integer batteryLevel, Integer signalStrength) {
        // 更新通信信息
        nodeInfoMapper.updateCommInfo(nodeId, batteryLevel, signalStrength);
        
        // 更新节点状态为在线
        nodeInfoMapper.updateStatus(nodeId, 1);
        
        logger.debug("更新节点通信信息: nodeId={}, battery={}, signal={}", 
                    nodeId, batteryLevel, signalStrength);
    }
    
    @Override
    @Transactional
    public void updateNodeStatus(String nodeId, Integer status) {
        nodeInfoMapper.updateStatus(nodeId, status);
        logger.info("更新节点状态: nodeId={}, status={}", nodeId, status);
    }
    
    @Override
    @Transactional
    public void checkNodeOnlineStatus() {
        List<NodeInfo> allNodes = nodeInfoMapper.selectAll();
        Date offlineThreshold = DateUtils.getHoursBefore(0);
        // 计算5分钟前的时间
        offlineThreshold = new Date(offlineThreshold.getTime() - OFFLINE_THRESHOLD_MINUTES * 60 * 1000);
        
        int offlineCount = 0;
        for (NodeInfo node : allNodes) {
            // 如果节点当前状态是在线，但最后通信时间超过阈值，则标记为离线
            if (node.getStatus() == 1 && 
                node.getLastCommTime() != null && 
                node.getLastCommTime().before(offlineThreshold)) {
                
                nodeInfoMapper.updateStatus(node.getNodeId(), 0);
                offlineCount++;
                logger.warn("节点离线: nodeId={}, lastCommTime={}", 
                           node.getNodeId(), node.getLastCommTime());
            }
        }
        
        if (offlineCount > 0) {
            logger.info("节点在线状态检查完成: offlineCount={}", offlineCount);
        }
    }
    
    @Override
    public List<NodeInfo> queryAllNodes(Integer pageNum, Integer pageSize) {
        // 简单实现：返回所有节点（实际项目中应该使用PageHelper进行分页）
        return nodeInfoMapper.selectAll();
    }
    
    @Override
    public NodeInfo getNodeInfo(Long id) {
        return nodeInfoMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public void addNode(NodeInfo nodeInfo) {
        if (nodeInfo.getStatus() == null) {
            nodeInfo.setStatus(0); // 默认离线状态
        }
        if (nodeInfo.getInstallDate() == null) {
            nodeInfo.setInstallDate(new Date());
        }
        nodeInfoMapper.insert(nodeInfo);
        logger.info("添加节点: nodeId={}, name={}", nodeInfo.getNodeId(), nodeInfo.getNodeName());
    }
    
    @Override
    @Transactional
    public void updateNode(NodeInfo nodeInfo) {
        nodeInfoMapper.updateById(nodeInfo);
        logger.info("更新节点: id={}, nodeId={}", nodeInfo.getId(), nodeInfo.getNodeId());
    }
    
    @Override
    @Transactional
    public void deleteNode(Long id) {
        nodeInfoMapper.deleteById(id);
        logger.info("删除节点: id={}", id);
    }
    
    @Override
    public Long countOnlineNodes() {
        List<NodeInfo> allNodes = nodeInfoMapper.selectAll();
        long count = allNodes.stream().filter(node -> node.getStatus() == 1).count();
        return count;
    }
}
