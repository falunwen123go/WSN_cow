// 初始化拓扑图 - 美化版本
const initTopologyChart = () => {
  if (!topologyChart.value) return
  
  // 如果已存在图表实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  // 创建新图表实例
  chartInstance = echarts.init(topologyChart.value)
  
  // 构建图数据
  const nodes = []
  const links = []
  
  // 添加协调器节点（中心节点）- 使用渐变色
  nodes.push({
    id: 'coordinator',
    name: 'ZigBee协调器',
    symbol: 'diamond',
    symbolSize: 80,
    x: 400,
    y: 250,
    itemStyle: {
      color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
        { offset: 0, color: '#4facfe' },
        { offset: 1, color: '#00f2fe' }
      ]),
      borderColor: '#fff',
      borderWidth: 3,
      shadowBlur: 20,
      shadowColor: 'rgba(79, 172, 254, 0.5)'
    },
    label: {
      show: true,
      fontSize: 14,
      fontWeight: 'bold',
      color: '#303133'
    }
  })
  
  // 添加传感器节点（以圆形排列）
  const nodeCount = tableData.value.length
  const radius = 200
  const centerX = 400
  const centerY = 250
  
  tableData.value.forEach((node, index) => {
    const angle = (2 * Math.PI * index) / nodeCount - Math.PI / 2
    const x = centerX + radius * Math.cos(angle)
    const y = centerY + radius * Math.sin(angle)
    
    // 根据状态设置颜色渐变
    let gradientColors = { start: '#909399', end: '#606266' }
    if (node.status === 1) {
      gradientColors = { start: '#85ce61', end: '#5cb85c' }
    } else if (node.status === 2) {
      gradientColors = { start: '#f78989', end: '#f56c6c' }
    }
    
    // 根据电池电量调整大小
    const sizeMultiplier = 0.5 + (node.batteryLevel / 200)
    const symbolSize = 60 * sizeMultiplier
    
    nodes.push({
      id: node.nodeId,
      name: `${node.nodeName}\n${node.location || ''}`,
      symbol: 'circle',
      symbolSize: symbolSize,
      x: x,
      y: y,
      itemStyle: {
        color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
          { offset: 0, color: gradientColors.start },
          { offset: 0.7, color: gradientColors.start },
          { offset: 1, color: gradientColors.end }
        ]),
        borderColor: '#fff',
        borderWidth: 2,
        shadowBlur: 15,
        shadowColor: node.status === 1 ? 'rgba(103, 194, 58, 0.4)' : 'rgba(144, 147, 153, 0.3)'
      },
      label: {
        show: true,
        fontSize: 11,
        color: '#303133'
      },
      tooltip: {
        formatter: () => {
          return `
            <div style="padding: 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 6px;">
              <div style="color: #fff; font-weight: bold; margin-bottom: 6px;">${node.nodeName}</div>
              <div style="color: #fff; font-size: 12px;">节点ID: ${node.nodeId}</div>
              <div style="color: #fff; font-size: 12px;">位置: ${node.location || '-'}</div>
              <div style="color: #fff; font-size: 12px;">状态: ${node.status === 1 ? '🟢 在线' : node.status === 2 ? '🔴 故障' : '⚪ 离线'}</div>
              <div style="color: #fff; font-size: 12px;">电池: 🔋 ${node.batteryLevel}%</div>
              <div style="color: #fff; font-size: 12px;">信号: 📶 ${node.signalStrength} dBm</div>
            </div>
          `
        }
      },
      nodeData: node
    })
    
    // 添加连接线
    links.push({
      source: 'coordinator',
      target: node.nodeId,
      lineStyle: {
        color: node.status === 1 ? 
          new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: 'rgba(79, 172, 254, 0.8)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.8)' }
          ]) : 'rgba(220, 223, 230, 0.6)',
        width: node.status === 1 ? 3 : 2,
        type: node.status === 1 ? 'solid' : 'dashed',
        shadowBlur: node.status === 1 ? 10 : 0,
        shadowColor: node.status === 1 ? 'rgba(103, 194, 58, 0.3)' : 'transparent'
      },
      // 脉冲动画效果
      effect: node.status === 1 ? {
        show: true,
        period: 3,
        trailLength: 0.2,
        symbol: 'circle',
        symbolSize: 4,
        color: '#67C23A'
      } : undefined
    })
  })
  
  // 配置图表选项
  const option = {
    title: {
      text: '星型ZigBee网络拓扑',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#333',
      borderWidth: 1
    },
    legend: {
      data: [
        { name: '在线', icon: 'circle', textStyle: { color: '#67C23A' } },
        { name: '离线', icon: 'circle', textStyle: { color: '#909399' } },
        { name: '故障', icon: 'circle', textStyle: { color: '#F56C6C' } },
        { name: '协调器', icon: 'diamond', textStyle: { color: '#409EFF' } }
      ],
      top: 40,
      left: 'center',
      itemGap: 20
    },
    animation: true,
    animationDuration: 1500,
    animationEasing: 'elasticOut',
    series: [{
      type: 'graph',
      layout: 'none',
      roam: true,
      scaleLimit: { min: 0.5, max: 3 },
      label: { show: true },
      edgeSymbol: ['none', 'arrow'],
      edgeSymbolSize: [0, 8],
      data: nodes,
      links: links,
      lineStyle: {
        opacity: 0.9,
        curveness: 0
      },
      emphasis: {
        focus: 'adjacency',
        scale: true,
        lineStyle: { width: 5 },
        itemStyle: {
          shadowBlur: 30,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  chartInstance.setOption(option)
  
  // 添加点击事件
  chartInstance.on('click', (params: any) => {
    if (params.dataType === 'node' && params.data.nodeData) {
      handleView(params.data.nodeData)
    }
  })
  
  // 窗口大小改变时重新调整图表
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}
