<template>
  <div class="login-container">
    <!-- 背景光斑动画元素，用于增加高级层次感 -->
    <div class="ambient-blob blob-1"></div>
    <div class="ambient-blob blob-2"></div>
    <div class="ambient-blob blob-3"></div>

    <div class="glass-card">
      <div class="login-left">
        <div class="brand-content">
          <div class="logo-wrapper">
            <span class="emoji">🍃</span> 
            <span class="brand-name">心灵互联</span>
          </div>
          <h1 class="brand-slogan">
            感知情绪的<br/>每一毫米。
          </h1>
          <p class="brand-subtitle">
            一个宁静的角落，陪你卸下防备，倾听内心最真实的声音。
          </p>
        </div>
      </div>
      
      <div class="login-right">
        <div class="form-wrapper">
          <h2 class="form-title">登录</h2>
          <p class="form-desc">欢迎回到您的专属空间</p>
          
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="premium-form">
            <el-form-item prop="username">
              <div class="input-group">
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="用户名" 
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon class="input-icon"><User /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>
            <el-form-item prop="password">
              <div class="input-group">
                <el-input 
                  v-model="loginForm.password" 
                  type="password" 
                  placeholder="密码" 
                  show-password
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon class="input-icon"><Lock /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>
            
            <el-form-item class="action-item">
              <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
                进入空间
              </el-button>
            </el-form-item>
            
            <div class="form-footer">
              <span class="text-secondary">新访客？</span>
              <router-link to="/register" class="link-primary">建立档案 <el-icon><Right /></el-icon></router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Right } from '@element-plus/icons-vue'
import { loginUser } from '@/api/user'
import { useSessionStore } from '@/stores/session'
import { showSuccess } from '@/utils/feedback'

const router = useRouter()
const sessionStore = useSessionStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    await loginFormRef.value.validate()
  } catch (error) {
    return
  }

  loading.value = true
  try {
    const { data } = await loginUser(loginForm)
    sessionStore.setSession(data)
    const role = data?.role || 'user'
    await sessionStore.fetchUser(true).catch(() => {})
    await showSuccess('登录成功')
    // 根据角色跳转不同页面
    if (role === 'admin') {
      router.push('/admin/dashboard')
    } else {
      router.push('/dashboard')
    }
  } catch (error) {
    // 错误消息由请求层统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700&family=Outfit:wght@300;400;600&display=swap');

.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  align-items: center;
  justify-content: center;
  background-color: #F4F1ED; /* 高级燕麦色背景 */
  position: relative;
  overflow: hidden;
  font-family: 'Outfit', -apple-system, sans-serif;
}

/* 动态迷幻光斑效果 */
.ambient-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 1;
  opacity: 0.6;
  animation: floatBlobs 20s infinite alternate ease-in-out;
}

.blob-1 {
  width: 600px;
  height: 600px;
  background: #C4D7D1; /* 清冷灰青 */
  top: -100px;
  left: -100px;
}

.blob-2 {
  width: 500px;
  height: 500px;
  background: #E8D3C3; /* 暖杏色 */
  bottom: -50px;
  right: -50px;
  animation-delay: -5s;
}

.blob-3 {
  width: 400px;
  height: 400px;
  background: #D4CFCB; 
  top: 40%;
  left: 60%;
  animation-delay: -10s;
}

@keyframes floatBlobs {
  0% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(100px, 50px) scale(1.1); }
  100% { transform: translate(-50px, 100px) scale(0.9); }
}

/* 居中毛玻璃卡片 */
.glass-card {
  position: relative;
  z-index: 10;
  display: flex;
  width: 1100px;
  height: 650px;
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 32px;
  box-shadow: 0 40px 80px rgba(0, 0, 0, 0.05), inset 0 0 0 1px rgba(255, 255, 255, 0.5);
  overflow: hidden;
}

.login-left {
  flex: 1.1;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 80px;
  background: linear-gradient(135deg, rgba(255,255,255,0.3) 0%, rgba(255,255,255,0) 100%);
  border-right: 1px solid rgba(255,255,255,0.4);
}

.brand-content {
  color: #2C3634; /* 深青灰 */
  animation: fadeUp 1s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.emoji {
  font-size: 32px;
  margin-right: 12px;
}

.brand-name {
  font-size: 24px;
  font-weight: 600;
  letter-spacing: 1px;
}

.brand-slogan {
  font-family: 'Noto Serif SC', serif; /* 高级衬线字体 */
  font-size: 56px;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 24px;
  color: #1A2623;
}

.brand-subtitle {
  font-size: 16px;
  line-height: 1.8;
  color: #5B6B67;
  max-width: 80%;
  letter-spacing: 0.5px;
}

.login-right {
  flex: 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: rgba(255, 255, 255, 0.2);
}

.form-wrapper {
  width: 100%;
  max-width: 360px;
  animation: fadeLeft 1s cubic-bezier(0.16, 1, 0.3, 1) 0.2s forwards;
  opacity: 0;
}

.form-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 36px;
  font-weight: 700;
  color: #1A2623;
  margin-bottom: 8px;
}

.form-desc {
  color: #798C87;
  font-size: 14px;
  margin-bottom: 40px;
}

/* 深度定制表单组件 */
.premium-form :deep(.el-form-item) {
  margin-bottom: 28px;
}

.input-group {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.6) !important;
  box-shadow: none !important;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1) !important;
  border-radius: 0 !important;
  padding: 8px 0;
  transition: all 0.4s ease;
}

.custom-input :deep(.el-input__inner) {
  color: #1A2623;
  font-size: 15px;
  height: 40px;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-bottom-color: #2F4D44 !important; /* 主题深青色 */
  background: rgba(255, 255, 255, 0.9) !important;
}

.input-icon {
  font-size: 18px;
  color: #798C87;
  margin-right: 12px;
}

.action-item {
  margin-top: 48px;
  margin-bottom: 32px;
}

.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: 26px;
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 2px;
  background-color: #2F4D44; /* 深青雅灰 */
  border: none;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 10px 20px rgba(47, 77, 68, 0.2);
}

.submit-btn:hover {
  background-color: #1A2623;
  transform: translateY(-2px);
  box-shadow: 0 15px 25px rgba(26, 38, 35, 0.3);
}

.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
}

.text-secondary {
  color: #798C87;
  margin-right: 8px;
}

.link-primary {
  color: #2F4D44;
  text-decoration: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.link-primary .el-icon {
  margin-left: 4px;
  transition: transform 0.3s ease;
}

.link-primary:hover {
  color: #1A2623;
}

.link-primary:hover .el-icon {
  transform: translateX(4px);
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeLeft {
  from { opacity: 0; transform: translateX(40px); }
  to { opacity: 1; transform: translateX(0); }
}

@media (max-width: 1024px) {
  .glass-card {
    width: 90%;
    height: auto;
    flex-direction: column;
  }
  .login-left, .login-right {
    padding: 40px;
    border: none;
  }
}
</style>
