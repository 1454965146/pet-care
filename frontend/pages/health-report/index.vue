<template>
  <view class="container">
    <view class="report-header">
      <text class="report-title">月度健康报告</text>
      <text class="report-date">{{ reportPeriod }}</text>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-grid">
      <view class="stat-card">
        <text class="stat-value">{{ statistics.avgWeight || '--' }}</text>
        <text class="stat-label">平均体重(kg)</text>
        <view class="stat-icon stat-icon-weight">
          <text class="icon-text">⚖️</text>
        </view>
      </view>
      <view class="stat-card">
        <text :class="['stat-value', statistics.weightVolatility > 10 ? 'value-warning' : '']">{{ statistics.weightVolatility || 0 }}%</text>
        <text class="stat-label">体重波动率</text>
        <view class="stat-icon stat-icon-vol">
          <text class="icon-text">📈</text>
        </view>
      </view>
      <view class="stat-card">
        <text :class="['stat-value', statistics.abnormalCount > 0 ? 'value-danger' : 'value-ok']">{{ statistics.abnormalCount || 0 }}</text>
        <text class="stat-label">异常记录</text>
        <view class="stat-icon stat-icon-abnormal">
          <text class="icon-text">⚠️</text>
        </view>
      </view>
      <view class="stat-card">
        <text :class="['stat-value', statistics.vaccineCompletionRate < 60 ? 'value-danger' : 'value-ok']">{{ statistics.vaccineCompletionRate || 100 }}%</text>
        <text class="stat-label">疫苗完成率</text>
        <view class="stat-icon stat-icon-vaccine">
          <text class="icon-text">💉</text>
        </view>
      </view>
    </view>
	
	

    <!-- 体重趋势图 -->
    <view class="chart-card">
      <text class="section-title">体重变化趋势</text>
      <view class="chart-wrap" id="chart-wrap">
        <view :prop="chartOption" :change:prop="echarts.updateChart" id="report-echarts" class="report-echarts"></view>
      </view>
    </view>

    <!-- 健康评估 -->
    <view class="assess-card">
      <text class="section-title">健康评估</text>
      <view class="assess-items">
        <view class="assess-item">
          <view class="assess-left">
            <view :class="['assess-dot', statistics.weightVolatility <= 10 ? 'dot-ok' : 'dot-warn']"></view>
            <text class="assess-name">体重稳定性</text>
          </view>
          <text :class="['assess-result', statistics.weightVolatility <= 10 ? 'text-ok' : 'text-warn']">{{ statistics.weightVolatility <= 10 ? '良好' : '需关注' }}</text>
        </view>
        <view class="assess-item">
          <view class="assess-left">
            <view :class="['assess-dot', statistics.abnormalCount === 0 ? 'dot-ok' : 'dot-warn']"></view>
            <text class="assess-name">异常情况</text>
          </view>
          <text :class="['assess-result', statistics.abnormalCount === 0 ? 'text-ok' : 'text-warn']">{{ statistics.abnormalCount === 0 ? '正常' : `${statistics.abnormalCount}次异常` }}</text>
        </view>
        <view class="assess-item">
          <view class="assess-left">
            <view :class="['assess-dot', statistics.vaccineCompletionRate >= 80 ? 'dot-ok' : 'dot-warn']"></view>
            <text class="assess-name">疫苗接种</text>
          </view>
          <text :class="['assess-result', statistics.vaccineCompletionRate >= 80 ? 'text-ok' : 'text-warn']">{{ statistics.vaccineCompletionRate >= 80 ? '按时' : '需补种' }}</text>
        </view>
      </view>
    </view>

    <!-- 总记录数 -->
    <view class="summary-card">
      <text class="summary-text">近30天共记录 <text class="highlight">{{ statistics.totalRecords || 0 }}</text> 条护理数据</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMonthlyStatistics } from '../../utils/api.js'

const petId = 1
const statistics = ref({
  dates: [], weights: [], temperatures: [],
  avgWeight: 0, weightVolatility: 0, abnormalCount: 0,
  totalRecords: 0, vaccineCompletionRate: 100
})

