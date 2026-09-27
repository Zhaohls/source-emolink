<template>
  <div class="app-wrapper">
    <el-container class="app-container">
      <el-aside width="260px" class="glass-sidebar">
        <div class="logo-box">
          <span class="logo-icon">✨</span>
          <span class="logo-text">心灵互联</span>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="premium-menu"
          router
          :collapse="false"
        >
          <div class="menu-group">
            <div class="menu-group-title">情绪空间</div>
            <el-menu-item index="/dashboard">
              <el-icon><Odometer /></el-icon>
              <template #title>心灵栖息地</template>
            </el-menu-item>
            <el-menu-item index="/emotion/create">
              <el-icon><EditPen /></el-icon>
              <template #title>记录当下</template>
            </el-menu-item>
            <el-menu-item index="/emotion/list">
              <el-icon><Collection /></el-icon>
              <template #title>我的足迹</template>
            </el-menu-item>
            <el-menu-item index="/emotion/calendar">
              <el-icon><Calendar /></el-icon>
              <template #title>情绪日历</template>
            </el-menu-item>
          </div>

          <div class="menu-group">
            <div class="menu-group-title">洞察与连接</div>
            <el-menu-item index="/report/daily">
              <el-icon><DataLine /></el-icon>
              <template #title>情绪日报</template>
            </el-menu-item>
            <el-menu-item index="/report/weekly">
              <el-icon><DataLine /></el-icon>
              <template #title>情绪周报</template>
            </el-menu-item>
            <el-menu-item index="/knowledge/list">
              <el-icon><Reading /></el-icon>
              <template #title>心理知识库</template>
            </el-menu-item>
            <el-menu-item index="/treehole/list">
              <el-icon><Microphone /></el-icon>
              <template #title>匿名树洞</template>
            </el-menu-item>
            <el-menu-item index="/partner/match">
              <el-icon><Message /></el-icon>
              <template #title>伙伴信箱</template>
            </el-menu-item>
          </div>

          <div class="menu-group">
            <div class="menu-group-title">我的</div>
            <el-menu-item index="/profile/info">
              <el-icon><UserFilled /></el-icon>
              <template #title>个人资料</template>
            </el-menu-item>
            <el-menu-item index="/profile/favorites">
              <el-icon><Star /></el-icon>
              <template #title>我的收藏</template>
            </el-menu-item>
            <el-menu-item index="/profile/password">
              <el-icon><Key /></el-icon>
              <template #title>修改密码</template>
            </el-menu-item>
          </div>
        </el-menu>
        
        <div class="sidebar-footer">
          <div class="user-profile">
            <el-avatar :size="40" :src="sessionStore.avatarUrl || undefined" class="premium-avatar">
              {{ sessionStore.avatarText }}
            </el-avatar>
            <div class="user-info">
              <span class="user-name">{{ sessionStore.displayName }}</span>
              <span class="user-status">在线</span>
            </div>
            <el-button link @click="handleLogout" class="logout-btn">
              <el-icon><SwitchButton /></el-icon>
            </el-button>
          </div>
        </div>
      </el-aside>
      
      <el-container class="main-container">
        <el-main class="custom-main scroll-hidden">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Odometer, EditPen, Collection, Calendar, 
  DataLine, Reading, Microphone, Message,
  SwitchButton, UserFilled, Star, Key
} from '@element-plus/icons-vue'
import { useSessionStore } from '@/stores/session'
import { showConfirm, showSuccess } from '@/utils/feedback'

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()

const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {
  if (sessionStore.isAuthenticated) {
    sessionStore.fetchUser().catch(() => {})
  }
})

const handleLogout = async () => {
  try {
    await showConfirm('要离开您的专属空间了吗？', {
      title: '退出登录',
      confirmButtonText: '确定离开',
      cancelButtonText: '继续停留',
    })
    sessionStore.clearSession()
    await showSuccess('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700&family=Outfit:wght@300;400;600&display=swap');

/* 全局背景图控制 */
.app-wrapper {
  height: 100vh;
  width: 100vw;
  background: url('@/assets/app_bg.png') no-repeat center center / cover;
  background-color: #F4F1ED; /* 回落背景色 */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  box-sizing: border-box;
  font-family: 'Outfit', -apple-system, sans-serif;
  overflow: hidden;
}

.app-container {
  width: 100%;
  height: 100%;
  max-width: 1600px;
  gap: 24px;
}

/* 侧边栏晶透玻璃效果 */
.glass-sidebar {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05), inset 0 0 0 1px rgba(255, 255, 255, 0.5);
  overflow: hidden;
}

.logo-box {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 0 32px;
  color: #1A2623;
  font-family: 'Noto Serif SC', serif;
}

.logo-icon {
  font-size: 28px;
  margin-right: 12px;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
}

/* 菜单定制 */
.premium-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 16px 20px;
  overflow-y: auto;
}

.premium-menu::-webkit-scrollbar {
  display: none;
}

.menu-group {
  margin-bottom: 32px;
}

.menu-group-title {
  font-size: 13px;
  color: #798C87;
  padding: 0 16px;
  margin-bottom: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}

.premium-menu .el-menu-item {
  height: 48px;
  line-height: 48px;
  border-radius: 14px;
  margin-bottom: 8px;
  color: #5B6B67;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.premium-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.7);
  color: #1A2623;
}

.premium-menu .el-menu-item.is-active {
  background: #FFF;
  color: #2F4D44;
  box-shadow: 0 4px 12px rgba(47, 77, 68, 0.08);
  font-weight: 600;
}

.premium-menu .el-menu-item.is-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.premium-menu .el-icon {
  font-size: 18px;
  margin-right: 10px;
}

.sidebar-footer {
  padding: 24px;
  background: linear-gradient(to bottom, rgba(255,255,255,0) 0%, rgba(255,255,255,0.4) 100%);
  border-top: 1px solid rgba(255,255,255,0.3);
}

.user-profile {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
}

.premium-avatar {
  background: #2F4D44;
  color: #FFF;
  font-weight: 600;
}

.user-info {
  margin-left: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1A2623;
  margin-bottom: 2px;
}

.user-status {
  font-size: 12px;
  color: #6BCB77;
}

.logout-btn {
  color: #798C87;
  font-size: 18px;
}

.logout-btn:hover {
  color: #FF6B6B;
}

/* 主内容区域，保持轻量透明 */
.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 28px;
}

.custom-main {
  padding: 0;
  height: 100%;
}

.scroll-hidden::-webkit-scrollbar {
  width: 0;
  display: none;
}

/* 路由转场动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
