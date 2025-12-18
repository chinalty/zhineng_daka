// API服务模块
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// 获取token
const getToken = () => localStorage.getItem('token')

// 通用请求函数
const request = async (url, options = {}) => {
  const token = getToken()
  
  console.log(`当前Token: ${token ? '存在' : '不存在'}`)
  if (token) {
    console.log(`Token内容: ${token.substring(0, 20)}...`)
  }
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    },
    ...options
  }
  
  try {
    console.log(`API请求: ${API_BASE_URL}${url}`, config)
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    
    // 先检查响应状态
    console.log(`响应状态: ${response.status} ${response.statusText}`)
    
    let data
    try {
      data = await response.json()
      console.log(`API响应: ${API_BASE_URL}${url}`, data)
    } catch (parseError) {
      console.error('解析响应JSON失败:', parseError)
      const responseText = await response.text()
      console.error('响应原文:', responseText)
      throw new Error('服务器响应格式错误')
    }
    
    if (!response.ok) {
      // 如果是401错误，清除本地token并跳转到登录页
      if (response.status === 401) {
        console.log('401错误，清除token并跳转登录页')
        localStorage.removeItem('token')
        window.location.href = '/login'
        throw new Error('登录已过期，请重新登录')
      }
      throw new Error(data.message || `请求失败 (${response.status})`)
    }
    
    return data
  } catch (error) {
    console.error('API请求错误:', error)
    console.error('请求URL:', `${API_BASE_URL}${url}`)
    console.error('请求配置:', config)
    throw error
  }
}

// 认证相关API
export const authAPI = {
  // 登录
  login: (credentials) => request('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials)
  }),
  
  // 注册
  register: (userData) => request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(userData)
  }),
  
  // 发送验证码
  sendCaptcha: (email) => request(`/auth/send-captcha?email=${email}`, {
    method: 'POST'
  })
}

// 用户相关API
export const userAPI = {
  // 获取用户信息
  getUserInfo: () => request('/user/info'),
  
  // 更新个人信息
  updateProfile: (profileData) => request('/user/update-profile', {
    method: 'PUT',
    body: JSON.stringify(profileData)
  }),
  
  // 修改密码
  changePassword: (passwordData) => request('/user/change-password', {
    method: 'PUT',
    body: JSON.stringify(passwordData)
  }),
  
  // 获取用户统计数据
  getUserStats: () => request('/user/stats'),
  
  // 获取部门员工列表
  getDepartmentUsers: () => request('/user/department-users'),
  
  // 获取所有部门
  getDepartments: () => request('/user/departments'),
  
  // 获取所有角色
  getRoles: () => request('/user/roles'),
  
  // 上传人脸照片
  uploadFaceImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    
    const token = getToken()
    return fetch(`${API_BASE_URL}/user/upload-face`, {
      method: 'POST',
      headers: {
        ...(token && { 'Authorization': `Bearer ${token}` })
      },
      body: formData
    }).then(response => response.json())
  }
}

// 考勤相关API
export const attendanceAPI = {
  // 打卡（签到/签退）
  checkIn: (type) => request('/attendance/check', {
    method: 'POST',
    body: JSON.stringify({ type })
  }),
  
  // 人脸识别打卡
  checkInWithFace: (file, type) => {
    const formData = new FormData()
    formData.append('faceImage', file)  // 后端期望的参数名是 faceImage
    formData.append('type', type)
    
    const token = getToken()
    return fetch(`${API_BASE_URL}/attendance/check-with-face`, {
      method: 'POST',
      headers: {
        ...(token && { 'Authorization': `Bearer ${token}` })
      },
      body: formData
    }).then(response => response.json())
  },
  
  // 获取今日考勤记录
  getTodayAttendance: () => request('/attendance/today'),
  
  // 获取月度考勤记录
  getMonthlyRecords: (month) => request(`/attendance/monthly-records?month=${month}`),
  
  // 获取最近考勤记录
  getRecentRecords: (limit = 10) => request(`/attendance/recent-records?limit=${limit}`),
  
  // 获取月度统计
  getMonthlyStats: (month) => request(`/attendance/monthly-stats?month=${month}`),
  
  // 补卡相关接口
  getSupplementRecords: () => request('/attendance/supplement-records'),
  
  applySupplement: (supplementData) => request('/attendance/supplement-apply', {
    method: 'POST',
    body: JSON.stringify(supplementData)
  }),
  
  cancelSupplement: (id) => request(`/attendance/supplement-apply/${id}`, {
    method: 'DELETE'
  }),
  
  approveSupplement: (approvalData) => request('/attendance/supplement-approve', {
    method: 'POST',
    body: JSON.stringify(approvalData)
  }),
  
  getPendingSupplements: () => request('/attendance/supplement-pending')
}

// 请假相关API
export const leaveAPI = {
  // 申请请假
  applyLeave: (leaveData) => request('/leave/apply', {
    method: 'POST',
    body: JSON.stringify(leaveData)
  }),
  
  // 获取我的请假记录
  getMyLeaveRecords: () => request('/leave/my-records'),
  
  // 获取待审批列表
  getPendingApprovals: () => request('/leave/pending-approvals'),
  
  // 获取部门所有请假记录（部门经理用）
  getDepartmentLeaveRecords: () => request('/leave/department-records'),
  
  // 审批请假
  approveLeave: (approvalData) => request('/leave/approve', {
    method: 'POST',
    body: JSON.stringify(approvalData)
  }),
  
  // 获取请假详情
  getLeaveDetail: (id) => request(`/leave/${id}`)
}

// 测试相关API
export const testAPI = {
  // 查看验证码缓存
  getCaptchaCache: () => request('/test/captcha-cache'),
  
  // 测试验证码验证
  testCaptcha: (email, code) => request(`/test/test-captcha?email=${email}&code=${code}`, {
    method: 'POST'
  }),
  
  // 测试token信息
  getTokenInfo: () => request('/test/token-info')
}

// 调试相关API
export const debugAPI = {
  // 手动设置验证码
  setCaptcha: (email, code) => request(`/debug/set-captcha?email=${email}&code=${code}`, {
    method: 'POST'
  }),
  
  // 测试邮件发送
  testEmail: (email, code) => request(`/debug/test-email?email=${email}&code=${code}`, {
    method: 'POST'
  }),
  
  // 检查验证码缓存
  checkCache: (email) => request(`/debug/check-cache?email=${email}`)
}