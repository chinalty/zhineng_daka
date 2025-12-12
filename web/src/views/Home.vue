<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-container">
        <div class="logo">
          <h2>智能打卡系统</h2>
        </div>
        <el-menu mode="horizontal" :default-active="activeIndex" class="nav-menu">
          <el-menu-item index="1">首页</el-menu-item>
          <el-menu-item index="2">产品介绍</el-menu-item>
          <el-menu-item index="3">功能特点</el-menu-item>
          <el-menu-item index="4">关于我们</el-menu-item>
        </el-menu>
        <div class="header-actions">
          <el-button type="primary" @click="goToLogin">登录系统</el-button>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 首页横幅 -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">欢迎使用智能打卡系统</h1>
          <p class="hero-subtitle">简化考勤流程，提高工作效率，让考勤管理更加智能化</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="goToLogin">进入系统</el-button>
            <el-button size="large" @click="scrollToFeatures">使用指南</el-button>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://via.placeholder.com/600x400/409EFF/FFFFFF?text=智能打卡系统" alt="智能打卡系统">
        </div>
      </section>

      <!-- 产品特点 -->
      <section class="features-section" ref="featuresRef">
        <h2 class="section-title">核心功能</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><Clock /></el-icon>
            </div>
            <h3>智能打卡</h3>
            <p>支持弹性工作制，自动计算工作时长，准确记录考勤数据</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><Calendar /></el-icon>
            </div>
            <h3>请假管理</h3>
            <p>便捷的请假申请与审批流程，支持多种请假类型</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><DataAnalysis /></el-icon>
            </div>
            <h3>数据统计</h3>
            <p>实时统计分析考勤数据，生成详细报表</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon size="48"><Lock /></el-icon>
            </div>
            <h3>安全可靠</h3>
            <p>采用JWT认证机制，确保数据安全与隐私保护</p>
          </div>
        </div>
      </section>

      <!-- 系统介绍 -->
      <section class="product-section">
        <div class="product-content">
          <div class="product-text">
            <h2>系统使用说明</h2>
            <p>智能打卡系统是公司内部使用的考勤管理工具，支持弹性工作制，帮助员工便捷打卡，管理者高效审批。</p>
            <ul class="product-list">
              <li>弹性打卡时间：08:00-10:00，适应灵活工作安排</li>
              <li>请假申请：支持事假、病假、年假三种类型</li>
              <li>审批流程：部门经理负责审批本部门员工请假</li>
              <li>考勤统计：自动计算工作时长，生成考勤报表</li>
            </ul>
            <el-button type="primary" @click="goToLogin">进入系统</el-button>
          </div>
          <div class="product-image">
            <img src="https://via.placeholder.com/500x400/67C23A/FFFFFF?text=系统界面" alt="系统界面">
          </div>
        </div>
      </section>

      <!-- 使用指南 -->
      <section class="tech-section">
        <h2 class="section-title">使用指南</h2>
        <div class="tech-grid">
          <div class="tech-category">
            <h3>员工操作</h3>
            <div class="tech-list">
              <span class="tech-item">每日打卡</span>
              <span class="tech-item">申请请假</span>
              <span class="tech-item">查看记录</span>
              <span class="tech-item">个人信息</span>
            </div>
          </div>
          <div class="tech-category">
            <h3>管理操作</h3>
            <div class="tech-list">
              <span class="tech-item">审批请假</span>
              <span class="tech-item">查看统计</span>
              <span class="tech-item">导出报表</span>
              <span class="tech-item">权限管理</span>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>智能打卡系统</h3>
          <p>公司内部考勤管理工具</p>
        </div>
        <div class="footer-section">
          <h3>系统功能</h3>
          <ul>
            <li><a href="#home">首页</a></li>
            <li><a href="#features">功能特点</a></li>
            <li><a href="#guide">使用指南</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h3>技术支持</h3>
          <p>IT部门: ext.8888</p>
          <p>邮箱: it@company.com</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2025 智能打卡系统. 内部使用系统.</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Clock, Calendar, DataAnalysis, Lock } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: {
    Clock,
    Calendar,
    DataAnalysis,
    Lock
  },
  setup() {
    const router = useRouter()
    const activeIndex = ref('1')
    const featuresRef = ref(null)

    const goToLogin = () => {
      // 跳转到登录页面
      router.push('/login')
    }

    const scrollToFeatures = () => {
      featuresRef.value?.scrollIntoView({ behavior: 'smooth' })
    }

    onMounted(() => {
      // 页面加载完成后的初始化操作
    })

    return {
      activeIndex,
      featuresRef,
      goToLogin,
      scrollToFeatures
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.header {
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo h2 {
  margin: 0;
  color: #409EFF;
}

.nav-menu {
  border: none;
  background: none;
}

.header-actions {
  display: flex;
  align-items: center;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
}

/* 首页横幅 */
.hero-section {
  display: flex;
  align-items: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  max-width: 1200px;
  margin: 0 auto;
}

.hero-content {
  flex: 1;
  padding-right: 40px;
}

.hero-title {
  font-size: 48px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
  line-height: 1.5;
}

.hero-actions {
  display: flex;
  gap: 20px;
}

.hero-image {
  flex: 1;
  text-align: center;
}

.hero-image img {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 功能特点部分 */
.features-section {
  padding: 80px 20px;
  background-color: #f8f9fa;
}

.section-title {
  text-align: center;
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 50px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background-color: #ffffff;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  color: #409EFF;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.feature-card p {
  color: #606266;
  line-height: 1.5;
}

/* 产品介绍部分 */
.product-section {
  padding: 80px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.product-content {
  display: flex;
  align-items: center;
  gap: 60px;
}

.product-text {
  flex: 1;
}

.product-text h2 {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.product-text p {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 30px;
}

.product-list {
  list-style: none;
  padding: 0;
  margin-bottom: 30px;
}

.product-list li {
  position: relative;
  padding-left: 25px;
  margin-bottom: 15px;
  color: #606266;
  line-height: 1.5;
}

.product-list li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #67C23A;
  font-weight: bold;
}

.product-image {
  flex: 1;
}

.product-image img {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 技术栈部分 */
.tech-section {
  padding: 80px 20px;
  background-color: #f8f9fa;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.tech-category h3 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  text-align: center;
}

.tech-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.tech-item {
  background-color: #409EFF;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

/* 页脚 */
.footer {
  background-color: #303133;
  color: #ffffff;
  padding: 40px 20px 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
  flex: 1;
}

.footer-section h3 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 8px;
}

.footer-section a {
  color: #ffffff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-section a:hover {
  color: #409EFF;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #4a4a4a;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    flex-direction: column;
    text-align: center;
    padding: 40px 20px;
  }

  .hero-content {
    padding-right: 0;
    margin-bottom: 30px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-actions {
    justify-content: center;
  }

  .product-content {
    flex-direction: column;
    text-align: center;
  }

  .product-text {
    order: 2;
  }

  .product-image {
    order: 1;
    margin-bottom: 30px;
  }

  .header-container {
    flex-wrap: wrap;
    padding: 0 10px;
  }

  .nav-menu {
    order: 3;
    width: 100%;
    margin-top: 10px;
  }
}
</style>