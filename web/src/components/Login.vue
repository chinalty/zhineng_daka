<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>智能打卡系统登录</span>
        </div>
      </template>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%;">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'

export default {
  name: 'Login',
  setup(props, { emit }) {
    const loginFormRef = ref(null)
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const loginRules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }
    
    const handleLogin = () => {
      loginFormRef.value.validate((valid) => {
        if (valid) {
          emit('login', loginForm)
        }
      })
    }
    
    return {
      loginFormRef,
      loginForm,
      loginRules,
      handleLogin
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
  background: linear-gradient(135deg, #7873f9, #6a11cb);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.card-header {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}
</style>