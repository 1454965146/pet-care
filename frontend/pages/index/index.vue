<template>
  <view class="container">
    <!-- 宠物信息卡片 -->
    <view class="pet-card">
      <view class="pet-avatar">
        <text class="avatar-emoji">🐱</text>
      </view>
      <view class="pet-info">
        <text class="pet-name">{{ petInfo.name || '小橘' }}</text>
        <text class="pet-breed">{{ petInfo.breed || '橘猫' }}</text>
      </view>
      <view class="pet-age-tag">
        <text class="age-text">{{ petAge }}</text>
      </view>
      <view class="add-pet-btn" @click="goAddPet">
        <text class="add-pet-icon">+</text>
      </view>
    </view>
	
	


    <!-- 体重体温趋势图 -->
    <view class="chart-card">
      <view class="card-header">
        <text class="card-title">健康趋势</text>
        <view class="chart-tabs">
          <text :class="['tab-item', chartType === 'weight' ? 'tab-active' : '']" @click="chartType = 'weight'">体重</text>
          <text :class="['tab-item', chartType === 'temperature' ? 'tab-active' : '']" @click="chartType = 'temperature'">体温</text>
        </view>
      </view>
      <view class="chart-container" id="chart-container">
        <view :prop="chartOption" :change:prop="echarts.updateChart" id="echarts-dom" class="echarts-dom"></view>
      </view>
    </view>

    <!-- 今日待办 -->
    <view class="reminder-card">
      <view class="card-header">
        <text class="card-title">今日待办</text>
        <text class="reminder-count">{{ reminders.length }}项</text>
      </view>
      <view v-if="reminders.length === 0" class="empty-tip">
        <text class="empty-text">今天没有待办事项~</text>
      </view>
      <view v-else class="reminder-list">
        <view v-for="item in reminders" :key="item.id" :class="['reminder-item', item.expired ? 'reminder-expired' : '']">
          <view :class="['reminder-dot', getCareTypeColor(item.careTypeName)]"></view>
          <view class="reminder-content">
            <text class="reminder-title">{{ item.title }}</text>
            <text class="reminder-date">{{ formatReminderDate(item) }}</text>
          </view>
          <view v-if="item.expired" class="expired-tag">
            <text class="expired-text">已过期</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMonthlyStatistics, getTodayReminders } from '../../utils/api.js'

const petInfo = ref({ name: '小橘', breed: '橘猫', birthDate: '2023-03-15', species: '猫' })
const chartType = ref('weight')
const reminders = ref([])
const statistics = ref({ dates: [], weights: [], temperatures: [], avgWeight: 0, weightVolatility: 0, abnormalCount: 0 })

const petId = 1

const petAge = computed(() => {
  if (!petInfo.value.birthDate) return ''
  const birth = new Date(petInfo.value.birthDate)
  const now = new Date()
  const months = (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth())
  if (months < 12) return `${months}个月`
  return `${Math.floor(months / 12)}岁${months % 12 ? months % 12 + '个月' : ''}`
})

