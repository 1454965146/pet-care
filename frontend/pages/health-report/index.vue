<template>
  <view class="container">
    <!-- 报告头部卡片 -->
    <view class="report-header">
      <view class="header-top">
        <view class="header-left">
          <text class="report-title">月度健康报告</text>
          <text class="report-date">{{ reportPeriod }}</text>
        </view>
        <view class="report-badge">
          <text class="badge-text">{{ healthScore }}分</text>
        </view>
      </view>
      <view class="health-bar-wrap">
        <view class="health-bar-bg">
          <view class="health-bar-fill" :style="{ width: healthScore + '%' }"></view>
        </view>
        <text class="health-bar-label">健康指数</text>
      </view>
    </view>

    <!-- 统计卡片 -->
    <view class="stats-grid">
      <view class="stat-card stat-card-pink">
        <view class="stat-icon-wrap icon-bg-pink">
          <text class="icon-text">⚖️</text>
        </view>
        <text class="stat-value">{{ formatNumber(statistics.avgWeight) }}</text>
        <text class="stat-unit">kg</text>
        <text class="stat-label">平均体重</text>
      </view>
      <view class="stat-card stat-card-orange">
        <view class="stat-icon-wrap icon-bg-orange">
          <text class="icon-text">📈</text>
        </view>
        <text class="stat-value" :class="statistics.weightVolatility > 10 ? 'value-warning' : ''">{{ formatNumber(statistics.weightVolatility) }}</text>
        <text class="stat-unit">%</text>
        <text class="stat-label">体重波动率</text>
      </view>
      <view class="stat-card stat-card-red">
        <view class="stat-icon-wrap icon-bg-red">
          <text class="icon-text">⚠️</text>
        </view>
        <text class="stat-value" :class="statistics.abnormalCount > 0 ? 'value-danger' : 'value-ok'">{{ statistics.abnormalCount || 0 }}</text>
        <text class="stat-unit">次</text>
        <text class="stat-label">异常记录</text>
      </view>
      <view class="stat-card stat-card-green">
        <view class="stat-icon-wrap icon-bg-green">
          <text class="icon-text">💉</text>
        </view>
        <text class="stat-value" :class="statistics.vaccineCompletionRate < 60 ? 'value-danger' : 'value-ok'">{{ statistics.vaccineCompletionRate || 100 }}</text>
        <text class="stat-unit">%</text>
        <text class="stat-label">疫苗完成率</text>
      </view>
    </view>

    <!-- 体重趋势图 -->
    <view class="chart-card">
      <view class="card-title-row">
        <text class="section-title">体重变化趋势</text>
        <text class="chart-tip">含平均标线</text>
      </view>
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
          <text :class="['assess-result', statistics.abnormalCount === 0 ? 'text-ok' : 'text-warn']">{{ statistics.abnormalCount === 0 ? '正常' : statistics.abnormalCount + '次异常' }}</text>
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
      <view class="summary-icon">
        <text class="summary-emoji">📊</text>
      </view>
      <view class="summary-content">
        <text class="summary-text">近30天共记录</text>
        <text class="highlight">{{ statistics.totalRecords || 0 }}</text>
        <text class="summary-text">条护理数据</text>
      </view>
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
  const fmt = (d) => d.getFullYear() + '.' + (d.getMonth() + 1) + '.' + d.getDate()
  return fmt(start) + ' - ' + fmt(now)
})

// 保留两位小数
const formatNumber = (val) => {
  if (val === null || val === undefined) return '--'
  return Number(val).toFixed(2)
}

// 健康指数：综合评分
const healthScore = computed(() => {
  let score = 100
  if (statistics.value.weightVolatility > 10) score -= 15
  if (statistics.value.weightVolatility > 20) score -= 10
  if (statistics.value.abnormalCount > 0) score -= statistics.value.abnormalCount * 10
  if (statistics.value.vaccineCompletionRate < 80) score -= 15
  if (statistics.value.vaccineCompletionRate < 60) score -= 10
  return Math.max(score, 0)
})

const chartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const p = params[0]
        return p.axisValue + '<br/>体重: ' + (p.value ? Number(p.value).toFixed(2) : '--') + ' kg'
      }
    },
    grid: { left: 50, right: 20, top: 30, bottom: 35 },
    xAxis: {
      type: 'category',
      data: statistics.value.dates,
      axisLabel: { fontSize: 10, color: '#999', interval: 4, rotate: 0 },
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { fontSize: 10, color: '#999', formatter: function(v) { return v.toFixed(1) } },
      splitLine: { lineStyle: { color: '#f5f5f5', type: 'dashed' } }
    },
    series: [{
      data: statistics.value.weights,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
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
        lineStyle: { color: '#4ECDC4', type: 'dashed', width: 2 },
        label: {
          fontSize: 10,
          color: '#4ECDC4',
          formatter: function(params) { return '均值 ' + Number(params.value).toFixed(2) }
        }
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

/* 报告头部 */
.report-header {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  border-radius: 24rpx;
  padding: 36rpx 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(255,107,107,0.25);
}
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28rpx;
}
.header-left {
  flex: 1;
}
.report-title {
  font-size: 38rpx;
  font-weight: bold;
  color: #ffffff;
  display: block;
}
.report-date {
  font-size: 22rpx;
  color: rgba(255,255,255,0.8);
  margin-top: 8rpx;
  display: block;
}
.report-badge {
  background: rgba(255,255,255,0.25);
  border-radius: 24rpx;
  padding: 12rpx 24rpx;
}
.badge-text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: bold;
}
.health-bar-wrap {
  display: flex;
  align-items: center;
}
.health-bar-bg {
  flex: 1;
  height: 16rpx;
  background: rgba(255,255,255,0.3);
  border-radius: 8rpx;
  overflow: hidden;
  margin-right: 16rpx;
}
.health-bar-fill {
  height: 100%;
  background: #ffffff;
  border-radius: 8rpx;
  transition: width 0.5s;
}
.health-bar-label {
  font-size: 22rpx;
  color: rgba(255,255,255,0.9);
}

/* 统计卡片 */
.stats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.stat-card {
  width: calc(50% - 8rpx);
  border-radius: 20rpx;
  padding: 28rpx 24rpx;
  position: relative;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}
.stat-card-pink { background: #ffffff; border-left: 6rpx solid #FF6B6B; }
.stat-card-orange { background: #ffffff; border-left: 6rpx solid #FF9F43; }
.stat-card-red { background: #ffffff; border-left: 6rpx solid #FF6B6B; }
.stat-card-green { background: #ffffff; border-left: 6rpx solid #4ECDC4; }
.stat-icon-wrap {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}
.icon-bg-pink { background: #FFF0F0; }
.icon-bg-orange { background: #FFF8E8; }
.icon-bg-red { background: #FFF0F0; }
.icon-bg-green { background: #E8F8F5; }
.icon-text { font-size: 28rpx; }
.stat-value {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
  display: inline;
}
.stat-unit {
  font-size: 24rpx;
  color: #999;
  margin-left: 8rpx;
  font-weight: normal;
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

/* 图表卡片 */
.chart-card, .assess-card, .summary-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.04);
}
.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}
.chart-tip {
  font-size: 22rpx;
  color: #4ECDC4;
  background: #E8F8F5;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}
.chart-wrap {
  width: 100%;
  height: 420rpx;
}
.report-echarts {
  width: 100%;
  height: 420rpx;
}

/* 健康评估 */
.assess-items {
  display: flex;
  flex-direction: column;
}
.assess-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
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

/* 汇总卡片 */
.summary-card {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 36rpx 28rpx;
}
.summary-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}
.summary-emoji { font-size: 36rpx; }
.summary-content {
  display: flex;
  align-items: baseline;
}
.summary-text { font-size: 26rpx; color: rgba(255,255,255,0.9); }
.highlight { font-size: 40rpx; font-weight: bold; color: #ffffff; margin: 0 8rpx; }
</style>
