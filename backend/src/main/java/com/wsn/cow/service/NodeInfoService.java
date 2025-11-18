package com.wsn.cow.service;

import com.wsn.cow.entity.NodeInfo;
import java.util.List;

/**
 * 节点信息服务接口
 */
public interface NodeInfoService {
    
    /**
     * 获取所有节点信息
     */
    List<NodeInfo> getAllNodes();
    
    /**
     * 根据节点ID获取节点信息
     */
    NodeInfo getNodeByNodeId(String nodeId);
    
    /**
     * 更新节点通信信息（电池、信号强度、最后通信时间）
     */
    void updateNodeCommInfo(String nodeId, Integer batteryLevel, Integer signalStrength);
    
    /**
     * 更新节点状态
     */
    void updateNodeStatus(String nodeId, Integer status);
    
    /**
     * 检查节点在线状态（超过一定时间未通信则标记为离线）
     */
    void checkNodeOnlineStatus();
    
    /**
     * 查询所有节点信息（分页）
     */
    List<NodeInfo> queryAllNodes(Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID获取节点详细信息
     */
    NodeInfo getNodeInfo(Long id);
    
    /**
     * 添加节点
     */
    void addNode(NodeInfo nodeInfo);
    
    /**
     * 更新节点信息
     */
    void updateNode(NodeInfo nodeInfo);
    
    /**
     * 删除节点
     */
    void deleteNode(Long id);
    
    /**
     * 统计在线节点数量
     */
    Long countOnlineNodes();
}
