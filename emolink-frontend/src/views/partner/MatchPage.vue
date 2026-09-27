<template>
  <div class="page-container glass-theme">
    <div class="page-header text-center">
      <h1 class="page-title">伙伴信箱 / 灵魂匹配</h1>
      <p class="page-subtitle">遇见频率相同的灵魂，交换彼此的温暖</p>
    </div>

    <div class="match-center glass-surface" v-loading="loading">
      <div class="radar-container" v-if="isWaiting">
        <div class="radar-scan"></div>
        <el-avatar :size="84" :src="sessionStore.avatarUrl || undefined" class="center-avatar">
          {{ sessionStore.avatarText }}
        </el-avatar>
        <p class="matching-text">已经进入匹配队列，正在替你寻找频率更接近的人...</p>
        <el-button plain class="premium-button-outline" @click="cancelMatch">取消本次匹配</el-button>
      </div>

      <div class="success-container" v-else-if="isMatched">
        <div class="match-avatars">
          <el-avatar :size="72" :src="sessionStore.avatarUrl || undefined" class="avatar-mine">
            {{ sessionStore.avatarText }}
          </el-avatar>
          <div class="match-heart"></div>
          <div class="avatar-partner">{{ partnerInitial }}</div>
        </div>
        <h2 class="match-title">遇见了「{{ partnerName }}」</h2>
        <p class="match-desc">匹配已经建立，现在可以直接进入信箱开始交流。</p>

        <div class="action-group">
          <el-button class="premium-button primary" @click="startChat">开启信件</el-button>
          <el-button plain class="premium-button-outline" @click="cancelMatch">结束当前匹配</el-button>
        </div>
      </div>

      <div class="init-container" v-else>
        <div class="orb-visual">
          <div class="orb"></div>
        </div>
        <h2 class="init-title">准备好开启一段奇妙的连接了吗？</h2>
        <p class="init-desc">系统会根据最近的情绪记录，为你匹配一位匿名伙伴。</p>
        <el-button class="premium-button start-btn" :loading="loading" @click="startMatch">
          <el-icon class="mr-2"><Promotion /></el-icon> 投递匹配信标
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Promotion } from '@element-plus/icons-vue'
import { endPartnerMatch, getPartnerMatchStatus, joinPartnerMatch } from '@/api/partner'
import { getInitial } from '@/utils/format'
import { useSessionStore } from '@/stores/session'
import { showSuccess } from '@/utils/feedback'

const router = useRouter()
const sessionStore = useSessionStore()
const loading = ref(false)
const matchStatus = ref({ status: 'none' })
let pollTimer = null

const isWaiting = computed(() => matchStatus.value?.status === 'waiting')
const isMatched = computed(() => matchStatus.value?.status === 'matched')
const partnerName = computed(() => matchStatus.value?.partnerAnonymousName || '匿名旅人')
const partnerInitial = computed(() => getInitial(partnerName.value))

const clearPolling = () => {
  if (pollTimer) {
    window.clearInterval(pollTimer)
    pollTimer = null
  }
}

const applyStatus = (data) => {
  matchStatus.value = data || { status: 'none' }
  if (data?.status === 'waiting') {
    clearPolling()
    pollTimer = window.setInterval(refreshStatus, 3000)
  } else {
    clearPolling()
  }
}

const refreshStatus = async () => {
  try {
    const { data } = await getPartnerMatchStatus()
    applyStatus(data)
  } catch (error) {
    clearPolling()
  }
}

const startMatch = async () => {
  loading.value = true
  try {
    const { data } = await joinPartnerMatch()
    applyStatus(data)
    await showSuccess(data.status === 'matched' ? '匹配成功' : '已进入匹配队列')
  } catch (error) {
    // 请求层统一提示
  } finally {
    loading.value = false
  }
}

const cancelMatch = async () => {
  if (!matchStatus.value?.matchId) {
    applyStatus({ status: 'none' })
    return
  }
  loading.value = true
  try {
    await endPartnerMatch({ matchId: matchStatus.value.matchId })
    applyStatus({ status: 'none' })
    await showSuccess('已结束当前匹配')
  } catch (error) {
    // 请求层统一提示
  } finally {
    loading.value = false
  }
}

const startChat = () => {
  if (!matchStatus.value?.matchId) return
  router.push({
    path: '/partner/chat',
    query: { matchId: matchStatus.value.matchId },
  })
}

onMounted(() => {
  refreshStatus()
})

onUnmounted(() => {
  clearPolling()
})
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
}

.text-center {
  text-align: center;
}

.page-header {
  margin-bottom: 40px;
}

.page-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 32px;
  color: #1A2623;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #798C87;
  font-size: 15px;
}

.match-center {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 40px;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.05);
  padding: 40px;
}

.init-container,
.radar-container,
.success-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.orb-visual {
  height: 120px;
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.orb {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.8), rgba(107, 203, 119, 0.5));
  box-shadow: 0 0 40px rgba(107, 203, 119, 0.4);
}

.init-title,
.match-title {
  font-size: 24px;
  color: #1A2623;
  margin-bottom: 16px;
  font-weight: 600;
}

.init-desc,
.match-desc,
.matching-text {
  font-size: 16px;
  color: #5B6B67;
  line-height: 1.6;
  margin-bottom: 28px;
  max-width: 420px;
}

.radar-scan {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: 1px solid rgba(47, 77, 68, 0.2);
  position: relative;
  margin-bottom: 40px;
  background: rgba(47, 77, 68, 0.03);
}

.radar-scan::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 1px solid rgba(47, 77, 68, 0.1);
  animation: ripple 1.5s linear infinite;
}

.center-avatar {
  position: absolute;
  width: 60px;
  height: 60px;
  background: #2F4D44;
  border-radius: 50%;
  color: #FFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 20px;
}

.match-avatars {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
}

.avatar-mine,
.avatar-partner {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.avatar-mine {
  background: #2F4D44;
  color: #FFF;
  font-size: 24px;
}

.avatar-partner {
  background: rgba(255,255,255,0.8);
  border: 1px solid rgba(0,0,0,0.05);
  color: #1A2623;
}

.action-group {
  display: flex;
  gap: 16px;
}

.premium-button {
  background: #2F4D44;
  color: #FFF;
  border: none;
  border-radius: 18px;
  padding: 0 24px;
  min-height: 44px;
}

.premium-button-outline {
  border-radius: 18px;
}

.match-heart {
  width: 32px;
  height: 32px;
  background: rgba(255, 107, 107, 0.15);
  border-radius: 50%;
}

@keyframes ripple {
  from { transform: scale(0.9); opacity: 1; }
  to { transform: scale(1.08); opacity: 0; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
