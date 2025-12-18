<template>
  <div class="profile">
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
          <div class="profile-content">
            <!-- 个人信息卡片 -->
            <el-card class="profile-card">
              <template #header>
                <div class="card-header">
                  <span>个人信息</span>
                  <el-button 
                    type="primary" 
                    @click="editMode = !editMode"
                    :icon="editMode ? 'Close' : 'Edit'">
                    {{ editMode ? '取消编辑' : '编辑信息' }}
                  </el-button>
                </div>
              </template>
              
              <el-form 
                :model="profileForm" 
                :rules="profileRules" 
                ref="profileFormRef" 
                label-width="100px"
                class="profile-form">
                <!-- 头像区域 -->
                <div class="avatar-section">
                  <div class="avatar-container">
                    <el-avatar 
                      :size="100" 
                      :src="userInfo.faceImageUrl" 
                      :icon="UserFilled" />
                    <el-upload
                      class="avatar-uploader"
                      :action="uploadAction"
                      :headers="uploadHeaders"
                      :show-file-list="false"
                      :on-success="handleUploadSuccess"
                      :on-error="handleUploadError"
                      :before-upload="beforeUpload"
                      accept="image/*">
                      <el-button size="small" type="primary" style="margin-top: 10px;">
                        {{ userInfo.faceImageUrl ? '更换人脸照片' : '上传人脸照片' }}
                      </el-button>
                    </el-upload>
                  </div>
                  <div class="avatar-info">
                    <h3>{{ userInfo.realName || userInfo.username }}</h3>
                    <p>{{ getRoleText(userInfo.roleId) }}</p>
                    <el-tag v-if="userInfo.faceImageUrl" type="success" size="small" style="margin-top: 5px;">
                      已录入人脸
                    </el-tag>
                    <el-tag v-else type="warning" size="small" style="margin-top: 5px;">
                      未录入人脸
                    </el-tag>
                  </div>
                </div>
                
                <!-- 基本信息 -->
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="用户名" prop="username">
                      <el-input 
                        v-model="profileForm.username" 
                        :disabled="!editMode"
                        placeholder="用户名">
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="真实姓名" prop="realName">
                      <el-input 
                        v-model="profileForm.realName" 
                        :disabled="!editMode"
                        placeholder="请输入真实姓名">
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="邮箱" prop="email">
                      <el-input 
                        v-model="profileForm.email" 
                        :disabled="!editMode"
                        placeholder="请输入邮箱地址">
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="部门" prop="department">
                      <el-input 
                        v-model="profileForm.departmentName" 
                        disabled
                        placeholder="部门信息">
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="角色">
                      <el-tag :type="getRoleTagType(userInfo.roleId)">
                        {{ getRoleText(userInfo.roleId) }}
                      </el-tag>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="状态">
                      <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                        {{ userInfo.status === 1 ? '启用' : '禁用' }}
                      </el-tag>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item v-if="editMode">
                  <el-button type="primary" @click="handleSaveProfile">保存修改</el-button>
                  <el-button @click="editMode = false">取消</el-button>
                </el-form-item>
              </el-form>
            </el-card>
            
            <!-- 修改密码卡片 -->
            <el-card class="password-card">
              <template #header>
                <div class="card-header">
                  <span>修改密码</span>
                </div>
              </template>
              
              <el-form 
                :model="passwordForm" 
                :rules="passwordRules" 
                ref="passwordFormRef" 
                label-width="100px"
                class="password-form">
                <el-form-item label="当前密码" prop="currentPassword">
                  <el-input 
                    v-model="passwordForm.currentPassword" 
                    type="password" 
                    show-password
                    placeholder="请输入当前密码">
                  </el-input>
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input 
                    v-model="passwordForm.newPassword" 
                    type="password" 
                    show-password
                    placeholder="请输入新密码（至少6位）">
                  </el-input>
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input 
                    v-model="passwordForm.confirmPassword" 
                    type="password" 
                    show-password
                    placeholder="请再次输入新密码">
                  </el-input>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
                  <el-button @click="resetPasswordForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
            
            <!-- 账户统计 -->
            <el-card class="stats-card">
              <template #header>
                <div class="card-header">
                  <span>账户统计</span>
                </div>
              </template>
              
              <el-row :gutter="20">
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-number">{{ stats.totalDays }}</div>
                    <div class="stat-label">总出勤天数</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-number">{{ stats.lateDays }}</div>
                    <div class="stat-label">迟到次数</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-number">{{ stats.leaveDays }}</div>
                    <div class="stat-label">请假天数</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-number">{{ stats.workHours }}</div>
                    <div class="stat-label">工作时长(小时)</div>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userAPI } from '../services/api'
