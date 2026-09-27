<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <span class="admin-logo">⚙️</span>
        <span class="admin-title">心灵互联 · 管控台</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :router="true"
        class="admin-menu"
        text-color="#8A9BA5"
        active-text-color="#FFFFFF"
        background-color="transparent"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/posts">
          <el-icon><ChatDotSquare /></el-icon>
          <span>帖子审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><Comment /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button link class="back-btn" @click="goUserSide">
          <el-icon><Back /></el-icon>
          返回用户端
        </el-button>
        <div class="admin-user">
          <div class="admin-avatar">管</div>
          <span class="admin-name">{{ sessionStore.displayName }}</span>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="admin-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataBoard, User, Document, ChatDotSquare, Comment, Back } from '@element-plus/icons-vue'
import { useSessionStore } from '@/stores/session'

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()

const activeMenu = computed(() => route.path)

const goUserSide = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: #F0F2F5;
  font-family: 'Outfit', -apple-system, sans-serif;
}

/* 侧边栏 */
.admin-sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1A2623 0%, #2F4D44 100%);
  display: flex;
  flex-direction: column;
  padding: 0;
  flex-shrink: 0;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.08);
}

.sidebar-header {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.admin-logo {
  font-size: 24px;
  margin-right: 12px;
}

.admin-title {
  font-size: 16px;
  font-weight: 600;
  color: #FFFFFF;
  letter-spacing: 0.5px;
}

/* 菜单 */
.admin-menu {
  flex: 1;
  border: none;
  padding: 16px 12px;
}

.admin-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  border-radius: 12px;
  margin-bottom: 4px;
  padding-left: 20px !important;
  font-size: 14px;
  transition: all 0.25s ease;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.15) !important;
  font-weight: 600;
}

.admin-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 12px;
}

/* 脚部 */
.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.back-btn {
  color: #8A9BA5 !important;
  font-size: 13px;
  margin-bottom: 16px;
  transition: color 0.2s ease;
}

.back-btn:hover {
  color: #FFFFFF !important;
}

.admin-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.15);
  color: #FFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.admin-name {
  font-size: 13px;
  color: #B0C0C5;
}

/* 主区域 */
.admin-main {
  flex: 1;
  overflow-y: auto;
  padding: 32px;
  background: #F0F2F5;
}

/* 路由切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.25s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.fade-slide-leave-to {
  opacity: 0;
}
</style>
