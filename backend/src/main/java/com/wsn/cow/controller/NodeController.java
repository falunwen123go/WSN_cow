package com.wsn.cow.controller;

import com.wsn.cow.common.Result;
import com.wsn.cow.entity.NodeInfo;
import com.wsn.cow.service.NodeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 节点管理控制器
 */
@RestController
@RequestMapping("/api/node")
public class NodeController extends BaseController {
    
    @Autowired
    private NodeInfoService nodeInfoService;
    
    /**
     * 获取所有节点列表
     * GET /api/node/list
     */
    @GetMapping("/list")
    public Result<List<NodeInfo>> getNodeList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("查询节点列表: pageNum={}, pageSize={}", pageNum, pageSize);
        List<NodeInfo> list = nodeInfoService.queryAllNodes(pageNum, pageSize);
        return Result.success(list);
    }
    
    /**
     * 获取在线节点数
     * GET /api/node/online/count
     */
    @GetMapping("/online/count")
    public Result<Long> getOnlineCount() {
        logger.info("查询在线节点数");
        Long count = nodeInfoService.countOnlineNodes();
        return Result.success(count);
    }
    
    /**
     * 获取网络拓扑
     * GET /api/node/topology
     */
    @GetMapping("/topology")
    public Result<List<NodeInfo>> getTopology() {
        logger.info("查询网络拓扑");
        List<NodeInfo> list = nodeInfoService.queryAllNodes(1, 100);
        return Result.success(list);
    }
    
    /**
     * 获取节点详情
     * GET /api/node/{nodeId}
     */
    @GetMapping("/{nodeId}")
    public Result<NodeInfo> getNodeInfo(@PathVariable String nodeId) {
        logger.info("查询节点详情: nodeId={}", nodeId);
        NodeInfo nodeInfo = nodeInfoService.getNodeByNodeId(nodeId);
        if (nodeInfo == null) {
            return Result.notFound("节点不存在");
        }
        return Result.success(nodeInfo);
    }
    
    /**
     * 添加节点
     * POST /api/node
     */
    @PostMapping
    public Result<String> addNode(@RequestBody NodeInfo nodeInfo) {
        logger.info("添加节点: {}", nodeInfo);
        
        // 验证参数
        if (nodeInfo.getNodeId() == null || nodeInfo.getNodeId().trim().isEmpty()) {
            return Result.badRequest("节点ID不能为空");
        }
        if (nodeInfo.getNodeName() == null || nodeInfo.getNodeName().trim().isEmpty()) {
            return Result.badRequest("节点名称不能为空");
        }
        
        // 检查节点是否已存在
        NodeInfo existing = nodeInfoService.getNodeByNodeId(nodeInfo.getNodeId());
        if (existing != null) {
            return Result.error("节点ID已存在");
        }
        
        nodeInfoService.addNode(nodeInfo);
        return Result.success("节点添加成功", "节点添加成功");
    }
    
    /**
     * 更新节点
     * PUT /api/node/{nodeId}
     */
    @PutMapping("/{nodeId}")
    public Result<String> updateNode(@PathVariable String nodeId, @RequestBody NodeInfo nodeInfo) {
        logger.info("更新节点: nodeId={}, nodeInfo={}", nodeId, nodeInfo);
        
        // 检查节点是否存在
        NodeInfo existing = nodeInfoService.getNodeByNodeId(nodeId);
        if (existing == null) {
            return Result.notFound("节点不存在");
        }
        
        nodeInfo.setId(existing.getId());
        nodeInfoService.updateNode(nodeInfo);
        return Result.success("节点更新成功", "节点更新成功");
    }
    
    /**
     * 删除节点
     * DELETE /api/node/{nodeId}
     */
    @DeleteMapping("/{nodeId}")
    public Result<String> deleteNode(@PathVariable String nodeId) {
        logger.info("删除节点: nodeId={}", nodeId);
        
        // 检查节点是否存在
        NodeInfo existing = nodeInfoService.getNodeByNodeId(nodeId);
        if (existing == null) {
            return Result.notFound("节点不存在");
        }
        
        nodeInfoService.deleteNode(existing.getId());
        return Result.success("节点删除成功", "节点删除成功");
    }
}