const chartOption = computed(() => {
  if (chartType.value === 'weight') {
    return {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
      xAxis: { type: 'category', data: statistics.value.dates, axisLabel: { fontSize: 10, color: '#999' } },
      yAxis: { type: 'value', axisLabel: { fontSize: 10, color: '#999' }, splitLine: { lineStyle: { color: '#f0f0f0' } } },
      series: [{ data: statistics.value.weights, type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, lineStyle: { color: '#FF6B6B', width: 2 }, itemStyle: { color: '#FF6B6B' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(255,107,107,0.3)' }, { offset: 1, color: 'rgba(255,107,107,0.02)' }] } } }]
    }
  } else {
    return {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 20, bottom: 30 },
      xAxis: { type: 'category', data: statistics.value.dates, axisLabel: { fontSize: 10, color: '#999' } },
      yAxis: { type: 'value', axisLabel: { fontSize: 10, color: '#999' }, splitLine: { lineStyle: { color: '#f0f0f0' } } },
      series: [{ data: statistics.value.temperatures, type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, lineStyle: { color: '#4ECDC4', width: 2 }, itemStyle: { color: '#4ECDC4' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(78,205,196,0.3)' }, { offset: 1, color: 'rgba(78,205,196,0.02)' }] } } }]
    }
  }
})

const getCareTypeColor = (type) => {
  const map = { FEEDING: 'dot-green', BATH: 'dot-blue', VACCINE: 'dot-orange', DEWORMING: 'dot-purple' }
  return map[type] || 'dot-green'
}

const formatReminderDate = (item) => {
  if (item.expired) return '已过期'
  const today = new Date()
  const date = new Date(item.reminderDate)
  if (date.toDateString() === today.toDateString()) return '今天'
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)
  if (date.toDateString() === tomorrow.toDateString()) return '明天'
  return item.reminderDate
}

const loadData = async () => {
  try {
    const [statsData, reminderData] = await Promise.all([
      getMonthlyStatistics(petId),
      getTodayReminders(petId)
    ])
    statistics.value = statsData || statistics.value
    reminders.value = reminderData || []
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

const goAddPet = () => {
  uni.navigateTo({ url: '/pages/pet-add/index' })
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
      script.onload = () => {
        this.initChart()
      }
      document.head.appendChild(script)
    } else if (typeof echarts !== 'undefined') {
      this.initChart()
    }
  },
  methods: {
    initChart() {
      const dom = document.getElementById('echarts-dom')
      if (dom) {
        myChart = echarts.init(dom)
      }
    },
    updateChart(newValue) {
      if (myChart && newValue) {
        myChart.setOption(newValue, true)
      }
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
.pet-card {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(255,107,107,0.3);
}
.pet-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}
.avatar-emoji {
  font-size: 48rpx;
}
.pet-info {
  flex: 1;
}
.pet-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  display: block;
}
.pet-breed {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
  display: block;
  margin-top: 4rpx;
}
.pet-age-tag {
  background: rgba(255,255,255,0.25);
  border-radius: 20rpx;
  padding: 8rpx 20rpx;
}
.age-text {
  font-size: 22rpx;
  color: #ffffff;
}
.add-pet-btn {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 16rpx;
}
.add-pet-icon {
  font-size: 40rpx;
  color: #ffffff;
  font-weight: 300;
}
.chart-card, .reminder-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}
.chart-tabs {
  display: flex;
  background: #f5f6fa;
  border-radius: 12rpx;
  padding: 4rpx;
}
.tab-item {
  padding: 8rpx 24rpx;
  font-size: 24rpx;
  color: #999999;
  border-radius: 8rpx;
}
.tab-active {
  background: #ffffff;
  color: #FF6B6B;
  font-weight: bold;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.08);
}
.chart-container {
  width: 100%;
  height: 400rpx;
}
.echarts-dom {
  width: 100%;
  height: 400rpx;
}
.reminder-count {
  font-size: 24rpx;
  color: #FF6B6B;
  font-weight: bold;
}
.empty-tip {
  padding: 40rpx 0;
  text-align: center;
}
.empty-text {
  color: #cccccc;
  font-size: 28rpx;
}
.reminder-list {
  display: flex;
  flex-direction: column;
}
.reminder-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.reminder-item:last-child {
  border-bottom: none;
}
.reminder-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  margin-right: 20rpx;
  flex-shrink: 0;
}
.dot-green { background: #4ECDC4; }
.dot-blue { background: #5B8FF9; }
.dot-orange { background: #FF9F43; }
.dot-purple { background: #A855F7; }
.reminder-content {
  flex: 1;
}
.reminder-title {
  font-size: 28rpx;
  color: #333333;
  display: block;
}
.reminder-date {
  font-size: 22rpx;
  color: #999999;
  display: block;
  margin-top: 4rpx;
}
.reminder-expired {
  opacity: 0.6;
}
.expired-tag {
  background: #FFF0F0;
  border-radius: 8rpx;
  padding: 4rpx 12rpx;
}
.expired-text {
  font-size: 22rpx;
  color: #FF6B6B;
}
</style>
