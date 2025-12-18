<template>
  <div class="attendance">
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
          <div class="attendance-content">
            <!-- 打卡区域 -->
            <el-row :gutter="20">
              <el-col :span="12">
                <el-card class="check-in-card">
                  <template #header>
                    <div class="card-header">
                      <span>快速打卡</span>
                    </div>
                  </template>
                  <div class="check-in-section">
                    <div class="time-display">{{ currentTime }}</div>
                    <div class="date-display">{{ currentDate }}</div>
                    
                    <!-- 打卡按钮组 -->
                    <div class="button-group">
                      <el-button 
                        v-if="!userInfo.faceImageUrl"
                        type="warning" 
                        size="large" 
                        @click="handleNoFaceImage"
                        class="check-in-button">
                        <el-icon><Warning /></el-icon>
                        未注册人像
                      </el-button>
                      
                      <el-button 
                        v-else-if="canCheckIn"
                        type="success" 
                        size="large" 
                        @click="handleFaceCheckIn"
                        class="check-in-button">
                        <el-icon><Camera /></el-icon>
                        {{ checkInButtonText }}
                      </el-button>
                      
                      <el-button 
                        v-else
                        type="info" 
                        size="large" 
                        disabled
                        class="check-in-button">
                        <el-icon><Check /></el-icon>
                        {{ checkInButtonText }}
                      </el-button>
                    </div>
                    
                    <!-- 打卡信息 -->
                    <div class="check-in-info">
                      <div v-if="!todayAttendance.checkInTime" class="info-item">
                        <el-tag type="info">弹性打卡时间：08:00-10:00</el-tag>
                      </div>
                      <div v-else-if="!todayAttendance.checkOutTime" class="info-item">
                        <el-tag type="warning">
                          预期签退时间：{{ expectedCheckOutTime }}
                        </el-tag>
                      </div>
                      <div v-else class="info-item">
                        <el-tag type="success">今日考勤已完成</el-tag>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              
              <el-col :span="12">
                <el-card class="status-card">
                  <template #header>
                    <div class="card-header">
                      <span>今日状态</span>
                    </div>
                  </template>
                  <div class="status-section">
                    <div class="status-item">
                      <div class="status-label">签到时间</div>
                      <div class="status-value">
                        {{ todayAttendance.checkInTime || '未签到' }}
                      </div>
                    </div>
                    <div class="status-item">
                      <div class="status-label">签退时间</div>
                      <div class="status-value">
                        {{ todayAttendance.checkOutTime || '未签退' }}
                      </div>
                    </div>
                    <div class="status-item">
                      <div class="status-label">工作时长</div>
                      <div class="status-value">
                        {{ todayAttendance.workHours || '0' }} 小时
                      </div>
                    </div>
                    <div class="status-item">
                      <div class="status-label">考勤状态</div>
                      <div class="status-value">
                        <el-tag :type="getStatusTagType()">
                          {{ getAttendanceStatus() }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 申请补卡区域 -->
            <el-card class="apply-card">
              <template #header>
                <div class="card-header">
                  <span>申请补卡</span>
                  <div>
                    <el-button 
                      type="info" 
                      size="small" 
                      @click="loadApplyRecords"
                      style="margin-right: 10px;">
                      <el-icon><Refresh /></el-icon>
                      刷新
                    </el-button>
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="showApplyDialog = true">
                      <el-icon><Plus /></el-icon>
                      申请补卡
                    </el-button>
                  </div>
                </div>
              </template>
              
              <!-- 补卡申请列表 -->
                          <el-table 
                            :data="applyRecords" 
                            style="width: 100%"
                            v-loading="applyLoading">
                              <el-table-column prop="attendanceDate" label="补卡日期" width="120">
                                <template #default="scope">
                                  {{ formatDate(scope.row.attendanceDate) }}
                                </template>
                              </el-table-column>
                              <el-table-column label="补卡时间" width="120">
                                <template #default="scope">
                                  <span v-if="scope.row.checkType === 1">
                                    {{ scope.row.checkInTime ? formatTime(scope.row.checkInTime) : '-' }}
                                  </span>
                                  <span v-else>
                                    {{ scope.row.checkOutTime ? formatTime(scope.row.checkOutTime) : '-' }}
                                  </span>
                                </template>
                              </el-table-column>
                              <el-table-column label="补卡类型" width="100">
                                <template #default="scope">
                                  <el-tag :type="getSupplementType(scope.row) === '签到' ? 'primary' : 'success'">
                                    {{ getSupplementType(scope.row) }}
                                  </el-tag>
                                </template>
                              </el-table-column>
                              <el-table-column prop="supplementReason" label="申请原因" min-width="200" show-overflow-tooltip />
                              <el-table-column prop="supplementStatus" label="状态" width="100">
                                <template #default="scope">
                                  <el-tag :type="getApplyStatusType(scope.row.supplementStatus)">
                                    {{ getApplyStatusText(scope.row.supplementStatus) }}
                                  </el-tag>
                                </template>
                              </el-table-column>
                              <el-table-column prop="createdAt" label="申请时间" width="150">
                                <template #default="scope">
                                  {{ formatDateTime(scope.row.createdAt) }}
                                </template>
                              </el-table-column>
                              <el-table-column label="操作" width="120" fixed="right">
                                <template #default="scope">
                                  <el-button 
                                    v-if="scope.row.supplementStatus === 0"
                                    type="danger" 
                                    size="small"
                                    @click="handleCancelApply(scope.row)">
                                    撤销
                                  </el-button>
                                  <span v-else>-</span>
                                </template>
                              </el-table-column>
                            </el-table>            </el-card>
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
    
    <!-- 申请补卡对话框 -->
    <el-dialog 
      v-model="showApplyDialog" 
      title="申请补卡"
      width="500px">
      <el-form 
        :model="applyForm" 
        :rules="applyRules" 
        ref="applyFormRef" 
        label-width="100px">
        <el-form-item label="补卡日期" prop="targetDate">
          <el-date-picker
            v-model="applyForm.targetDate"
            type="date"
            placeholder="选择补卡日期"
            :disabled-date="disabledDate"
            style="width: 100%;" />
        </el-form-item>
        <el-form-item label="补卡类型" prop="checkType">
          <el-radio-group v-model="applyForm.checkType">
            <el-radio :label="1">签到</el-radio>
            <el-radio :label="2">签退</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="补卡时间" prop="checkTime">
          <el-time-picker
            v-model="applyForm.checkTime"
            placeholder="选择补卡时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%;" />
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input 
            v-model="applyForm.reason" 
            type="textarea" 
            :rows="3"
            placeholder="请输入申请原因">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApplyDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitApply"
          :loading="submitting">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAPI, attendanceAPI } from '../services/api'
