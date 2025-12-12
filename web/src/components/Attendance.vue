<template>
  <div class="attendance-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>今日考勤</span>
        </div>
      </template>
      <div class="attendance-section">
        <div class="time-display">{{ currentTime }}</div>
        <el-button 
          type="primary" 
          size="large" 
          @click="handleCheckIn" 
          :disabled="checkedIn"
          class="check-in-button">
          {{ checkedIn ? '今日已打卡' : '点击打卡' }}
        </el-button>
        <p class="attendance-info">弹性打卡时间：08:00-10:00</p>
      </div>
    </el-card>
    
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <div class="quick-actions">
        <el-button type="success" @click="applyLeave">申请请假</el-button>
        <el-button type="info" @click="viewRecords">查看记录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'Attendance',
  setup(props, { emit }) {
    const currentTime = ref(new Date().toLocaleTimeString('zh-CN'))
    const checkedIn = ref(false)
    
    const updateTime = () => {
      currentTime.value = new Date().toLocaleTimeString('zh-CN')
    }
    
    const handleCheckIn = () => {
      checkedIn.value = true
      emit('check-in')
    }
    
    const applyLeave = () => {
      emit('apply-leave')
    }
    
    const viewRecords = () => {
      emit('view-records')
    }
    
    onMounted(() => {
      setInterval(updateTime, 1000)
    })
    
    return {
      currentTime,
      checkedIn,
      handleCheckIn,
      applyLeave,
      viewRecords
    }
  }
}
</script>

<style scoped>
.attendance-section {
  text-align: center;
  padding: 20px 0;
}

.time-display {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.check-in-button {
  margin-bottom: 15px;
}

.attendance-info {
  margin-top: 15px;
  color: #909399;
  font-size: 14px;
}

.quick-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.card-header {
  text-align: center;
  font-size: 16px;
  font-weight: bold;
}
</style>