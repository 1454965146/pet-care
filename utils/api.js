const BASE_URL = 'http://localhost:8080/api/pet'

const request = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          resolve(res.data.data)
        } else {
          reject(res.data.message || '请求失败')
        }
      },
      fail: (err) => {
        reject(err.errMsg || '网络错误')
      }
    })
  })
}

export const addHealthRecord = (data) => request({ url: '/health-record', method: 'POST', data })
export const getMonthlyStatistics = (petId) => request({ url: '/statistics/monthly', data: { petId } })
export const getTodayReminders = (petId) => request({ url: '/reminders/today', data: { petId } })
export const getHealthRecords = (petId, careType) => request({ url: '/health-records', data: { petId, careType } })
export const getPetList = () => request({ url: '/list' })
export const addPet = (data) => request({ url: '/add', method: 'POST', data })
export const addReminder = (data) => request({ url: '/reminder/add', method: 'POST', data })
