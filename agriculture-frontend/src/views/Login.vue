<template>
  <div class="login-container">
    <Clock class="clock-component" />
    <div class="content-wrapper">
      <div class="login-box">
        <h2>用户登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              type="text"
              id="username"
              v-model="form.username"
              required
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input
              type="password"
              id="password"
              v-model="form.password"
              required
              placeholder="请输入密码"
            />
          </div>
          <button type="submit" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
          <div class="additional-links">
            <router-link to="/register">注册账号</router-link>
            <router-link to="/forgot-password">忘记密码？</router-link>
          </div>
        </form>
      </div>
      <div class="chart-box">
        <TemperatureChart />
      </div>
    </div>
    <div class="visitor-stats">
      <p>当前在线人数: {{ activeUsers }}</p>
      <p>历史访问人数: {{ totalVisitors }}</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api/user'
import { statisticsApi } from '../api/statistics'
import { ElMessage } from 'element-plus'
import Clock from '../components/Clock.vue'
import TemperatureChart from '../components/TemperatureChart.vue'
import axios from 'axios'

export default {
  name: 'Login',
  components: {
    Clock,
    TemperatureChart
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const form = ref({
      username: '',
      password: ''
    })
    const activeUsers = ref(0)
    const totalVisitors = ref(0)

    const fetchVisitorStats = async () => {
      try {
        const response = await statisticsApi.getVisitorStats()
        activeUsers.value = response.data.activeUsers
        totalVisitors.value = response.data.totalVisitors
      } catch (error) {
        console.error('获取访问统计失败:', error)
      }
    }

    const handleLogin = async () => {
      try {
        loading.value = true
        const response = await userApi.login(form.value.username, form.value.password)
        localStorage.setItem('token', response.data.token)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchVisitorStats()
      // 定期更新访问统计
      setInterval(fetchVisitorStats, 30000)
    })

    return {
      form,
      loading,
      handleLogin,
      activeUsers,
      totalVisitors
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: url('../assets/background.jpg') no-repeat center center fixed;
  background-size: cover;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.clock-component {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.content-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 40px;
  margin: 20px 0;
  flex: 1;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.chart-box {
  width: 600px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #333;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
}

button:hover {
  background-color: #45a049;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.additional-links {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.additional-links a {
  color: #4CAF50;
  text-decoration: none;
}

.additional-links a:hover {
  text-decoration: underline;
}

.visitor-stats {
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  padding: 10px;
  border-radius: 8px;
  margin-top: auto;
}

.visitor-stats p {
  margin: 5px 0;
  color: #333;
  font-size: 1.1em;
}
</style> 