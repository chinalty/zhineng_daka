<template>
  <div class="approval">
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
          <div class="approval-content">
            <!-- 页面标题 -->
            <div class="page-header">
              <h2>部门请假管理</h2>
              <p>查看和管理本部门所有员工的请假记录</p>
            </div>
            
            <!-- 统计卡片 -->
            <el-row :gutter="20" class="stats-row">
              <el-col :span="8">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon pending">
                      <el-icon size="32"><Clock /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>待审批</h3>
                      <p>{{ pendingCount }} 条</p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon approved">
                      <el-icon size="32"><Check /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>已批准</h3>
                      <p>{{ approvedCount }} 条</p>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="stats-card">
                  <div class="stats-content">
                    <div class="stats-icon rejected">
                      <el-icon size="32"><Close /></el-icon>
                    </div>
                    <div class="stats-info">
                      <h3>已拒绝</h3>
                      <p>{{ rejectedCount }} 条</p>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 筛选器 -->
            <el-card class="filter-card">
              <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="状态">
                  <el-select v-model="filterForm.status" placeholder="全部状态" @change="handleFilter">
                    <el-option label="全部" :value="null"></el-option>
                    <el-option label="待审批" :value="0"></el-option>
                    <el-option label="已批准" :value="1"></el-option>
                    <el-option label="已拒绝" :value="2"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="请假类型">
                  <el-select v-model="filterForm.leaveType" placeholder="全部类型" @change="handleFilter">
                    <el-option label="全部" value=""></el-option>
                    <el-option label="事假" value="事假"></el-option>
                    <el-option label="病假" value="病假"></el-option>
                    <el-option label="年假" value="年假"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadApprovalList">刷新</el-button>
                </el-form-item>
              </el-form>
            </el-card>
            
            <!-- 请假列表 -->
            <el-card class="list-card">
              <el-table 
                :data="filteredList" 
                style="width: 100%"
                v-loading="loading"
                :empty-text="'暂无部门请假记录'">
                <el-table-column prop="id" label="申请编号" width="100"></el-table-column>
                <el-table-column label="申请人" width="120">
                  <template #default="scope">
                    <span>{{ scope.row.applicantName }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="leaveType" label="请假类型" width="100">
                  <template #default="scope">
                    <el-tag :type="getLeaveTypeTag(scope.row.leaveType)">
                      {{ scope.row.leaveType }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="请假时间" width="220">
                  <template #default="scope">
                    {{ formatDate(scope.row.startDate) }} 至 {{ formatDate(scope.row.endDate) }}
                  </template>
                </el-table-column>
                <el-table-column prop="leaveDays" label="天数" width="80"></el-table-column>
                <el-table-column prop="reason" label="请假原因" min-width="150" show-overflow-tooltip></el-table-column>
                <el-table-column label="申请时间" width="180">
                  <template #default="scope">
                    {{ formatDateTime(scope.row.createdAt) }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">
                      {{ getStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="审批时间" width="180">
                  <template #default="scope">
                    {{ scope.row.approvalTime ? formatDateTime(scope.row.approvalTime) : '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="审批人" width="120">
                  <template #default="scope">
                    {{ scope.row.approverName || '-' }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                  <template #default="scope">
                    <div style="display: flex; gap: 5px; flex-wrap: nowrap;">
                      <el-button 
                        v-if="scope.row.status === 0"
                        type="success" 
                        size="small" 
                        @click="handleApprove(scope.row, 1)">
                        批准
                      </el-button>
                      <el-button 
                        v-if="scope.row.status === 0"
                        type="danger" 
                        size="small" 
                        @click="handleApprove(scope.row, 2)">
                        拒绝
                      </el-button>
                      <el-button 
                        type="info" 
                        size="small" 
                        @click="handleViewDetail(scope.row)">
                        详情
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
    
    <!-- 审批对话框 -->
    <el-dialog 
      v-model="approvalDialogVisible" 
      :title="approvalAction === 1 ? '批准请假' : '拒绝请假'"
      width="500px">
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="80px">
        <el-form-item label="申请人">
          <span>{{ currentRecord?.applicantName }}</span>
        </el-form-item>
        <el-form-item label="请假类型">
          <el-tag :type="getLeaveTypeTag(currentRecord?.leaveType)">
            {{ currentRecord?.leaveType }}
          </el-tag>
        </el-form-item>
        <el-form-item label="请假时间">
          <span>{{ formatDate(currentRecord?.startDate) }} 至 {{ formatDate(currentRecord?.endDate) }}</span>
        </el-form-item>
        <el-form-item label="请假天数">
          <span>{{ currentRecord?.leaveDays }} 天</span>
        </el-form-item>
        <el-form-item label="请假原因">
          <span>{{ currentRecord?.reason }}</span>
        </el-form-item>
        <el-form-item label="审批备注" prop="remark">
          <el-input 
            v-model="approvalForm.remark" 
            type="textarea" 
            :rows="3"
            :placeholder="approvalAction === 1 ? '选填，可以填写批准意见' : '必填，请说明拒绝原因'">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button 
          :type="approvalAction === 1 ? 'success' : 'danger'" 
          @click="submitApproval"
          :loading="submitting">
          确认{{ approvalAction === 1 ? '批准' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="请假详情"
      width="600px">
      <el-descriptions :column="2" border v-if="currentRecord">
        <el-descriptions-item label="申请编号">{{ currentRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRecord.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="请假类型">
          <el-tag :type="getLeaveTypeTag(currentRecord.leaveType)">
            {{ currentRecord.leaveType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请假天数">{{ currentRecord.leaveDays }} 天</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ formatDate(currentRecord.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ formatDate(currentRecord.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ formatDateTime(currentRecord.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="请假原因" :span="2">{{ currentRecord.reason }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ getStatusText(currentRecord.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人" v-if="currentRecord.status !== 0">
          {{ currentRecord.approverId ? `用户ID: ${currentRecord.approverId}` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="审批时间" :span="2" v-if="currentRecord.status !== 0">
          {{ currentRecord.approvalTime ? formatDateTime(currentRecord.approvalTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="审批备注" :span="2" v-if="currentRecord.status !== 0">
          {{ currentRecord.approvalRemark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAPI, leaveAPI } from '../services/api'
import { 
  User, 
  ArrowDown, 
  House, 
  Clock, 
  Document, 
  Calendar, 
  Check,
  Close
} from '@element-plus/icons-vue'

export default {
  name: 'Approval',
  components: {
    User,
    ArrowDown,
    House,
    Clock,
    Document,
    Calendar,
    Check,
    Close
  },
  setup() {
    const router = useRouter()
    const activeMenu = ref('approval')
    const loading = ref(false)
    const submitting = ref(false)
    
    const approvalDialogVisible = ref(false)
    const detailDialogVisible = ref(false)
    const approvalFormRef = ref(null)
    
    const currentRecord = ref(null)
    const approvalAction = ref(1) // 1: 批准, 2: 拒绝
    
    // 用户信息
    const userInfo = reactive({
      id: null,
      username: '',
      realName: '',
      roleId: null
    })
    
    // 请假列表
    const approvalList = ref([])
    
    // 筛选表单
    const filterForm = reactive({
      status: null,
      leaveType: ''
    })
    
    // 审批表单
    const approvalForm = reactive({
      remark: ''
    })
    
    // 审批表单验证规则
    const approvalRules = {
      remark: [
        { validator: (rule, value, callback) => {
          if (approvalAction.value === 2 && !value) {
            callback(new Error('拒绝时必须填写原因'))
          } else {
            callback()
          }
        }, trigger: 'blur' }
      ]
    }
    
    // 统计数据
    const pendingCount = computed(() => {
      return approvalList.value.filter(item => item.status === 0).length
    })
    
    const approvedCount = computed(() => {
      return approvalList.value.filter(item => item.status === 1).length
    })
    
    const rejectedCount = computed(() => {
      return approvalList.value.filter(item => item.status === 2).length
    })
    
    // 筛选后的列表
    const filteredList = computed(() => {
      let list = approvalList.value
      
      if (filterForm.status !== null) {
        list = list.filter(item => item.status === filterForm.status)
      }
      
      if (filterForm.leaveType) {
        list = list.filter(item => item.leaveType === filterForm.leaveType)
      }
      
      return list
    })
    
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
    
    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '待审批',
        1: '已批准',
        2: '已拒绝'
      }
      return statusMap[status] || '未知'
    }
    
    // 获取状态类型
    const getStatusType = (status) => {
      const typeMap = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return typeMap[status] || 'info'
    }
    
    // 获取请假类型标签
    const getLeaveTypeTag = (type) => {
      const typeMap = {
        '事假': 'info',
        '病假': 'warning',
        '年假': 'success'
      }
      return typeMap[type] || 'info'
    }
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        const response = await userAPI.getUserInfo()
        if (response.ok && response.data) {
          Object.assign(userInfo, response.data)
          
          // 检查权限
          if (userInfo.roleId === 3) {
            ElMessage.error('您没有权限访问此页面')
            router.push('/dashboard')
          }
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
        }
      }
    }
    
    // 加载审批列表
    const loadApprovalList = async () => {
      loading.value = true
      try {
        console.log('审批页面 - 开始加载部门所有请假记录')
        console.log('当前用户角色:', userInfo.roleId)
        
        const response = await leaveAPI.getDepartmentLeaveRecords()
        console.log('审批页面 - 接口响应:', response)
        
        if (response.ok && response.data) {
          console.log('审批页面 - 获取到数据:', response.data)
          approvalList.value = response.data
        } else {
          console.warn('审批页面 - 接口返回异常:', response)
          approvalList.value = []
        }
      } catch (error) {
        console.error('审批页面 - 加载审批列表失败:', error)
        ElMessage.error('加载审批列表失败: ' + error.message)
      } finally {
        loading.value = false
      }
    }
    
    // 筛选处理
    const handleFilter = () => {
      // 筛选逻辑由 computed 自动处理
    }
    
    // 审批处理
    const handleApprove = (record, action) => {
      currentRecord.value = record
      approvalAction.value = action
      approvalForm.remark = ''
      approvalDialogVisible.value = true
    }
    
    // 提交审批
    const submitApproval = () => {
      approvalFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true
          try {
            const response = await leaveAPI.approveLeave({
              leaveId: currentRecord.value.id,
              status: approvalAction.value,
              remark: approvalForm.remark
            })
            
            if (response.ok) {
              ElMessage.success('审批完成')
              approvalDialogVisible.value = false
              // 重新加载列表
              loadApprovalList()
            }
          } catch (error) {
            ElMessage.error(error.message || '审批失败')
          } finally {
            submitting.value = false
          }
        }
      })
    }
    
    // 查看详情
    const handleViewDetail = (record) => {
      currentRecord.value = record
      detailDialogVisible.value = true
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
      loadApprovalList()
    })
    
    return {
      activeMenu,
      loading,
      submitting,
      userInfo,
      approvalList,
      filterForm,
      approvalForm,
      approvalRules,
      approvalFormRef,
      currentRecord,
      approvalAction,
      approvalDialogVisible,
      detailDialogVisible,
      pendingCount,
      approvedCount,
      rejectedCount,
      filteredList,
      formatDate,
      formatDateTime,
      getStatusText,
      getStatusType,
      getLeaveTypeTag,
      loadApprovalList,
      handleFilter,
      handleApprove,
      submitApproval,
      handleViewDetail,
      handleMenuSelect,
      handleCommand
    }
  }
}
</script>

<style scoped>
.approval {
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

.approval-content {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stats-content {
  display: flex;
  align-items: center;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stats-icon.pending {
  background-color: #FDF6EC;
  color: #E6A23C;
}

.stats-icon.approved {
  background-color: #F0F9FF;
  color: #67C23A;
}

.stats-icon.rejected {
  background-color: #FEF0F0;
  color: #F56C6C;
}

.stats-info h3 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #909399;
}

.stats-info p {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.list-card {
  min-height: 400px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 10px;
  }
  
  .header-left h2 {
    font-size: 18px;
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
