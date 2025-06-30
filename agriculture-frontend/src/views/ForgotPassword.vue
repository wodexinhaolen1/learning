<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      <h2>重置密码</h2>
      <form @submit.prevent="handleSubmit">
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
          <label for="email">邮箱</label>
          <input
            type="email"
            id="email"
            v-model="form.email"
            required
            placeholder="请输入注册时的邮箱"
          />
        </div>
        <div v-if="isVerified" class="form-group">
          <label for="newPassword">新密码</label>
          <input
            type="password"
            id="newPassword"
            v-model="form.newPassword"
            required
            placeholder="请输入新密码"
          />
        </div>
        <div v-if="isVerified" class="form-group">
          <label for="confirmPassword">确认新密码</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="form.confirmPassword"
            required
            placeholder="请再次输入新密码"
          />
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '处理中...' : (isVerified ? '重置密码' : '验证身份') }}
        </button>
        <p class="login-link">
          记起密码了？<router-link to="/login">立即登录</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api/user'
import { ElMessage } from 'element-plus'
import { validatePassword, PASSWORD_RULE_MESSAGE } from '../utils/validator'

export default {
  name: 'ForgotPassword',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const isVerified = ref(false)
    const form = ref({
      username: '',
      email: '',
      newPassword: '',
      confirmPassword: ''
    })

    const handleSubmit = async () => {
      if (!isVerified.value) {
        await verifyUser()
      } else {
        await resetPassword()
      }
    }

    const verifyUser = async () => {
      try {
        loading.value = true
        const response = await userApi.verifyUserEmail(form.value.username, form.value.email)
        if (response.data.valid) {
          isVerified.value = true
          ElMessage.success('身份验证成功，请设置新密码')
        } else {
          ElMessage.error('用户名或邮箱验证失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '验证失败')
      } finally {
        loading.value = false
      }
    }

    const resetPassword = async () => {
      if (form.value.newPassword !== form.value.confirmPassword) {
        ElMessage.error('两次输入的密码不一致')
        return
      }

      if (!validatePassword(form.value.newPassword)) {
        ElMessage.error(PASSWORD_RULE_MESSAGE)
        return
      }

      try {
        loading.value = true
        await userApi.resetPassword(
          form.value.username,
          form.value.email,
          form.value.newPassword
        )
        ElMessage.success('密码重置成功')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '密码重置失败')
      } finally {
        loading.value = false
      }
    }

    return {
      form,
      loading,
      isVerified,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url('../assets/background.jpg') no-repeat center center fixed;
  background-size: cover;
}

.forgot-password-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
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

.login-link {
  text-align: center;
  margin-top: 20px;
}

.login-link a {
  color: #4CAF50;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style> 