import { 
  User, 
  ArrowDown, 
  House, 
  Clock, 
  Document, 
  Calendar, 
  Check, 
  UserFilled,
  Edit,
  Close
} from '@element-plus/icons-vue'

export default {
  name: 'Profile',
  components: {
    User,
    ArrowDown,
    House,
    Clock,
    Document,
    Calendar,
    Check,
    UserFilled,
    Edit,
    Close
  },
  setup() {
    const router = useRouter()
    const activeMenu = ref('profile')
    const editMode = ref(false)
    
    const profileFormRef = ref(null)
    const passwordFormRef = ref(null)
    
    // 人脸照片上传配置
    const uploadAction = 'http://localhost:8080/api/user/upload-face'
    const uploadHeaders = {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
    
    // 用户信息
    const userInfo = reactive({
      id: null,
      username: '',
      email: '',
      realName: '',
      departmentId: null,
      departmentName: '',
      roleId: null,
      roleName: '',
      status: 1,
      createdAt: null,
      faceImageUrl: null
    })
    
    // 个人信息表单
    const profileForm = reactive({
      username: '',
      realName: '',
      email: ''
    })
    
    // 密码表单
    const passwordForm = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    // 统计数据
    const stats = reactive({
      totalDays: 0,
      lateDays: 0,
      leaveDays: 0,
      workHours: 0
    })
    
    // 表单验证规则
    const profileRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 30, message: '用户名长度在3到30个字符', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' },
        { min: 2, max: 50, message: '姓名长度在2到50个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ]
    }
    
    const passwordRules = {
      currentPassword: [
        { required: true, message: '请输入当前密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: (rule, value, callback) => {
          if (value !== passwordForm.newPassword) {
            callback(new Error('两次输入密码不一致'))
          } else {
            callback()
          }
        }, trigger: 'blur' }
      ]
    }
    
    // 获取角色文本
    const getRoleText = (roleId) => {
      const roleMap = {
        1: '系统管理员',
        2: '部门经理',
        3: '普通员工'
      }
      return roleMap[roleId] || '未知角色'
    }
    
    // 获取角色标签类型
    const getRoleTagType = (roleId) => {
      const typeMap = {
        1: 'danger',
        2: 'warning',
        3: 'info'
      }
      return typeMap[roleId] || 'info'
    }
    
    // 保存个人信息
    const handleSaveProfile = () => {
      profileFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await userAPI.updateProfile(profileForm)
            
            if (response.ok) {
              // 更新本地用户信息
              Object.assign(userInfo, profileForm)
              
              editMode.value = false
              ElMessage.success('个人信息更新成功')
            } else {
              ElMessage.error(response.message || '更新失败')
            }
          } catch (error) {
            ElMessage.error('更新失败：' + error.message)
          }
        }
      })
    }
    
    // 修改密码
    const handleChangePassword = () => {
      passwordFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await userAPI.changePassword(passwordForm)
            
            if (response.ok) {
              ElMessage.success('密码修改成功')
              resetPasswordForm()
            } else {
              ElMessage.error(response.message || '密码修改失败')
            }
          } catch (error) {
            ElMessage.error('密码修改失败：' + error.message)
          }
        }
      })
    }
    
    // 重置密码表单
    const resetPasswordForm = () => {
      passwordForm.currentPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      passwordFormRef.value?.resetFields()
    }
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        const response = await userAPI.getUserInfo()
        if (response.ok && response.data) {
          Object.assign(userInfo, response.data)
          Object.assign(profileForm, {
            username: response.data.username,
            realName: response.data.realName,
            email: response.data.email,
            departmentName: response.data.departmentName || ''
          })
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        // 从 token 中提取用户名作为降级方案
        try {
          const token = localStorage.getItem('token')
          if (token) {
            const payload = JSON.parse(atob(token.split('.')[1]))
            userInfo.username = payload.sub || '用户'
            profileForm.username = payload.sub || '用户'
            ElMessage.warning('部分信息加载失败，请联系管理员')
          } else {
            ElMessage.error('未登录，请重新登录')
            router.push('/login')
          }
        } catch (e) {
          console.error('解析token失败:', e)
          ElMessage.error('登录状态异常，请重新登录')
          router.push('/login')
        }
      }
    }
    
    // 加载统计数据
    const loadStats = async () => {
      try {
        const response = await userAPI.getUserStats()
        if (response.ok && response.data) {
          stats.totalDays = response.data.totalDays || 0
          stats.lateDays = response.data.lateDays || 0
          stats.leaveDays = response.data.leaveDays || 0
          stats.workHours = response.data.workHours || 0
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        // 如果API不可用，显示0
        stats.totalDays = 0
        stats.lateDays = 0
        stats.leaveDays = 0
        stats.workHours = 0
      }
    }
    
    // 菜单选择处理
    const handleMenuSelect = (key) => {
      router.push(`/${key}`)
    }
    
    // 下拉菜单命令处理
    const handleCommand = (command) => {
      if (command === 'profile') {
        // 当前页面，不做处理
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
    
    // 上传前验证
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        ElMessage.error('只能上传图片文件！')
        return false
      }
      if (!isLt5M) {
        ElMessage.error('图片大小不能超过 5MB！')
        return false
      }
      return true
    }
    
    // 上传成功
    const handleUploadSuccess = (response) => {
      if (response.ok) {
        ElMessage.success('人脸照片上传成功')
        // 更新用户信息
        if (response.data && response.data.faceImageUrl) {
          userInfo.faceImageUrl = response.data.faceImageUrl
        }
        // 重新加载用户信息
        loadUserInfo()
      } else {
        ElMessage.error(response.message || '上传失败')
      }
    }
    
    // 上传失败
    const handleUploadError = (error) => {
      console.error('上传失败:', error)
      ElMessage.error('上传失败，请重试')
    }
    
    onMounted(() => {
      loadUserInfo()
      loadStats()
    })
    
    return {
      activeMenu,
      editMode,
      userInfo,
      profileForm,
      passwordForm,
      stats,
      profileRules,
      passwordRules,
      profileFormRef,
      passwordFormRef,
      uploadAction,
      uploadHeaders,
      getRoleText,
      getRoleTagType,
      handleSaveProfile,
      handleChangePassword,
      resetPasswordForm,
      handleMenuSelect,
      handleCommand,
      beforeUpload,
      handleUploadSuccess,
      handleUploadError
    }
  }
}
</script>

<style scoped>
.profile {
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

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card,
.password-card,
.stats-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-uploader {
  margin-top: 10px;
}

.avatar-info {
  margin-left: 20px;
}

.avatar-info h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.avatar-info p {
  margin: 0;
  color: #909399;
}

.profile-form,
.password-form {
  max-width: 800px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 10px;
  }
  
  .header-left h2 {
    font-size: 18px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .avatar-info {
    margin-left: 0;
    margin-top: 10px;
  }
  
  .stat-item {
    padding: 15px 10px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}
</style>