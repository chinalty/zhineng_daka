// API服务模块
const API_BASE_URL = 'http://localhost:8080/api'

// 获取token
const getToken = () => localStorage.getItem('token')

// 通用请求函数
const request = async (url, options = {}) => {
  const token = getToken()
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    },
    ...options
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)
    const data = await response.json()
    
    if (!response.ok) {
      throw new Error(data.message || '请求失败')
    }
    
    return data
  } catch (error) {
    console.error('API请求错误:', error)
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
  
  // 获取部门员工列表
  getDepartmentUsers: () => request('/user/department-users'),
  
  // 获取所有部门
  getDepartments: () => request('/user/departments'),
  
  // 获取所有角色
  getRoles: () => request('/user/roles')
}

// 考勤相关API
export const attendanceAPI = {
  // 打卡（签到/签退）
  checkIn: (type) => request('/attendance/check', {
    method: 'POST',
    body: JSON.stringify({ type })
  }),
  
  // 获取今日考勤记录
  getTodayAttendance: () => request('/attendance/today'),
  
  // 获取月度考勤记录
  getMonthlyRecords: (month) => request(`/attendance/monthly-records?month=${month}`),
  
  // 获取最近考勤记录
  getRecentRecords: (limit = 10) => request(`/attendance/recent-records?limit=${limit}`),
  
  // 获取月度统计
  getMonthlyStats: (month) => request(`/attendance/monthly-stats?month=${month}`)
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
  })
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