const reportPeriod = computed(() => {
  const now = new Date()
  const start = new Date(now.getTime() - 29 * 24 * 60 * 60 * 1000)
  const fmt = (d) => `${d.getMonth() + 1}/${d.getDate()}`
  return `${fmt(start)} - ${fmt(now)}`
})

const chartOption = computed(() => {
  return {
    tooltip: { trigger: 'axis', formatter: '{b}<br/>体重: {c} kg' },
    grid: { left: 45, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: statistics.value.dates,
      axisLabel: { fontSize: 10, color: '#999', interval: 4 },
      axisLine: { lineStyle: { color: '#e8e8e8' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { fontSize: 10, color: '#999' },
      splitLine: { lineStyle: { color: '#f5f5f5' } }
    },
    series: [{
      data: statistics.value.weights,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#FF6B6B', width: 3 },
      itemStyle: { color: '#FF6B6B', borderWidth: 2, borderColor: '#fff' },
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(255,107,107,0.25)' },
            { offset: 1, color: 'rgba(255,107,107,0.02)' }
          ]
        }
      },
      markLine: {
        silent: true,
        data: [{ type: 'average', name: '平均值' }],
        lineStyle: { color: '#4ECDC4', type: 'dashed' },
        label: { fontSize: 10, color: '#4ECDC4' }
      }
    }]
  }
})

const loadData = async () => {
  try {
    const data = await getMonthlyStatistics(petId)
    if (data) statistics.value = data
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

onMounted(() => {
  loadData()
})
</script>

<script module="echarts" lang="renderjs">
let myChart = null
export default {
  mounted() {
    if (typeof window !== 'undefined' && typeof echarts === 'undefined') {
      const script = document.createElement('script')
      script.src = 'https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js'
      script.onload = () => { this.initChart() }
      document.head.appendChild(script)
    } else if (typeof echarts !== 'undefined') {
      this.initChart()
    }
  },
  methods: {
    initChart() {
      const dom = document.getElementById('report-echarts')
      if (dom) myChart = echarts.init(dom)
    },
    updateChart(newValue) {
      if (myChart && newValue) myChart.setOption(newValue, true)
    }
  }
}
</script>

<style scoped>
.container {
  padding: 24rpx;
  background-color: #f5f6fa;
  min-height: 100vh;
}
.report-header {
  margin-bottom: 24rpx;
}
.report-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  display: block;
}
.report-date {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}
.stats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.stat-card {
  width: calc(50% - 8rpx);
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
}
.stat-value {
  font-size: 44rpx;
  font-weight: bold;
  color: #333;
  display: block;
}
.value-warning { color: #FF9F43; }
.value-danger { color: #FF6B6B; }
.value-ok { color: #4ECDC4; }
.stat-label {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-top: 8rpx;
}
.stat-icon {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-icon-weight { background: #FFF0F0; }
.stat-icon-vol { background: #FFF8E1; }
.stat-icon-abnormal { background: #FFF0F0; }
.stat-icon-vaccine { background: #E8F8F5; }
.icon-text { font-size: 28rpx; }
.chart-card, .assess-card, .summary-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
}
.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}
.chart-wrap {
  width: 100%;
  height: 400rpx;
}
.report-echarts {
  width: 100%;
  height: 400rpx;
}
.assess-items {
  display: flex;
  flex-direction: column;
}
.assess-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.assess-item:last-child { border-bottom: none; }
.assess-left {
  display: flex;
  align-items: center;
}
.assess-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  margin-right: 16rpx;
}
.dot-ok { background: #4ECDC4; }
.dot-warn { background: #FF9F43; }
.assess-name { font-size: 28rpx; color: #333; }
.assess-result { font-size: 26rpx; font-weight: bold; }
.text-ok { color: #4ECDC4; }
.text-warn { color: #FF9F43; }
.summary-card {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  text-align: center;
}
.summary-text { font-size: 28rpx; color: rgba(255,255,255,0.9); }
.highlight { font-size: 36rpx; font-weight: bold; color: #ffffff; }
</style>
