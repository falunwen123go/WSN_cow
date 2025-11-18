import { request } from '@/utils/http'
import type { NodeInfo, PageParams, PageResult } from '@/types'

// 获取节点列表(分页)
export const getNodeList = (params?: PageParams & { status?: number; keyword?: string }) => {
  return request.get<NodeInfo[]>('/node/list', params)
}

// 获取所有节点(不分页)
export const getAllNodes = () => {
  return request.get<NodeInfo[]>('/node/list', { pageNum: 1, pageSize: 100 })
}

// 根据ID获取节点信息
export const getNodeById = (id: number) => {
  return request.get<NodeInfo>(`/node/${id}`)
}

// 根据节点ID获取节点信息
export const getNodeByNodeId = (nodeId: string) => {
  return request.get<NodeInfo>(`/node/nodeId/${nodeId}`)
}

// 添加节点
export const addNode = (data: Partial<NodeInfo>) => {
  return request.post<void>('/node', data)
}

// 更新节点信息
export const updateNode = (id: number, data: Partial<NodeInfo>) => {
  return request.put<void>(`/node/${id}`, data)
}

// 删除节点
export const deleteNode = (id: number) => {
  return request.delete<void>(`/node/${id}`)
}

// 获取网络拓扑
export const getNodeTopology = () => {
  return request.get<NodeInfo[]>('/node/topology')
}

// 获取在线节点数量
export const getOnlineNodeCount = () => {
  return request.get<number>('/node/online/count')
}