import { 
  User, 
  ArrowDown, 
  House, 
  Clock, 
  Document, 
  Calendar, 
  Check,
  Camera,
  Warning,
  Plus,
  Refresh
} from '@element-plus/icons-vue'

export default {
  name: 'Attendance',
  components: {
    User,
    ArrowDown,
    House,
    Clock,
    Document,
    Calendar,
    Check,
    Camera,
    Warning,
    Plus,
    Refresh
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const activeMenu = ref('attendance')
    const currentTime = ref(new Date().toLocaleTimeString('zh-CN'))
    const currentDate = ref(new Date().toLocaleDateString('zh-CN', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      weekday: 'long'
    }))
    const timeTimer = ref(null)
    let workHoursTimer = null
    
    // 人脸识别相关
    const faceDialogVisible = ref(false)
    const videoRef = ref(null)
    const canvasRef = ref(null)
    const faceChecking = ref(false)
    let mediaStream = null
    
    // 申请补卡相关
    const showApplyDialog = ref(false)
    const applyLoading = ref(false)
    const submitting = ref(false)
    const applyFormRef = ref(null)
    
    // 用户信息
    const userInfo = reactive({
      id: null,
      username: '',
      realName: '',
      roleId: null,
      faceImageUrl: null
    })
    
    // 今日考勤信息
    const todayAttendance = reactive({
      id: null,
      checkInTime: null,
      checkOutTime: null,
      workHours: 0,
      isLate: false,
      isEarlyLeave: false,
      expectedCheckOutTime: null
    })
    
    // 申请补卡记录
    const applyRecords = ref([])
    
    // 申请补卡表单
    const applyForm = reactive({
      targetDate: null,
      checkType: 1,
      checkTime: null,
      reason: ''
    })
    
    // 表单验证规则
    const applyRules = {
      targetDate: [
        { required: true, message: '请选择补卡日期', trigger: 'change' }
      ],
      checkType: [
        { required: true, message: '请选择补卡类型', trigger: 'change' }
      ],
      checkTime: [
        { required: true, message: '请选择补卡时间', trigger: 'change' }
      ],
      reason: [
        { required: true, message: '请输入申请原因', trigger: 'blur' },
        { min: 5, message: '申请原因至少5个字符', trigger: 'blur' }
      ]
    }
    
    // 更新时间
    const updateTime = () => {
      currentTime.value = new Date().toLocaleTimeString('zh-CN')
      currentDate.value = new Date().toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        weekday: 'long'
      })
    }
    
    // 计算是否可以打卡
    const canCheckIn = computed(() => {
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
      if (todayAttendance.expectedCheckOutTime) {
        const expectedTime = new Date(todayAttendance.expectedCheckOutTime)
        return expectedTime.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
      return ''
    })
    
    // 获取考勤状态
    const getAttendanceStatus = () => {
      if (!todayAttendance.checkInTime && !todayAttendance.checkOutTime) {
        return '未打卡'
      } else if (todayAttendance.isLate) {
        return '迟到'
      } else if (todayAttendance.isEarlyLeave) {
        return '早退'
      } else {
        return '正常'
      }
    }
    
    // 获取状态标签类型
    const getStatusTagType = () => {
      const status = getAttendanceStatus()
      const typeMap = {
        '未打卡': 'info',
        '正常': 'success',
        '迟到': 'warning',
        '早退': 'danger'
      }
      return typeMap[status] || 'info'
    }
    
    // 获取申请状态文本
    const getApplyStatusText = (status) => {
      const statusMap = {
        0: '待审批',
        1: '已批准',
        2: '已拒绝'
      }
      return statusMap[status] || '未知'
    }
    
    // 获取申请状态类型
    const getApplyStatusType = (status) => {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return typeMap[status] || 'info'
    }
    
    // 禁用日期（只能申请过去30天内的补卡）
    const disabledDate = (time) => {
      const today = new Date()
      const thirtyDaysAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
      return time.getTime() > today.getTime() || time.getTime() < thirtyDaysAgo.getTime()
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    }
    
    // 格式化日期时间
    const formatDateTime = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }
    
    // 格式化时间
    const formatTime = (timeStr) => {
      if (!timeStr) return '-'
      const time = new Date(timeStr)
      return time.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }
    
    // 格式化日期用于提交（YYYY-MM-DD）
    const formatDateForSubmit = (date) => {
      if (!date) return null
      
      // 如果是字符串，直接返回
      if (typeof date === 'string') {
        return date
      }
      
      // 如果是Date对象，格式化为 YYYY-MM-DD
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      return null
    }
    
    // 获取补卡类型
    const getSupplementType = (record) => {
      if (record.checkInIp === '补卡申请') {
        return '签到'
      } else if (record.checkOutIp === '补卡申请') {
        return '签退'
      }
      return '未知'
    }
    
    
    
    // 人脸识别打卡
    const handleFaceCheckIn = async () => {
      faceDialogVisible.value = true
      
      try {
        mediaStream = await navigator.mediaDevices.getUserMedia({ 
          video: { width: 640, height: 480 } 
        })
        
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
    
    // 拍照并打卡
    const captureAndCheckIn = async () => {
      if (!videoRef.value || !canvasRef.value) {
        ElMessage.error('摄像头未就绪')
        return
      }
      
      faceChecking.value = true
      
      try {
        const canvas = canvasRef.value
        const video = videoRef.value
        canvas.width = video.videoWidth
        canvas.height = video.videoHeight
        const ctx = canvas.getContext('2d')
        ctx.drawImage(video, 0, 0)
        
        const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg', 0.8))
        const type = !todayAttendance.checkInTime ? 1 : 2
        
        const response = await attendanceAPI.checkInWithFace(blob, type)
        
        if (response.ok) {
          const action = type === 1 ? '签到' : '签退'
          ElMessage.success(`人脸识别${action}成功`)
          faceDialogVisible.value = false
          await loadTodayAttendance()
          
          // 立即更新一次工作时长
          setTimeout(() => {
            const response = attendanceAPI.getTodayAttendance()
            response.then(res => {
              if (res.ok && res.data) {
                calculateWorkHours(res.data)
              }
            })
          }, 2000)
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
      if (mediaStream) {
        mediaStream.getTracks().forEach(track => track.stop())
        mediaStream = null
      }
    }
    
    // 未注册人脸处理
    const handleNoFaceImage = () => {
      ElMessageBox.confirm('请先在个人中心上传人脸照片后使用人脸识别打卡功能', '提示', {
        confirmButtonText: '去个人中心',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        router.push('/profile')
      }).catch(() => {
        // 用户取消
      })
    }
    
    // 提交补卡申请
    const submitApply = () => {
      applyFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true
          
          // 格式化数据以匹配后端期望
          // 确保日期格式为 YYYY-MM-DD，时间格式为 HH:mm
          const formData = {
            targetDate: applyForm.targetDate ? formatDateForSubmit(applyForm.targetDate) : null,
            checkType: applyForm.checkType,
            checkTime: applyForm.checkTime, // 后端期望 HH:mm 格式
            reason: applyForm.reason // 后端字段名是 reason，不是 supplementReason
          }
          
          console.log('格式化后的数据:', formData)
          
          try {
            const response = await attendanceAPI.applySupplement(formData)
            
            if (response.ok) {
              ElMessage.success('补卡申请已提交，等待审批')
              showApplyDialog.value = false
              resetApplyForm()
              loadApplyRecords()
            } else {
              ElMessage.error(response.message || '提交失败')
            }
          } catch (error) {
            console.error('补卡申请提交失败:', error)
            ElMessage.error('提交失败：' + error.message)
          } finally {
            submitting.value = false
          }
        }
      })
    }
    
    // 重置申请表单
    const resetApplyForm = () => {
      applyForm.targetDate = null
      applyForm.checkType = 1
      applyForm.checkTime = null
      applyForm.reason = ''
      applyFormRef.value?.resetFields()
    }
    
    // 撤销申请
    const handleCancelApply = (record) => {
      ElMessageBox.confirm('确定要撤销这个补卡申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await attendanceAPI.cancelSupplement(record.id)
          
          if (response.ok) {
            ElMessage.success('申请已撤销')
            loadApplyRecords()
          } else {
            ElMessage.error(response.message || '撤销失败')
          }
        } catch (error) {
          ElMessage.error('撤销失败：' + error.message)
        }
      }).catch(() => {
        // 用户取消
      })
    }
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        const response = await userAPI.getUserInfo()
        if (response.ok && response.data) {
          Object.assign(userInfo, response.data)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }
    
    // 加载今日考勤
    const loadTodayAttendance = async () => {
      try {
        const response = await attendanceAPI.getTodayAttendance()
        if (response.ok && response.data) {
          Object.assign(todayAttendance, response.data)
          
          // 保存原始时间数据
          const originalData = { ...response.data }
          
          if (originalData.checkInTime) {
            const checkInDate = new Date(originalData.checkInTime)
            todayAttendance.checkInTime = checkInDate.toLocaleTimeString('zh-CN', { 
              hour: '2-digit', 
              minute: '2-digit' 
            })
          }
          
          if (originalData.checkOutTime) {
            const checkOutDate = new Date(originalData.checkOutTime)
            todayAttendance.checkOutTime = checkOutDate.toLocaleTimeString('zh-CN', { 
              hour: '2-digit', 
              minute: '2-digit' 
            })
          }
          
          // 前端计算工作时长
          calculateWorkHours(originalData)
        }
      } catch (error) {
        console.error('加载今日考勤失败:', error)
      }
    }
    
    // 计算工作时长
    const calculateWorkHours = (data) => {
      if (!data.checkInTime) {
        todayAttendance.workHours = 0
        return
      }
      
      const checkIn = new Date(data.checkInTime)
      let endTime
      
      if (data.checkOutTime) {
        // 已签退
        endTime = new Date(data.checkOutTime)
      } else {
        // 未签退，使用当前时间
        endTime = new Date()
      }
      
      // 调试信息
      console.log('计算工作时长调试:')
      console.log('签到时间:', data.checkInTime, '→', checkIn.toString())
      console.log('结束时间:', data.checkOutTime || '当前时间', '→', endTime.toString())
      
      // 计算时间差（毫秒）
      let diff = endTime.getTime() - checkIn.getTime()
      console.log('原始时间差(ms):', diff)
      
      // 只有在时间差小于-12小时（-43200000ms）时才认为是跨天
      if (diff < -12 * 60 * 60 * 1000) {
        diff += 24 * 60 * 60 * 1000
        console.log('检测到跨天，调整后时间差(ms):', diff)
      }
      
      // 转换为小时
      const hours = diff / (1000 * 60 * 60)
      
      // 限制最大工作时长为12小时，超过的部分可能是系统错误
      let displayHours = hours
      if (hours > 12) {
        console.warn('工作时长超过12小时，可能有数据错误')
        displayHours = Math.min(hours, 12)
      }
      
      // 保留一位小数
      todayAttendance.workHours = Math.round(displayHours * 10) / 10
      
      console.log('计算结果(hours):', hours)
      console.log('显示的工作时长:', todayAttendance.workHours)
    }
    
    
    
    // 加载申请记录
    const loadApplyRecords = async () => {
      applyLoading.value = true
      try {
        console.log('开始加载补卡记录...')
        const response = await attendanceAPI.getSupplementRecords()
        console.log('补卡记录接口响应:', response)
        
        if (response.ok && response.data) {
          applyRecords.value = response.data
          console.log('加载到的补卡记录:', response.data)
        } else {
          console.warn('补卡记录接口返回异常:', response)
          applyRecords.value = []
        }
      } catch (error) {
        console.error('加载申请记录失败:', error)
        ElMessage.error('加载申请记录失败')
      } finally {
        applyLoading.value = false
      }
    }
    
    // 菜单选择处理
    const handleMenuSelect = (key) => {
      router.push(`/${key}`)
    }
    
    // 下拉菜单命令处理
    const handleCommand = (command) => {
      if (command === 'profile') {
        router.push('/profile')
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
    
    onMounted(() => {
      // 启动时间更新定时器
      timeTimer.value = setInterval(updateTime, 1000)
      
      // 启动工作时长更新定时器（每30秒更新一次）
      workHoursTimer = setInterval(() => {
        if (todayAttendance.checkInTime) {
          const response = attendanceAPI.getTodayAttendance()
          response.then(res => {
            if (res.ok && res.data) {
              calculateWorkHours(res.data)
            }
          }).catch(err => {
            console.error('更新工作时长失败:', err)
          })
        }
      }, 30000)
      
      // 根据路由设置活动菜单
      activeMenu.value = route.path.replace('/', '') || 'attendance'
      
      // 加载数据
      loadUserInfo()
      loadTodayAttendance()
      loadApplyRecords()
    })
    
    onUnmounted(() => {
      // 清理定时器
      if (timeTimer.value) {
        clearInterval(timeTimer.value)
      }
      if (workHoursTimer) {
        clearInterval(workHoursTimer)
      }
      
      // 清理摄像头
      if (mediaStream) {
        mediaStream.getTracks().forEach(track => track.stop())
      }
    })
    
    return {
      activeMenu,
      currentTime,
      currentDate,
      userInfo,
      todayAttendance,
      canCheckIn,
      checkInButtonText,
      expectedCheckOutTime,
      faceDialogVisible,
      videoRef,
      canvasRef,
      faceChecking,
      showApplyDialog,
      applyLoading,
      submitting,
      applyForm,
      applyRules,
      applyFormRef,
      applyRecords,
      getAttendanceStatus,
      getStatusTagType,
      getApplyStatusText,
      getApplyStatusType,
      disabledDate,
      formatDate,
      formatDateTime,
      formatTime,
      formatDateForSubmit,
      getSupplementType,
      loadApplyRecords,
      handleFaceCheckIn,
      captureAndCheckIn,
      closeFaceDialog,
      handleNoFaceImage,
      submitApply,
      resetApplyForm,
      handleCancelApply,
      handleMenuSelect,
      handleCommand
    }
  }
}
</script>

<style scoped>
.attendance {
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

.attendance-content {
  max-width: 1200px;
  margin: 0 auto;
}

.check-in-card,
.status-card,
.apply-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check-in-section {
  text-align: center;
  padding: 30px 0;
}

.time-display {
  font-size: 48px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.date-display {
  font-size: 18px;
  color: #909399;
  margin-bottom: 30px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
  margin-bottom: 20px;
}

.check-in-button {
  width: 200px;
  height: 50px;
  font-size: 16px;
}

.check-in-info {
  display: flex;
  justify-content: center;
}

.info-item {
  margin: 0 5px;
}

.status-section {
  padding: 20px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.status-item:last-child {
  border-bottom: none;
}

.status-label {
  font-size: 14px;
  color: #909399;
}

.status-value {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
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
  
  .time-display {
    font-size: 36px;
  }
  
  .check-in-button {
    width: 160px;
    height: 45px;
    font-size: 14px;
  }
}
</style>