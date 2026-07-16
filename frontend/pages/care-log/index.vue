<template>
  <view class="container">
    <!-- 标签筛选 -->
    <scroll-view scroll-x class="filter-bar">
      <view v-for="tag in filterTags" :key="tag.value" :class="['filter-tag', activeFilter === tag.value ? 'filter-active' : '']" @click="activeFilter = tag.value">
        <text :class="['filter-text', activeFilter === tag.value ? 'filter-text-active' : '']">{{ tag.label }}</text>
      </view>
    </scroll-view>

    <!-- 时间线列表 -->
    <view v-if="filteredRecords.length === 0" class="empty-state">
      <text class="empty-emoji">📋</text>
      <text class="empty-text">暂无护理记录</text>
      <text class="empty-sub">点击下方按钮添加第一条记录</text>
    </view>
    <view v-else class="timeline">
      <view v-for="(record, index) in filteredRecords" :key="record.id" class="timeline-item">
        <view class="timeline-line-wrapper">
          <view :class="['timeline-dot', getDotClass(record.careTypeName)]"></view>
          <view v-if="index < filteredRecords.length - 1" class="timeline-line"></view>
        </view>
        <view class="timeline-card">
          <view class="card-top">
            <view :class="['care-tag', getTagClass(record.careTypeName)]">
              <text class="tag-text">{{ getCareLabel(record.careTypeName) }}</text>
            </view>
            <text class="record-date">{{ record.recordDate }}</text>
          </view>
          <view v-if="record.weight || record.temperature" class="card-data">
            <view v-if="record.weight" class="data-item">
              <text class="data-label">体重</text>
              <text class="data-value">{{ record.weight }} kg</text>
            </view>
            <view v-if="record.temperature" class="data-item">
              <text class="data-label">体温</text>
              <text class="data-value">{{ record.temperature }} ℃</text>
            </view>
          </view>
          <view v-if="record.mentalStateName" class="mental-state">
            <text class="state-label">精神状态：</text>
            <text :class="['state-value', record.mentalStateName === 'ILL' ? 'state-ill' : '']">{{ getMentalLabel(record.mentalStateName) }}</text>
          </view>
          <view v-if="record.note" class="card-note">
            <text class="note-text">{{ record.note }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 新增按钮 -->
    <view class="fab-btn" @click="showAddModal = true">
      <text class="fab-icon">+</text>
    </view>

    <!-- 新增弹窗 -->
    <view v-if="showAddModal" class="modal-mask" @click="showAddModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">新增护理记录</text>
          <text class="modal-close" @click="showAddModal = false">✕</text>
        </view>
        <view class="form-group">
          <text class="form-label">护理类型</text>
          <view class="type-selector">
            <view v-for="t in careTypes" :key="t.value" :class="['type-item', formData.careType === t.value ? 'type-selected' : '']" @click="formData.careType = t.value">
              <text class="type-emoji">{{ t.emoji }}</text>
              <text :class="['type-name', formData.careType === t.value ? 'type-name-selected' : '']">{{ t.label }}</text>
            </view>
          </view>
        </view>
        <view class="form-row">
          <view class="form-group half">
            <text class="form-label">体重(kg)</text>
            <input class="form-input" v-model="formData.weight" type="digit" placeholder="选填" />
          </view>
          <view class="form-group half">
            <text class="form-label">体温(℃)</text>
            <input class="form-input" v-model="formData.temperature" type="digit" placeholder="选填" />
          </view>
        </view>
        <view class="form-group">
          <text class="form-label">精神状态</text>
          <view class="state-selector">
            <view v-for="s in mentalStates" :key="s.value" :class="['state-item', formData.mentalState === s.value ? 'state-selected' : '']" @click="formData.mentalState = s.value">
              <text :class="['state-name', formData.mentalState === s.value ? 'state-name-selected' : '']">{{ s.label }}</text>
            </view>
          </view>
        </view>
        <view class="form-group">
          <text class="form-label">备注</text>
          <textarea class="form-textarea" v-model="formData.note" placeholder="选填" maxlength="200" />
        </view>
        <view class="form-btn" @click="submitRecord">
          <text class="btn-text">保存记录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { addHealthRecord } from '../../utils/api.js'

const petId = 1
const activeFilter = ref('')
const records = ref([])
const showAddModal = ref(false)

const filterTags = [
  { label: '全部', value: '' },
  { label: '🍚 喂食', value: 'FEEDING' },
  { label: '🛁 洗澡', value: 'BATH' },
  { label: '💉 疫苗', value: 'VACCINE' },
  { label: '💊 驱虫', value: 'DEWORMING' }
]

const careTypes = [
  { value: 'FEEDING', label: '喂食', emoji: '🍚' },
  { value: 'BATH', label: '洗澡', emoji: '🛁' },
  { value: 'VACCINE', label: '疫苗', emoji: '💉' },
  { value: 'DEWORMING', label: '驱虫', emoji: '💊' }
]

const mentalStates = [
  { value: 'ACTIVE', label: '活跃' },
  { value: 'NORMAL', label: '正常' },
  { value: 'QUIET', label: '安静' },
  { value: 'ILL', label: '不适' }
]

const formData = ref({
  careType: 'FEEDING',
  weight: '',
  temperature: '',
  mentalState: 'NORMAL',
  note: ''
})

const filteredRecords = computed(() => {
  if (!activeFilter.value) return records.value
  return records.value.filter(r => r.careTypeName === activeFilter.value)
})

const getDotClass = (type) => {
  const map = { FEEDING: 'dot-green', BATH: 'dot-blue', VACCINE: 'dot-orange', DEWORMING: 'dot-purple' }
  return map[type] || 'dot-green'
}

const getTagClass = (type) => {
  const map = { FEEDING: 'tag-green', BATH: 'tag-blue', VACCINE: 'tag-orange', DEWORMING: 'tag-purple' }
  return map[type] || 'tag-green'
}

const getCareLabel = (type) => {
  const map = { FEEDING: '喂食', BATH: '洗澡', VACCINE: '疫苗', DEWORMING: '驱虫' }
  return map[type] || type
}

const getMentalLabel = (state) => {
  const map = { ACTIVE: '活跃', NORMAL: '正常', QUIET: '安静', ILL: '不适' }
  return map[state] || state
}

const submitRecord = async () => {
  try {
    const data = {
      petId,
      careType: formData.value.careType,
      weight: formData.value.weight ? parseFloat(formData.value.weight) : null,
      temperature: formData.value.temperature ? parseFloat(formData.value.temperature) : null,
      mentalState: formData.value.mentalState,
      note: formData.value.note,
      recordDate: new Date().toISOString().slice(0, 10)
    }
    const result = await addHealthRecord(data)
    records.value.unshift(result)
    showAddModal.value = false
    formData.value = { careType: 'FEEDING', weight: '', temperature: '', mentalState: 'NORMAL', note: '' }
    uni.showToast({ title: '记录成功', icon: 'success' })
  } catch (e) {
    uni.showToast({ title: e || '添加失败', icon: 'none' })
  }
}

const loadRecords = async () => {
  try {
    const res = await uni.request({
      url: 'http://localhost:8080/api/pet/health-records',
      data: { petId }
    })
    if (res.statusCode === 200 && res.data.code === 200) {
      records.value = res.data.data || []
    }
  } catch (e) {
    console.error('加载记录失败', e)
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.container {
  padding: 24rpx;
  background-color: #f5f6fa;
  min-height: 100vh;
  padding-bottom: 120rpx;
}
.filter-bar {
  white-space: nowrap;
  margin-bottom: 24rpx;
}
.filter-tag {
  display: inline-flex;
  padding: 12rpx 28rpx;
  border-radius: 32rpx;
  background: #ffffff;
  margin-right: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.filter-active {
  background: #FF6B6B;
  box-shadow: 0 4rpx 12rpx rgba(255,107,107,0.3);
}
.filter-text {
  font-size: 26rpx;
  color: #666666;
}
.filter-text-active {
  color: #ffffff;
  font-weight: bold;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120rpx;
}
.empty-emoji { font-size: 80rpx; margin-bottom: 20rpx; }
.empty-text { font-size: 30rpx; color: #999; margin-bottom: 8rpx; }
.empty-sub { font-size: 24rpx; color: #cccccc; }
.timeline { padding-left: 8rpx; }
.timeline-item {
  display: flex;
  min-height: 120rpx;
}
.timeline-line-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40rpx;
  margin-right: 20rpx;
}
.timeline-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 12rpx;
}
.dot-green { background: #4ECDC4; }
.dot-blue { background: #5B8FF9; }
.dot-orange { background: #FF9F43; }
.dot-purple { background: #A855F7; }
.timeline-line {
  width: 2rpx;
  flex: 1;
  background: #e8e8e8;
  margin-top: 8rpx;
}
.timeline-card {
  flex: 1;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.care-tag {
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}
.tag-green { background: #E8F8F5; }
.tag-blue { background: #EBF0FF; }
.tag-orange { background: #FFF3E0; }
.tag-purple { background: #F3E8FF; }
.tag-text { font-size: 22rpx; font-weight: bold; }
.tag-green .tag-text { color: #4ECDC4; }
.tag-blue .tag-text { color: #5B8FF9; }
.tag-orange .tag-text { color: #FF9F43; }
.tag-purple .tag-text { color: #A855F7; }
.record-date { font-size: 22rpx; color: #999999; }
.card-data {
  display: flex;
  gap: 32rpx;
  margin-bottom: 12rpx;
}
.data-item { display: flex; align-items: baseline; }
.data-label { font-size: 22rpx; color: #999; margin-right: 8rpx; }
.data-value { font-size: 28rpx; color: #333; font-weight: bold; }
.mental-state { margin-bottom: 8rpx; }
.state-label { font-size: 24rpx; color: #999; }
.state-value { font-size: 24rpx; color: #333; }
.state-ill { color: #FF6B6B; font-weight: bold; }
.card-note {
  background: #f9f9f9;
  border-radius: 8rpx;
  padding: 12rpx;
  margin-top: 8rpx;
}
.note-text { font-size: 24rpx; color: #666; }
.fab-btn {
  position: fixed;
  bottom: 180rpx;
  right: 40rpx;
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255,107,107,0.4);
  z-index: 100;
}
.fab-icon { font-size: 48rpx; color: #ffffff; font-weight: 300; }
.modal-mask {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}
.modal-content {
  width: 100%;
  background: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 40rpx;
  max-height: 80vh;
  overflow-y: auto;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}
.modal-title { font-size: 34rpx; font-weight: bold; color: #333; }
.modal-close { font-size: 36rpx; color: #999; padding: 8rpx; }
.form-group { margin-bottom: 28rpx; }
.form-group.half { flex: 1; }
.form-row { display: flex; gap: 24rpx; }
.form-label { font-size: 26rpx; color: #666; margin-bottom: 12rpx; display: block; }
.form-input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}
.type-selector {
  display: flex;
  gap: 16rpx;
}
.type-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
}
.type-selected {
  border-color: #FF6B6B;
  background: #FFF5F5;
}
.type-emoji { font-size: 36rpx; margin-bottom: 4rpx; }
.type-name { font-size: 22rpx; color: #999; }
.type-name-selected { color: #FF6B6B; font-weight: bold; }
.state-selector { display: flex; gap: 12rpx; flex-wrap: wrap; }
.state-item {
  padding: 12rpx 28rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 24rpx;
}
.state-selected { border-color: #4ECDC4; background: #E8F8F5; }
.state-name { font-size: 24rpx; color: #999; }
.state-name-selected { color: #4ECDC4; font-weight: bold; }
.form-textarea {
  width: 100%;
  height: 120rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 12rpx;
  padding: 16rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}
.form-btn {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  border-radius: 16rpx;
  padding: 24rpx 0;
  text-align: center;
  margin-top: 20rpx;
}
.btn-text { font-size: 32rpx; color: #ffffff; font-weight: bold; }
</style>
