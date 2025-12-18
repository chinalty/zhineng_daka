<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>智能打卡系统</h2>
        <p>公司内部考勤管理平台</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 登录标签页 -->
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="用户名或邮箱" 
                prefix-icon="User"
                size="large">
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock"
                size="large"
                show-password>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleLogin" 
                :loading="loginLoading"
                size="large"
                class="login-button">
                {{ loginLoading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 注册标签页 -->
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="用户名" 
                prefix-icon="User"
                size="large">
              </el-input>
            </el-form-item>
            <el-form-item prop="email">
              <el-input 
                v-model="registerForm.email" 
                placeholder="邮箱地址" 
                prefix-icon="Message"
                size="large">
              </el-input>
            </el-form-item>
            <el-form-item prop="departmentId">
              <el-select 
                v-model="registerForm.departmentId" 
                placeholder="选择部门" 
                size="large"
                style="width: 100%">
                <el-option 
                  v-for="dept in departments" 
                  :key="dept.id" 
                  :label="dept.name" 
                  :value="dept.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock"
                size="large"
                show-password>
              </el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="确认密码" 
                prefix-icon="Lock"
                size="large"
                show-password>
              </el-input>
            </el-form-item>
            <el-form-item prop="captcha">
              <div class="captcha-container">
                <el-input 
                  v-model="registerForm.captcha" 
                  placeholder="验证码" 
                  prefix-icon="Key"
                  size="large">
                </el-input>
                <el-button 
                  @click="sendCaptcha" 
                  :disabled="captchaDisabled"
                  size="large"
                  class="captcha-button">
                  {{ captchaText }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleRegister" 
                :loading="registerLoading"
                size="large"
                class="login-button">
                {{ registerLoading ? '注册中...' : '注册' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <!-- 测试账号提示 -->
      <div class="test-tips">
        <el-alert
          title="测试账号"
          type="info"
          :closable="false"
          show-icon>
          <p>管理员: admin / admin123</p>
          <p>员工: employee / 123456</p>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authAPI, userAPI } from '../services/api'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const activeTab = ref('login')
    const loginLoading = ref(false)
    const registerLoading = ref(false)
    const captchaDisabled = ref(false)
    const captchaText = ref('发送验证码')
    const captchaTimer = ref(null)
    const departments = ref([])
    
    const loginFormRef = ref(null)
    const registerFormRef = ref(null)
    
    // 登录表单
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    // 注册表单
    const registerForm = reactive({
      username: '',
      email: '',
      departmentId: null,
      password: '',
      confirmPassword: '',
      captcha: ''
    })
    
    // 登录表单验证规则
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ]
    }
    
    // 注册表单验证规则
    const registerRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 30, message: '用户名长度在3到30个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      departmentId: [
        { required: true, message: '请选择部门', trigger: 'change' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, max: 30, message: '密码长度在8到30个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: (rule, value, callback) => {
          if (value !== registerForm.password) {
            callback(new Error('两次输入密码不一致'))
          } else {
            callback()
          }
        }, trigger: 'blur' }
      ],
      captcha: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { len: 4, message: '验证码为4位数字', trigger: 'blur' }
      ]
    }
    
    // 登录处理
    const handleLogin = () => {
      loginFormRef.value.validate(async (valid) => {
        if (valid) {
          loginLoading.value = true
          try {
            const response = await authAPI.login(loginForm)
            
            if (response.ok) {
              // 保存token到localStorage
              localStorage.setItem('token', response.token)
              ElMessage.success('登录成功')
              // 跳转到主页面
              router.push('/dashboard')
            }
          } catch (error) {
            ElMessage.error(error.message || '登录失败')
          } finally {
            loginLoading.value = false
          }
        }
      })
    }
    
    // 发送验证码
    const sendCaptcha = async () => {
      if (!registerForm.email) {
        ElMessage.warning('请先输入邮箱地址')
        return
      }
      
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
        ElMessage.warning('请输入正确的邮箱地址')
        return
      }
      
      try {
        const response = await authAPI.sendCaptcha(registerForm.email)
        
        if (response.ok) {
          ElMessage.success('验证码已发送')
          // 开始倒计时
          captchaDisabled.value = true
          let countdown = 60
          captchaTimer.value = setInterval(() => {
            countdown--
            captchaText.value = `${countdown}秒后重发`
            if (countdown <= 0) {
              clearInterval(captchaTimer.value)
              captchaDisabled.value = false
              captchaText.value = '发送验证码'
            }
          }, 1000)
        }
      } catch (error) {
        ElMessage.error(error.message || '发送验证码失败')
      }
    }
    
    // 注册处理
    const handleRegister = () => {
      registerFormRef.value.validate(async (valid) => {
        if (valid) {
          registerLoading.value = true
          try {
            const response = await authAPI.register({
              username: registerForm.username,
              email: registerForm.email,
              departmentId: registerForm.departmentId,
              password: registerForm.password,
              captcha: registerForm.captcha
            })
            
            if (response.ok) {
              ElMessage.success('注册成功，请登录')
              // 切换到登录标签
              activeTab.value = 'login'
              // 清空注册表单
              Object.keys(registerForm).forEach(key => {
                registerForm[key] = ''
              })
            }
          } catch (error) {
            ElMessage.error(error.message || '注册失败')
          } finally {
            registerLoading.value = false
          }
        }
      })
    }
    
    // 加载部门列表
    onMounted(async () => {
      try {
        const response = await fetch('http://localhost:8080/api/user/departments')
        const data = await response.json()
        if (data.ok) {
          departments.value = data.data
        }
      } catch (error) {
        console.error('加载部门列表失败:', error)
      }
    })
    
    return {
      activeTab,
      loginForm,
      registerForm,
      loginFormRef,
      registerFormRef,
      loginRules,
      registerRules,
      loginLoading,
      registerLoading,
      captchaDisabled,
      captchaText,
      departments,
      handleLogin,
      sendCaptcha,
      handleRegister
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #303133;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 500;
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-container .el-input {
  flex: 1;
}

.captcha-button {
  width: 120px;
  white-space: nowrap;
}

.test-tips {
  margin-top: 20px;
}

.test-tips .el-alert {
  border-radius: 8px;
}

.test-tips p {
  margin: 5px 0;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .login-header h2 {
    font-size: 24px;
  }
  
  .captcha-container {
    flex-direction: column;
  }
  
  .captcha-button {
    width: 100%;
  }
}
</style>