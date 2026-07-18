// 全局宠物状态管理
import { reactive } from 'vue'

export const petStore = reactive({
  currentPetId: null,
  currentPet: {},
  petList: []
})

export const setCurrentPet = (pet) => {
  petStore.currentPetId = pet.id
  petStore.currentPet = pet
  // 通知其他页面更新
  uni.$emit('petChanged', pet)
}
