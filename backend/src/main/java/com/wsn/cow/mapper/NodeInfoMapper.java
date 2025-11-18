package com.wsn.cow.mapper;

import com.wsn.cow.entity.NodeInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 节点信息Mapper接口
 */
public interface NodeInfoMapper {
    
    /**
     * 插入节点信息
     */
    int insert(NodeInfo nodeInfo);
    
    /**
     * 根据ID查询
     */
    NodeInfo selectById(Long id);
    
    /**
     * 根据节点ID查询
     */
    NodeInfo selectByNodeId(String nodeId);
    
    /**
     * 查询所有节点
     */
    List<NodeInfo> selectAll();
    
    /**
     * 根据状态查询节点列表
     */
    List<NodeInfo> selectByStatus(Integer status);
    
    /**
     * 更新节点信息
     */
    int updateById(NodeInfo nodeInfo);
    
    /**
     * 更新节点状态
     */
    int updateStatus(@Param("nodeId") String nodeId, 
                    @Param("status") Integer status);
    
    /**
     * 更新节点通信信息
     */
    int updateCommInfo(@Param("nodeId") String nodeId,
                      @Param("batteryLevel") Integer batteryLevel,
                      @Param("signalStrength") Integer signalStrength);
    
    /**
     * 删除节点
     */
    int deleteById(Long id);
}
