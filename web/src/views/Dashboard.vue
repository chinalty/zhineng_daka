<template>
  <div class="dashboard">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="dashboard-header">
        <div class="header-left">
          <h2>智能打卡系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo.realName || userInfo.username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px" class="dashboard-aside">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            @select="handleMenuSelect">
            <el-menu-item index="dashboard">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="attendance">
              <el-icon><Clock /></el-icon>
              <span>考勤打卡</span>
            </el-menu-item>
            <el-menu-item index="records">
              <el-icon><Document /></el-icon>
              <span>考勤记录</span>
            </el-menu-item>
            <el-menu-item index="leave">
              <el-icon><Calendar /></el-icon>
              <span>请假管理</span>
            </el-menu-item>
            <el-menu-item index="approval" v-if="userInfo.roleId !== 3">
              <el-icon><Check /></el-icon>
              <span>请假审批</span>
            </el-menu-item>
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        
        <!-- 主内容区 -->
        <el-main class="dashboard-main">
          <!-- 首页内容 -->
          <div v-if="activeMenu === 'dashboard'" class="content-section">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon">
                      <el-icon size="40"><Clock /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>今日考勤</h3>
                      <p>{{ todayAttendance.status || '未打卡' }}</p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon">
                      <el-icon size="40"><Calendar /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>本月请假</h3>
                      <p>{{ monthLeaveDays }} 天</p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon">
                      <el-icon size="40"><DataAnalysis /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>工作时长</h3>
                      <p>{{ monthWorkHours }} 小时</p>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>快速打卡</span>
                    </div>
                  </template>
                  <div class="quick-check-in">
                    <div class="time-display">{{ currentTime }}</div>
                    <div class="button-group">
                      <el-button 
                        v-if="userInfo.faceImageUrl"
                        type="success" 
                        size="large" 
                        @click="handleFaceCheckIn"
                        :disabled="!canCheckIn"
                        class="check-in-button face-button">
                        <el-icon><Camera /></el-icon>
                        {{ checkInButtonText }}
                      </el-button>
                      <el-button 
                        v-else
                        type="warning" 
                        size="large" 
                        @click="handleGoToProfile"
                        class="check-in-button">
                        <el-icon><User /></el-icon>
                        未注册人像
                      </el-button>
                    </div>
                    <p class="check-in-info" v-if="!todayAttendance.checkInTime">
                      弹性打卡时间：08:00-10:00
                    </p>
                    <p class="check-in-info" v-else-if="!todayAttendance.checkOutTime">
                      预期签退时间：{{ expectedCheckOutTime }}
                      <el-tag type="warning" size="small" style="margin-left: 10px;">
                        请在{{ expectedCheckOutTime }}前签退，否则将记为早退
                      </el-tag>
                    </p>
                    <p class="check-in-info" v-else>
                      今日考勤已完成
                    </p>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>最近考勤记录</span>
                    </div>
                  </template>
                  <el-table :data="recentRecords" style="width: 100%">
                    <el-table-column prop="date" label="日期" width="100"></el-table-column>
                    <el-table-column prop="checkIn" label="签到"></el-table-column>
                    <el-table-column prop="checkOut" label="签退"></el-table-column>
                    <el-table-column prop="status" label="状态">
                      <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">
                          {{ scope.row.status }}
                        </el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </el-col>
            </el-row>
          </div>
          
          <!-- 其他菜单内容占位 -->
          <div v-else class="content-section">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>{{ getMenuTitle(activeMenu) }}</span>
                </div>
              </template>
              <p>{{ getMenuTitle(activeMenu) }}功能正在开发中...</p>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
    
    <!-- 人脸识别打卡对话框 -->
    <el-dialog 
      v-model="faceDialogVisible" 
      title="人脸识别打卡"
      width="600px"
      @close="closeFaceDialog">
      <div class="face-check-in">
        <div class="camera-container">
          <video ref="videoRef" autoplay playsinline class="camera-video"></video>
          <canvas ref="canvasRef" style="display: none;"></canvas>
        </div>
        <div class="face-actions">
          <el-button 
            type="primary" 
            size="large" 
            @click="captureAndCheckIn"
            :loading="faceChecking">
            <el-icon><Camera /></el-icon>
            {{ faceChecking ? '识别中...' : '拍照打卡' }}
          </el-button>
          <p class="face-tip">请将面部对准摄像头，点击拍照完成打卡</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAPI, attendanceAPI, leaveAPI, testAPI } from '../services/api'
