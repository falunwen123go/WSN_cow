import { request } from '@/utils/http'
import type { NodeInfo, PageParams, PageResult } from '@/types'

// 获取节点列表(分页)
export const getNodeList = (params?: PageParams & { nodeType?: number; nodeStatus?: number }) => {
  return request.get<PageResult<NodeInfo>>('/node/list', params)
}

// 获取所有节点(不分页)
export const getAllNodes = () => {
  return request.get<NodeInfo[]>('/node/all')
}

// 根据ID获取节点信息
export const getNodeById = (id: number) => {
  return request.get<NodeInfo>(`/node/${id}`)
}

// 更新节点信息
export const updateNode = (data: Partial<NodeInfo>) => {
  return request.put<void>('/node/update', data)
}

// 获取节点拓扑结构
export const getNodeTopology = () => {
  return request.get<any>('/node/topology')
}

// 获取在线节点数量
export const getOnlineNodeCount = () => {
  return request.get<number>('/node/online/count')
}

// 获取节点状态统计
export const getNodeStatusStats = () => {
  return request.get<{ online: number; offline: number; total: number }>('/node/status/stats')
}

// 检查节点在线状态
export const checkNodeOnline = (nodeId: number) => {
  return request.get<boolean>(`/node/${nodeId}/online`)
}
