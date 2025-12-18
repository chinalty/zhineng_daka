<template>
  <div class="records">
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
          <div class="records-content">
            <!-- 页面标题和筛选 -->
            <div class="page-header">
              <div>
                <h2>考勤记录</h2>
                <p class="period-info">{{ getCurrentPeriodText() }}</p>
              </div>
              <div class="header-controls">
                <el-radio-group v-model="viewType" @change="handleViewTypeChange">
                  <el-radio-button label="month">月视图</el-radio-button>
                  <el-radio-button label="week">周视图</el-radio-button>
                </el-radio-group>
                <el-date-picker
                  v-model="selectedDate"
                  :type="viewType === 'month' ? 'month' : 'week'"
                  :format="viewType === 'month' ? 'YYYY年MM月' : 'YYYY-MM-DD'"
                  :placeholder="viewType === 'month' ? '选择月份' : '选择周'"
                  @change="handleDateChange"
                  style="margin-left: 20px;" />
              </div>
            </div>
            
            <!-- 统计卡片 -->
            <el-row :gutter="20" class="stats-row">
              <el-col :span="4">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon success">
                      <el-icon size="24"><Check /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>正常出勤</h3>
                      <p>{{ stats.normalDays }}<span>天</span></p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="4">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon warning">
                      <el-icon size="24"><Clock /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>迟到次数</h3>
                      <p>{{ stats.lateDays }}<span>次</span></p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="4">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon danger">
                      <el-icon size="24"><Warning /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>早退次数</h3>
                      <p>{{ stats.earlyDays }}<span>次</span></p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="4">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon info">
                      <el-icon size="24"><DataAnalysis /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>工作时长</h3>
                      <p>{{ stats.totalHours }}<span>小时</span></p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="4">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon overtime">
                      <el-icon size="24"><Clock /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>加班时长</h3>
                      <p>{{ stats.totalOvertimeHours }}<span>小时</span></p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="4">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon primary">
                      <el-icon size="32"><TrendCharts /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>平均工时</h3>
                      <p>{{ stats.normalDays > 0 ? (stats.totalHours / stats.normalDays).toFixed(1) : '0' }}<span>小时</span></p>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 图表区域 -->
            <el-row :gutter="20" class="chart-row">
              <el-col :span="16">
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>考勤趋势图</span>
                    </div>
                  </template>
                  <div class="chart-container" id="attendanceChart"></div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>出勤统计</span>
                    </div>
                  </template>
                  <div class="chart-container" id="pieChart"></div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 考勤记录表格 -->
            <el-card class="table-card">
              <template #header>
                <div class="card-header">
                  <span>{{ viewType === 'month' ? '本月' : '本周' }}考勤记录</span>
                  <el-button type="primary" @click="loadRecords">刷新</el-button>
                </div>
              </template>
              
              <el-table 
                :data="attendanceRecords" 
                style="width: 100%"
                v-loading="loading"
                :default-sort="{ prop: 'attendanceDate', order: 'descending' }">
                <el-table-column 
                  prop="attendanceDate" 
                  label="日期" 
                  width="120"
                  sortable>
                  <template #default="scope">
                    {{ formatDate(scope.row.attendanceDate) }}
                  </template>
                </el-table-column>
                <el-table-column prop="checkInTime" label="签到时间" width="120">
                  <template #default="scope">
                    {{ scope.row.checkInTime ? formatTime(scope.row.checkInTime) : '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="checkOutTime" label="签退时间" width="120">
                  <template #default="scope">
                    {{ scope.row.checkOutTime ? formatTime(scope.row.checkOutTime) : '-' }}
                    <!-- 调试信息，临时显示原始数据 -->
                    <!-- {{ scope.row.checkOutTime }} -->
                  </template>
                </el-table-column>
                <el-table-column prop="workHours" label="工作时长" width="100">
                  <template #default="scope">
                    {{ scope.row.workHours || '-' }} 小时
                  </template>
                </el-table-column>
                <el-table-column prop="overtimeHours" label="加班时长" width="100">
                  <template #default="scope">
                    <span v-if="scope.row.overtimeHours > 0" class="overtime-text">
                      {{ scope.row.overtimeHours }} 小时
                    </span>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row)">
                      {{ getStatusText(scope.row) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="checkInIp" label="签到IP" width="130">
                  <template #default="scope">
                    {{ scope.row.checkInIp || '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="checkOutIp" label="签退IP" width="130">
                  <template #default="scope">
                    {{ scope.row.checkOutIp || '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
                  <template #default="scope">
                    {{ scope.row.remark || '-' }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAPI, attendanceAPI } from '../services/api'
import * as echarts from 'echarts'
import { 
  User, 
  ArrowDown, 
  House, 
  Clock, 
  Document, 
  Calendar, 
  Check,
  Warning,
  DataAnalysis,
  TrendCharts
} from '@element-plus/icons-vue'

export default {
  name: 'Records',
  components: {
    User,
    ArrowDown,
    House,
    Clock,
    Document,
    Calendar,
    Check,
    Warning,
    DataAnalysis,
    TrendCharts
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const activeMenu = ref('records')
    const loading = ref(false)
    const viewType = ref('month') // 'month' 或 'week'
    const selectedDate = ref(new Date())
    
    // 用户信息
    const userInfo = reactive({
      id: null,
      username: '',
      realName: '',
      roleId: null
    })
    
    // 统计数据
    const stats = reactive({
      normalDays: 0,
      lateDays: 0,
      earlyDays: 0,
      totalHours: 0,
      totalOvertimeHours: 0
    })
    
    // 考勤记录
    const attendanceRecords = ref([])
    
    // 图表实例
    let attendanceChart = null
    let pieChart = null
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN', { 
        month: '2-digit', 
        day: '2-digit' 
      })
    }
    
    // 格式化时间
    const formatTime = (timeStr) => {
      if (!timeStr) return '-'
      try {
        // 处理 ISO 时间字符串，避免时区问题
        const time = new Date(timeStr)
        
        // 检查日期是否有效
        if (isNaN(time.getTime())) {
          console.warn('无效的时间格式:', timeStr)
          return '-'
        }
        
        // 直接从时间字符串提取时分，避免时区转换
        const timeMatch = timeStr.match(/T(\d{2}):(\d{2}):/)
        if (timeMatch) {
          return `${timeMatch[1]}:${timeMatch[2]}`
        }
        
        // 降级方案：使用 toLocaleTimeString
        return time.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit',
          timeZone: 'UTC'  // 使用 UTC 时间避免时区偏移
        })
      } catch (error) {
        console.error('时间格式化错误:', error, timeStr)
        return '-'
      }
    }
    
    // 获取状态文本
    const getStatusText = (record) => {
      if (!record.checkInTime && !record.checkOutTime) return '缺勤'
      if (record.isLate) return '迟到'
      if (record.isEarlyLeave) return '早退'
      return '正常'
    }
    
    // 获取状态类型
    const getStatusType = (record) => {
      if (!record.checkInTime && !record.checkOutTime) return 'danger'
      if (record.isLate) return 'warning'
      if (record.isEarlyLeave) return 'warning'
      return 'success'
    }
    
    // 视图类型改变
    const handleViewTypeChange = () => {
      loadRecords()
      loadCharts()
    }
    
    // 日期改变
    const handleDateChange = () => {
      loadRecords()
      loadCharts()
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
    
    // 加载考勤记录
    const loadRecords = async () => {
      loading.value = true
      try {
        let dateStr
        if (viewType.value === 'month') {
          dateStr = formatDateForAPI(selectedDate.value)
        } else {
          // 周视图使用月份，但后续会过滤数据
          dateStr = formatDateForAPI(selectedDate.value)
        }
        
        const response = await attendanceAPI.getMonthlyRecords(dateStr)
        
        if (response.ok && response.data) {
          let filteredData = response.data
          
          // 如果是周视图，过滤出本周的数据
          if (viewType.value === 'week') {
            const weekRange = getWeekRange(selectedDate.value)
            filteredData = response.data.filter(record => {
              const recordDate = new Date(record.attendanceDate)
              return recordDate >= weekRange.start && recordDate <= weekRange.end
            })
          }
          
          attendanceRecords.value = filteredData
          calculateStats()
          // 数据加载完成后重新渲染图表
          loadCharts()
        }
      } catch (error) {
        console.error('加载考勤记录失败:', error)
        ElMessage.error('加载考勤记录失败')
      } finally {
        loading.value = false
      }
    }
    
    // 格式化日期用于API
    const formatDateForAPI = (date) => {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      return `${year}-${month}`
    }
    
    // 获取周的开始和结束日期
    const getWeekRange = (date) => {
      const d = new Date(date)
      const day = d.getDay()
      const diff = d.getDate() - day + (day === 0 ? -6 : 1) // 调整为周一开始
      const monday = new Date(d.setDate(diff))
      const sunday = new Date(d.setDate(diff + 6))
      return { start: monday, end: sunday }
    }
    
    // 获取当前周期文本
    const getCurrentPeriodText = () => {
      if (viewType.value === 'month') {
        const year = selectedDate.value.getFullYear()
        const month = selectedDate.value.getMonth() + 1
        return `${year}年${month}月`
      } else {
        const weekRange = getWeekRange(selectedDate.value)
        const start = formatDate(weekRange.start)
        const end = formatDate(weekRange.end)
        return `${start} - ${end}`
      }
    }
    
    // 计算统计数据
    const calculateStats = () => {
      let normalDays = 0
      let lateDays = 0
      let earlyDays = 0
      let totalHours = 0
      let totalOvertimeHours = 0
      
      attendanceRecords.value.forEach(record => {
        if (record.isLate) lateDays++
        if (record.isEarlyLeave) earlyDays++
        if (!record.isLate && !record.isEarlyLeave && record.checkInTime) normalDays++
        
        // 工作时长
        let workHours = 0
        if (!record.workHours && record.checkInTime && record.checkOutTime) {
          workHours = calculateWorkHours(record.checkInTime, record.checkOutTime)
        } else if (record.workHours) {
          workHours = parseFloat(record.workHours)
        }
        totalHours += workHours
        
        // 优先使用后端计算的加班时长
        if (record.overtimeHours) {
          totalOvertimeHours += parseFloat(record.overtimeHours)
        } else if (workHours > 8) {
          // 如果后端没有数据，前端计算（超过8小时的部分）
          totalOvertimeHours += (workHours - 8)
        }
      })
      
      stats.normalDays = normalDays
      stats.lateDays = lateDays
      stats.earlyDays = earlyDays
      stats.totalHours = totalHours.toFixed(1)
      stats.totalOvertimeHours = totalOvertimeHours.toFixed(1)
    }
    
    // 计算工作时长（前端计算，作为备用）
    const calculateWorkHours = (checkInTime, checkOutTime) => {
      try {
        const checkIn = new Date(checkInTime)
        const checkOut = new Date(checkOutTime)
        
        let diff = checkOut.getTime() - checkIn.getTime()
        
        // 如果签退时间早于签到时间，说明跨天了，加24小时
        if (diff < 0) {
          diff += 24 * 60 * 60 * 1000
        }
        
        return Math.round(diff / (1000 * 60 * 60) * 100) / 100 // 保留两位小数
      } catch (error) {
        console.error('计算工作时长失败:', error)
        return 0
      }
    }
    
    // 加载图表
    const loadCharts = () => {
      nextTick(() => {
        // 初始化考勤趋势图
        const attendanceChartEl = document.getElementById('attendanceChart')
        if (attendanceChartEl) {
          if (attendanceChart) {
            attendanceChart.dispose()
          }
          attendanceChart = echarts.init(attendanceChartEl)
          renderAttendanceChart()
        }
        
        // 初始化饼图
        const pieChartEl = document.getElementById('pieChart')
        if (pieChartEl) {
          if (pieChart) {
            pieChart.dispose()
          }
          pieChart = echarts.init(pieChartEl)
          renderPieChart()
        }
      })
    }
    
    // 渲染考勤趋势图
    const renderAttendanceChart = () => {
      if (!attendanceChart || attendanceRecords.value.length === 0) return
      
      // 准备数据 - 按日期正序排列
      const sortedRecords = [...attendanceRecords.value].sort((a, b) => 
        new Date(a.attendanceDate) - new Date(b.attendanceDate)
      )
      
      const dates = []
      const workHours = []
      const statusData = []
      
      sortedRecords.forEach(record => {
        dates.push(formatDate(record.attendanceDate))
        
        // 使用计算出的工作时长
        let hours = 0
        if (record.workHours) {
          hours = parseFloat(record.workHours)
        } else if (record.checkInTime && record.checkOutTime) {
          hours = calculateWorkHours(record.checkInTime, record.checkOutTime)
        }
        workHours.push(hours)
        
        statusData.push(getStatusValue(record))
      })
      
      const option = {
        title: {
          text: '考勤趋势',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['工作时长', '考勤状态'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: [
          {
            type: 'value',
            name: '工作时长(小时)',
            position: 'left',
            axisLabel: {
              formatter: '{value}h'
            }
          },
          {
            type: 'value',
            name: '考勤状态',
            position: 'right',
            min: 0,
            max: 3,
            axisLabel: {
              formatter: function(value) {
                const statusMap = { 0: '缺勤', 1: '正常', 2: '迟到', 3: '早退' }
                return statusMap[value] || ''
              }
            }
          }
        ],
        series: [
          {
            name: '工作时长',
            type: 'line',
            smooth: true,
            data: workHours,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                ]
              }
            }
          },
          {
            name: '考勤状态',
            type: 'scatter',
            yAxisIndex: 1,
            data: statusData,
            symbolSize: 8,
            itemStyle: {
              color: function(params) {
                const colorMap = { 0: '#F56C6C', 1: '#67C23A', 2: '#E6A23C', 3: '#E6A23C' }
                return colorMap[params.value] || '#909399'
              }
            }
          }
        ]
      }
      
      attendanceChart.setOption(option)
    }
    
    // 渲染饼图
    const renderPieChart = () => {
      if (!pieChart) return
      
      const data = [
        { value: stats.normalDays, name: '正常出勤', itemStyle: { color: '#67C23A' } },
        { value: stats.lateDays, name: '迟到', itemStyle: { color: '#E6A23C' } },
        { value: stats.earlyDays, name: '早退', itemStyle: { color: '#F56C6C' } }
      ]
      
      const option = {
        title: {
          text: '出勤分布',
          left: 'center',
          textStyle: {
            fontSize: 16,
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 30
        },
        series: [
          {
            name: '考勤统计',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      }
      
      pieChart.setOption(option)
    }
    
    // 获取状态数值
    const getStatusValue = (record) => {
      if (!record.checkInTime && !record.checkOutTime) return 0 // 缺勤
      if (record.isLate) return 2 // 迟到
      if (record.isEarlyLeave) return 3 // 早退
      return 1 // 正常
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
      loadUserInfo()
      loadRecords()
      loadCharts()
      
      // 监听窗口大小变化
      window.addEventListener('resize', handleResize)
    })
    
    onUnmounted(() => {
      // 清理图表实例
      if (attendanceChart) {
        attendanceChart.dispose()
        attendanceChart = null
      }
      if (pieChart) {
        pieChart.dispose()
        pieChart = null
      }
      
      // 移除事件监听
      window.removeEventListener('resize', handleResize)
    })
    
    // 处理窗口大小变化
    const handleResize = () => {
      if (attendanceChart) {
        attendanceChart.resize()
      }
      if (pieChart) {
        pieChart.resize()
      }
    }
    
    return {
      activeMenu,
      viewType,
      selectedDate,
      userInfo,
      stats,
      attendanceRecords,
      loading,
      formatDate,
      formatTime,
      getStatusText,
      getStatusType,
      handleViewTypeChange,
      handleDateChange,
      loadRecords,
      handleMenuSelect,
      handleCommand,
      handleResize,
      getCurrentPeriodText
    }
  }
}
</script>

<style scoped>
.records {
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

.records-content {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.period-info {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.header-controls {
  display: flex;
  align-items: center;
}

.stats-row {
  margin-bottom: 24px;
  align-items: stretch;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--card-color) 0%, var(--card-color-light) 100%);
  z-index: 1;
}

.stats-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 16px;
  flex: 1;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  position: relative;
  flex-shrink: 0;
}

.stats-icon::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 100%);
}

.stats-icon.success {
  background: linear-gradient(135deg, #52C41A 0%, #73D13D 100%);
  color: white;
  --card-color: #52C41A;
  --card-color-light: #73D13D;
}

.stats-icon.warning {
  background: linear-gradient(135deg, #FA8C16 0%, #FFA940 100%);
  color: white;
  --card-color: #FA8C16;
  --card-color-light: #FFA940;
}

.stats-icon.danger {
  background: linear-gradient(135deg, #FF4D4F 0%, #FF7875 100%);
  color: white;
  --card-color: #FF4D4F;
  --card-color-light: #FF7875;
}

.stats-icon.info {
  background: linear-gradient(135deg, #1890FF 0%, #40A9FF 100%);
  color: white;
  --card-color: #1890FF;
  --card-color-light: #40A9FF;
}

.stats-icon.overtime {
  background: linear-gradient(135deg, #722ED1 0%, #9254DE 100%);
  color: white;
  --card-color: #722ED1;
  --card-color-light: #9254DE;
}

.stats-icon.primary {
  background: linear-gradient(135deg, #13C2C2 0%, #36CFC9 100%);
  color: white;
  --card-color: #13C2C2;
  --card-color-light: #36CFC9;
}

.stats-info {
  flex: 1;
}

.stats-info h3 {
  margin: 0 0 4px 0;
  font-size: 13px;
  font-weight: 500;
  color: #8C8C8C;
  letter-spacing: 0.5px;
}

.stats-info p {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #262626;
  line-height: 1.2;
  display: flex;
  align-items: baseline;
  white-space: nowrap;
}

.stats-info p span {
  font-size: 13px;
  font-weight: 400;
  margin-left: 2px;
  color: #8C8C8C;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.table-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overtime-text {
  color: #E6A23C;
  font-weight: 600;
}

.stats-icon.overtime {
  background-color: #FDF6EC;
  color: #E6A23C;
}

.stats-icon.primary {
  background-color: #ECF5FF;
  color: #409EFF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 10px;
  }
  
  .header-left h2 {
    font-size: 18px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-controls {
    width: 100%;
    justify-content: space-between;
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