import { 
  User, 
  ArrowDown, 
  House, 
  Clock, 
  Document, 
  Calendar, 
  Check, 
  DataAnalysis,
  Camera
} from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: {
    User,
    ArrowDown,
    House,
    Clock,
    Document,
    Calendar,
    Check,
    DataAnalysis,
    Camera
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const activeMenu = ref('dashboard')
    const currentTime = ref(new Date().toLocaleTimeString('zh-CN'))
    const timeTimer = ref(null)
    
    // 人脸识别相关
    const faceDialogVisible = ref(false)
    const videoRef = ref(null)
    const canvasRef = ref(null)
    const faceChecking = ref(false)
    let mediaStream = null
    
    // 用户信息
    const userInfo = reactive({
      id: null,
      username: '',
      realName: '',
      email: '',
      roleId: null,
      departmentId: null,
      departmentName: '',
      faceImageUrl: null
    })
    
    // 今日考勤信息
    const todayAttendance = reactive({
      id: null,
      status: '未打卡',
      checkInTime: null,
      checkOutTime: null,
      workHours: 0,
      isLate: false,
      isEarlyLeave: false
    })
    
    // 统计数据
    const monthLeaveDays = ref(0)
    const monthWorkHours = ref(0)
    
    // 最近考勤记录
    const recentRecords = ref([])
    
    // 更新时间
    const updateTime = () => {
      currentTime.value = new Date().toLocaleTimeString('zh-CN')
    }
    
    // 菜单选择处理
    const handleMenuSelect = (key) => {
      activeMenu.value = key
      // 跳转到对应页面
      router.push(`/${key}`)
    }
    
    // 下拉菜单命令处理
    const handleCommand = (command) => {
      if (command === 'profile') {
        ElMessage.info('个人信息功能开发中')
      } else if (command === 'logout') {
        ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          localStorage.removeItem('token')
          router.push('/login')
          ElMessage.success('已退出登录')
        }).catch(() => {
          // 用户取消
        })
      }
    }
    
    // 打卡处理
    const handleCheckIn = async () => {
      try {
        // 判断是签到还是签退
        const type = !todayAttendance.checkInTime ? 1 : 2 // 1: 签到, 2: 签退
        
        const response = await attendanceAPI.checkIn(type)
        
        if (response.ok) {
          // 刷新今日考勤信息
          await loadTodayAttendance()
          
          const action = type === 1 ? '签到' : '签退'
          ElMessage.success(`${action}成功`)
        }
      } catch (error) {
        ElMessage.error(error.message || '打卡失败')
      }
    }
    
    // 人脸识别打卡
    const handleFaceCheckIn = async () => {
      faceDialogVisible.value = true
      
      // 打开摄像头
      try {
        mediaStream = await navigator.mediaDevices.getUserMedia({ 
          video: { width: 640, height: 480 } 
        })
        
        // 等待 DOM 更新
        setTimeout(() => {
          if (videoRef.value) {
            videoRef.value.srcObject = mediaStream
          }
        }, 100)
      } catch (error) {
        console.error('无法访问摄像头:', error)
        ElMessage.error('无法访问摄像头，请检查权限设置')
        faceDialogVisible.value = false
      }
    }
    
    // 跳转到个人中心
    const handleGoToProfile = () => {
      ElMessage.info('请先在个人中心上传人脸照片')
      router.push('/profile')
    }
    
    // 拍照并打卡
    const captureAndCheckIn = async () => {
      if (!videoRef.value || !canvasRef.value) {
        ElMessage.error('摄像头未就绪')
        return
      }
      
      faceChecking.value = true
      
      try {
        // 在 canvas 上绘制当前视频帧
        const canvas = canvasRef.value
        const video = videoRef.value
        canvas.width = video.videoWidth
        canvas.height = video.videoHeight
        const ctx = canvas.getContext('2d')
        ctx.drawImage(video, 0, 0)
        
        // 将 canvas 转换为 Blob
        const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg', 0.8))
        
        // 判断是签到还是签退
        const type = !todayAttendance.checkInTime ? 1 : 2
        
        // 上传识别
        const response = await attendanceAPI.checkInWithFace(blob, type)
        
        if (response.ok) {
          const action = type === 1 ? '签到' : '签退'
          ElMessage.success(`人脸识别${action}成功`)
          
          // 关闭对话框
          faceDialogVisible.value = false
          
          // 刷新今日考勤信息
          await loadTodayAttendance()
        } else {
          ElMessage.error(response.message || '人脸识别失败')
        }
      } catch (error) {
        console.error('人脸识别打卡失败:', error)
        ElMessage.error(error.message || '人脸识别打卡失败')
      } finally {
        faceChecking.value = false
      }
    }
    
    // 关闭人脸识别对话框
    const closeFaceDialog = () => {
      // 停止摄像头
      if (mediaStream) {
        mediaStream.getTracks().forEach(track => track.stop())
        mediaStream = null
      }
    }
    
    // 计算是否可以打卡
    const canCheckIn = computed(() => {
      // 允许全天签到，超过10点会标记为迟到
      if (!todayAttendance.checkInTime) {
        return true // 未签到时任何时间都可以签到
      } else if (!todayAttendance.checkOutTime) {
        return true // 已签到未签退时可以签退
      }
      return false // 已签到已签退
    })
    
    // 打卡按钮文字
    const checkInButtonText = computed(() => {
      if (!todayAttendance.checkInTime) {
        return '点击签到'
      } else if (!todayAttendance.checkOutTime) {
        return '点击签退'
      } else {
        return '今日已完成'
      }
    })
    
    // 预期签退时间
    const expectedCheckOutTime = computed(() => {
      if (todayAttendance.checkInTime && todayAttendance.expectedCheckOutTime) {
        const expectedTime = new Date(todayAttendance.expectedCheckOutTime)
        return expectedTime.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
      return ''
    })
    
    // 获取菜单标题
    const getMenuTitle = (menu) => {
      const titles = {
        dashboard: '首页',
        attendance: '考勤打卡',
        records: '考勤记录',
        leave: '请假管理',
        approval: '请假审批',
        profile: '个人中心'
      }
      return titles[menu] || menu
    }
    
    // 获取状态类型
    const getStatusType = (status) => {
      const types = {
        '正常': 'success',
        '迟到': 'warning',
        '早退': 'warning',
        '缺勤': 'danger'
      }
      return types[status] || 'info'
    }
    
    onMounted(() => {
      // 启动时间更新定时器
      timeTimer.value = setInterval(updateTime, 1000)
      
      // 根据当前路由设置 activeMenu
      const currentPath = route.path
      const pathToMenu = {
        '/dashboard': 'dashboard',
        '/attendance': 'attendance',
        '/records': 'records',
        '/leave': 'leave',
        '/approval': 'approval',
        '/profile': 'profile'
      }
      activeMenu.value = pathToMenu[currentPath] || 'dashboard'
      
      // 检查token状态
      const token = localStorage.getItem('token')
      console.log('Dashboard mounted - 当前token状态:', token ? '存在' : '不存在')
      if (token) {
        console.log('Token内容预览:', token.substring(0, 50) + '...')
        
        // 检查token是否过期（简单检查）
        try {
          const payload = JSON.parse(atob(token.split('.')[1]))
          const exp = payload.exp * 1000 // 转换为毫秒
          const now = Date.now()
          console.log('Token过期时间:', new Date(exp).toLocaleString())
          console.log('当前时间:', new Date(now).toLocaleString())
          console.log('Token是否过期:', now > exp ? '已过期' : '有效')
        } catch (e) {
          console.error('解析token失败:', e)
        }
      }
      
      // 加载所有数据
      loadDashboardData()
    })
    
    // 加载仪表板数据
    const loadDashboardData = async () => {
      try {
        await Promise.all([
          loadUserInfo(),
          loadTodayAttendance(),
          loadLeaveStats(),
          loadAttendanceStats()
        ])
      } catch (error) {
        console.error('加载仪表板数据失败:', error)
      }
    }
    
    onUnmounted(() => {
      // 清理定时器
      if (timeTimer.value) {
        clearInterval(timeTimer.value)
      }
    })
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        const response = await userAPI.getUserInfo()
        if (response.ok && response.data) {
          Object.assign(userInfo, response.data)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        // 从 token 中提取用户名作为降级方案
        try {
          const token = localStorage.getItem('token')
          if (token) {
            const payload = JSON.parse(atob(token.split('.')[1]))
            userInfo.username = payload.sub || '用户'
          }
        } catch (e) {
          console.error('解析token失败:', e)
          userInfo.username = '用户'
        }
        // 不显示错误提示，避免影响用户体验
      }
    }
    
    // 加载今日考勤信息
    const loadTodayAttendance = async () => {
      try {
        const response = await attendanceAPI.getTodayAttendance()
        if (response.ok && response.data) {
          Object.assign(todayAttendance, response.data)
          
          // 格式化时间显示
          if (todayAttendance.checkInTime) {
            const checkInDate = new Date(todayAttendance.checkInTime)
            todayAttendance.checkInTime = checkInDate.toLocaleTimeString('zh-CN', { 
              hour: '2-digit', 
              minute: '2-digit' 
            })
          }
          
          if (todayAttendance.checkOutTime) {
            const checkOutDate = new Date(todayAttendance.checkOutTime)
            todayAttendance.checkOutTime = checkOutDate.toLocaleTimeString('zh-CN', { 
              hour: '2-digit', 
              minute: '2-digit' 
            })
          }
          
          // 设置状态
          if (todayAttendance.checkInTime && todayAttendance.checkOutTime) {
            todayAttendance.status = '已完成'
          } else if (todayAttendance.checkInTime) {
            todayAttendance.status = '已签到'
          } else {
            todayAttendance.status = '未打卡'
          }
        }
      } catch (error) {
        console.error('加载今日考勤失败:', error)
        // 如果没有今日考勤记录，保持默认状态
      }
    }
    
    // 加载请假统计
    const loadLeaveStats = async () => {
      try {
        const response = await leaveAPI.getMyLeaveRecords()
        if (response.ok && response.data) {
          // 计算本月请假天数
          const currentMonth = new Date().getMonth()
          const currentYear = new Date().getFullYear()
          
          let monthDays = 0
          response.data.forEach(record => {
            const startDate = new Date(record.startDate)
            if (startDate.getMonth() === currentMonth && startDate.getFullYear() === currentYear) {
              monthDays += record.leaveDays || 1
            }
          })
          
          monthLeaveDays.value = monthDays
        }
      } catch (error) {
        console.error('加载请假统计失败:', error)
      }
    }
    
    // 加载考勤统计
    const loadAttendanceStats = async () => {
      try {
        const now = new Date()
        const currentMonth = now.getMonth()
        const currentYear = now.getFullYear()
        
        // 格式化为 YYYY-MM 格式
        const monthStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}`
        
        // 获取月度统计（包含总工作时长）
        try {
          const statsResponse = await attendanceAPI.getMonthlyStats(monthStr)
          if (statsResponse.ok && statsResponse.data) {
            monthWorkHours.value = statsResponse.data.totalHours || 0
          }
        } catch (statsError) {
          console.log('月度统计接口不可用，尝试从记录计算')
          
          // 如果月度统计接口不可用，从月度记录计算
          try {
            const recordsResponse = await attendanceAPI.getMonthlyRecords(monthStr)
            if (recordsResponse.ok && recordsResponse.data) {
              let totalHours = 0
              recordsResponse.data.forEach(record => {
                if (record.workHours) {
                  totalHours += parseFloat(record.workHours)
                }
              })
              monthWorkHours.value = totalHours
            }
          } catch (recordsError) {
            console.log('月度记录接口也不可用，使用默认值')
            monthWorkHours.value = 0
          }
        }
        
        // 获取最近考勤记录
        try {
          const recentResponse = await attendanceAPI.getRecentRecords(10)
          if (recentResponse.ok && recentResponse.data) {
            recentRecords.value = recentResponse.data.map(record => ({
              date: new Date(record.attendanceDate).toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }),
              checkIn: record.checkInTime ? new Date(record.checkInTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : '-',
              checkOut: record.checkOutTime ? new Date(record.checkOutTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : '-',
              status: record.isLate ? '迟到' : record.isEarlyLeave ? '早退' : record.status === 1 ? '正常' : '异常'
            }))
          }
        } catch (recentError) {
          console.log('最近考勤记录接口不可用')
          recentRecords.value = []
        }
        
      } catch (error) {
        console.error('加载考勤统计失败:', error)
      }
    }
    
    return {
      activeMenu,
      currentTime,
      userInfo,
      todayAttendance,
      monthLeaveDays,
      monthWorkHours,
      recentRecords,
      canCheckIn,
      checkInButtonText,
      expectedCheckOutTime,
      faceDialogVisible,
      videoRef,
      canvasRef,
      faceChecking,
      handleMenuSelect,
      handleCommand,
      handleFaceCheckIn,
      handleGoToProfile,
      captureAndCheckIn,
      closeFaceDialog,
      getMenuTitle,
      getStatusType,
      loadDashboardData
    }
  }
}
</script>

<style scoped>
.dashboard {
  height: 100vh;
}

.dashboard-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: white;
}

.user-info .el-icon {
  margin-right: 5px;
}

.dashboard-aside {
  background-color: #f5f5f5;
  border-right: 1px solid #e6e6e6;
}

.dashboard-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.content-section {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-content {
  display: flex;
  align-items: center;
}

.stats-icon {
  color: #409EFF;
  margin-right: 15px;
}

.stats-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.stats-info p {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.quick-check-in {
  text-align: center;
  padding: 20px 0;
}

.time-display {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 15px;
}

.check-in-button {
  padding: 12px 30px;
  font-size: 16px;
  flex: 1;
  max-width: 200px;
}

.face-button {
  margin-left: 0;
}

.check-in-info {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 人脸识别对话框 */
.face-check-in {
  text-align: center;
}

.camera-container {
  width: 100%;
  max-width: 640px;
  margin: 0 auto 20px;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
}

.camera-video {
  width: 100%;
  height: auto;
  display: block;
}

.face-actions {
  text-align: center;
}

.face-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 10px;
  }
  
  .header-left h2 {
    font-size: 18px;
  }
  
  .dashboard-aside {
    width: 64px !important;
  }
  
  .dashboard-main {
    padding: 10px;
  }
  
  .stats-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stats-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>