<template>
  <view class="container">
    <view class="form-card">
      <!-- 头像预览 -->
      <view class="avatar-section">
        <view class="avatar-circle">
          <text class="avatar-emoji">{{ getSpeciesEmoji(formData.species) }}</text>
        </view>
        <text class="avatar-tip">点击下方选择宠物种类</text>
      </view>

      <!-- 宠物名称 -->
      <view class="form-group">
        <text class="form-label">宠物名称</text>
        <input class="form-input" v-model="formData.name" type="text" placeholder="请输入宠物名称" maxlength="20" />
      </view>
	  
	  

      <!-- 种类选择 -->
      <view class="form-group">
        <text class="form-label">宠物种类</text>
        <view class="species-selector">
          <view
            v-for="item in speciesList"
            :key="item.value"
            :class="['species-item', formData.species === item.value ? 'species-selected' : '']"
            @click="formData.species = item.value"
          >
            <text class="species-emoji">{{ item.emoji }}</text>
            <text :class="['species-name', formData.species === item.value ? 'species-name-selected' : '']">{{ item.label }}</text>
          </view>
        </view>
      </view>

      <!-- 品种 -->
      <view class="form-group">
        <text class="form-label">品种</text>
        <input class="form-input" v-model="formData.breed" type="text" placeholder="如：橘猫、金毛、柯基" maxlength="30" />
      </view>

      <!-- 性别 -->
      <view class="form-group">
        <text class="form-label">性别</text>
        <view class="gender-selector">
          <view :class="['gender-item', formData.gender === '公' ? 'gender-selected-male' : '']" @click="formData.gender = '公'">
            <text class="gender-emoji">♂</text>
            <text :class="['gender-name', formData.gender === '公' ? 'gender-name-selected' : '']">公</text>
          </view>
          <view :class="['gender-item', formData.gender === '母' ? 'gender-selected-female' : '']" @click="formData.gender = '母'">
            <text class="gender-emoji">♀</text>
            <text :class="['gender-name', formData.gender === '母' ? 'gender-name-selected' : '']">母</text>
          </view>
        </view>
      </view>

      <!-- 出生日期 -->
      <view class="form-group">
        <text class="form-label">出生日期</text>
        <picker mode="date" :value="formData.birthDate" :end="today" @change="onDateChange">
          <view class="form-picker">
            <text :class="['picker-text', formData.birthDate ? '' : 'picker-placeholder']">
              {{ formData.birthDate || '请选择出生日期' }}
            </text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-btn" :class="{ 'btn-disabled': !canSubmit }" @click="submitForm">
      <text class="btn-text">保存宠物信息</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { addPet } from '../../utils/api.js'

const today = new Date().toISOString().slice(0, 10)

const speciesList = [
  { value: '猫', label: '猫咪', emoji: '🐱' },
  { value: '狗', label: '狗狗', emoji: '🐶' },
  { value: '兔', label: '兔子', emoji: '🐰' },
  { value: '鸟', label: '鸟类', emoji: '🐦' },
  { value: '鱼', label: '鱼类', emoji: '🐟' },
  { value: '其他', label: '其他', emoji: '🐾' }
]

const formData = ref({
  name: '',
  species: '猫',
  breed: '',
  gender: '公',
  birthDate: ''
})

const canSubmit = computed(() => {
  return formData.value.name && formData.value.species && formData.value.breed
})

const getSpeciesEmoji = (species) => {
  const item = speciesList.find(s => s.value === species)
  return item ? item.emoji : '🐾'
}

const onDateChange = (e) => {
  formData.value.birthDate = e.detail.value
}

const submitForm = async () => {
  if (!canSubmit.value) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  try {
    await addPet(formData.value)
    uni.showToast({ title: '添加成功', icon: 'success' })
    // 通知首页刷新宠物列表
    uni.$emit('petAdded')
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (e) {
    uni.showToast({ title: e || '添加失败', icon: 'none' })
  }
}
</script>

<style scoped>
.container {
  padding: 24rpx;
  background-color: #f5f6fa;
  min-height: 100vh;
}
.form-card {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 36rpx 28rpx;
  margin-bottom: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06);
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 36rpx;
}
.avatar-circle {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFF0F0, #FFE0E0);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
  border: 4rpx solid #FF6B6B;
}
.avatar-emoji { font-size: 72rpx; }
.avatar-tip { font-size: 22rpx; color: #cccccc; }
.form-group { margin-bottom: 28rpx; }
.form-label {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 16rpx;
  display: block;
}
.form-input {
  width: 100%;
  height: 88rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
  background: #fafafa;
}
.species-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.species-item {
  width: calc(33.33% - 11rpx);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  background: #fafafa;
}
.species-selected {
  border-color: #FF6B6B;
  background: #FFF5F5;
}
.species-emoji { font-size: 40rpx; margin-bottom: 8rpx; }
.species-name { font-size: 24rpx; color: #999; }
.species-name-selected { color: #FF6B6B; font-weight: bold; }
.gender-selector { display: flex; gap: 24rpx; }
.gender-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24rpx 0;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  background: #fafafa;
}
.gender-selected-male {
  border-color: #5B8FF9;
  background: #EBF0FF;
}
.gender-selected-female {
  border-color: #FF6B6B;
  background: #FFF0F0;
}
.gender-emoji { font-size: 32rpx; margin-right: 12rpx; }
.gender-selected-male .gender-emoji { color: #5B8FF9; }
.gender-selected-female .gender-emoji { color: #FF6B6B; }
.gender-name { font-size: 28rpx; color: #999; }
.gender-name-selected { font-weight: bold; color: #333; }
.form-picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 88rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  padding: 0 24rpx;
  background: #fafafa;
}
.picker-text { font-size: 28rpx; color: #333; }
.picker-placeholder { color: #cccccc; }
.picker-arrow { font-size: 28rpx; color: #cccccc; }
.submit-btn {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
  border-radius: 16rpx;
  padding: 28rpx 0;
  text-align: center;
  box-shadow: 0 8rpx 24rpx rgba(255,107,107,0.3);
}
.btn-disabled {
  background: #cccccc;
  box-shadow: none;
}
.btn-text { font-size: 32rpx; color: #ffffff; font-weight: bold; }
</style>
