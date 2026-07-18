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
export const getMonthlyStatistics = (petId) => request({ url: '/statistics/monthly?petId=' + petId })
export const getTodayReminders = (petId) => request({ url: '/reminders/today?petId=' + petId })
export const getHealthRecords = (petId, careType) => {
  let url = '/health-records?petId=' + petId
  if (careType) url += '&careType=' + careType
  return request({ url })
}
export const getPetList = () => request({ url: '/list' })
export const addPet = (data) => request({ url: '/add', method: 'POST', data })
export const addReminder = (data) => request({ url: '/reminder/add', method: 'POST', data })
export const completeReminder = (id) => request({ url: '/reminder/complete/' + id, method: 'PUT' })
export const updateReminder = (id, data) => request({ url: '/reminder/update/' + id, method: 'PUT', data })
export const deleteReminder = (id) => request({ url: '/reminder/' + id, method: 'DELETE' })
