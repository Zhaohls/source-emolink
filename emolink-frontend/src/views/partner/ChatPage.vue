<template>
  <div class="page-container">
    <div class="glass-surface chat-shell" v-loading="booting">
      <template v-if="hasActiveMatch">
        <div class="chat-header">
          <div>
            <h1 class="chat-title">正在与 {{ partnerName }} 通信</h1>
            <p class="chat-subtitle">{{ ended ? '这次对话已经结束' : socketReady ? '信箱已连接' : '正在连接信箱...' }}</p>
          </div>
          <div class="chat-actions">
            <el-button @click="loadMessages">刷新消息</el-button>
            <el-button type="danger" plain :loading="ending" @click="handleEndMatch">结束对话</el-button>
          </div>
        </div>

        <div class="message-list" ref="messageListRef">
          <div
            v-for="message in messages"
            :key="message.id"
            class="message-row"
            :class="{ self: message.isSelf }"
          >
            <div class="message-bubble">
              <div class="message-content">{{ message.content }}</div>
              <div class="message-time">{{ message.createdAtText }}</div>
            </div>
          </div>

          <div v-if="!messages.length" class="empty-message">
            还没有消息，写下第一句问候吧。
          </div>
        </div>

        <div class="composer" v-if="!ended">
          <el-input
            v-model="draft"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="写点什么发送给你的匿名伙伴"
            @keyup.ctrl.enter="sendMessage"
          />
          <div class="composer-actions">
            <span class="composer-tip">Ctrl + Enter 发送</span>
            <el-button type="primary" :loading="sending" :disabled="!socketReady" @click="sendMessage">发送</el-button>
          </div>
        </div>

        <div class="ended-banner" v-else>
          这次通信已经结束，可以回到匹配页重新开始。
        </div>
      </template>

      <div v-else class="empty-state">
        <h2>当前没有有效匹配</h2>
        <p>先去匹配一位伙伴，再回来开启信件。</p>
        <el-button type="primary" @click="$router.push('/partner/match')">前往匹配</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { endPartnerMatch, getPartnerMatchStatus, getPartnerMessageList } from '@/api/partner'
import { formatRelativeTime } from '@/utils/format'
import { createPartnerSocketClient, sendPartnerSocketMessage } from '@/utils/partnerSocket'
import { useSessionStore } from '@/stores/session'
import { showError, showInfo, showSuccess, showWarning } from '@/utils/feedback'

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()

const booting = ref(false)
const sending = ref(false)
const ending = ref(false)
const socketReady = ref(false)
const ended = ref(false)
const matchStatus = ref(null)
const messages = ref([])
const draft = ref('')
const messageListRef = ref(null)

let socketClient = null

const currentMatchId = computed(() => {
  const fromRoute = Number(route.query.matchId)
  if (fromRoute) return fromRoute
  return Number(matchStatus.value?.matchId || 0)
})

const hasActiveMatch = computed(() => matchStatus.value?.status === 'matched')
const partnerName = computed(() => matchStatus.value?.partnerAnonymousName || '匿名旅人')

const normalizeMessage = (message) => ({
  ...message,
  createdAtText: formatRelativeTime(message.createdAt),
})

const scrollToBottom = () => {
  if (!messageListRef.value) return
  messageListRef.value.scrollTop = messageListRef.value.scrollHeight
}

const disconnectSocket = () => {
  socketReady.value = false
  if (socketClient) {
    socketClient.deactivate()
    socketClient = null
  }
}

const upsertMessage = async (message) => {
  if (messages.value.some((item) => String(item.id) === String(message.id))) {
    return
  }
  messages.value.push(normalizeMessage(message))
  messages.value.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  await nextTick()
  scrollToBottom()
}

const connectSocket = () => {
  if (socketClient || !sessionStore.token || !hasActiveMatch.value) return
  socketClient = createPartnerSocketClient({
    token: sessionStore.token,
    onConnect: () => {
      socketReady.value = true
    },
    onMessage: (message) => {
      if (Number(message.matchId) === currentMatchId.value) {
        upsertMessage(message)
      }
    },
    onMatchEvent: (event) => {
      if (Number(event.matchId) !== currentMatchId.value) return
      if (event.type === 'ENDED') {
        ended.value = true
        socketReady.value = false
        void showInfo(event.message || '本次对话已结束')
      }
    },
    onError: (message) => {
      socketReady.value = false
      void showWarning(message)
    },
  })
}

const loadStatus = async () => {
  const { data } = await getPartnerMatchStatus()
  matchStatus.value = data
  ended.value = data?.status !== 'matched'
  if (!currentMatchId.value && data?.matchId) {
    router.replace({
      path: route.path,
      query: { matchId: data.matchId },
    })
  }
  return data
}

const loadMessages = async () => {
  if (!currentMatchId.value) return
  try {
    const { data } = await getPartnerMessageList({
      matchId: currentMatchId.value,
      pageNum: 1,
      pageSize: 100,
    })
    messages.value = (data.records || []).map(normalizeMessage)
    await nextTick()
    scrollToBottom()
  } catch (error) {
    messages.value = []
  }
}

const bootstrap = async () => {
  booting.value = true
  try {
    const status = await loadStatus()
    if (status?.status === 'matched') {
      await loadMessages()
      connectSocket()
    } else {
      disconnectSocket()
    }
  } finally {
    booting.value = false
  }
}

const sendMessage = async () => {
  if (!draft.value.trim()) {
    await showWarning('写点什么再发送吧')
    return
  }
  if (!socketReady.value || !socketClient) {
    await showWarning('信箱还在连接中，请稍候')
    return
  }

  const content = draft.value.trim()
  const tempId = `local-${Date.now()}`
  draft.value = ''
  messages.value.push(normalizeMessage({
    id: tempId,
    matchId: currentMatchId.value,
    isSelf: true,
    content,
    createdAt: new Date().toISOString(),
  }))
  await nextTick()
  scrollToBottom()

  sending.value = true
  try {
    sendPartnerSocketMessage(socketClient, {
      matchId: currentMatchId.value,
      content,
    })
    window.setTimeout(() => {
      loadMessages()
    }, 400)
  } catch (error) {
    messages.value = messages.value.filter((item) => item.id !== tempId)
    await showError(error.message || '发送失败')
  } finally {
    sending.value = false
  }
}

const handleEndMatch = async () => {
  if (!currentMatchId.value) return
  ending.value = true
  try {
    await endPartnerMatch({ matchId: currentMatchId.value })
    ended.value = true
    matchStatus.value = { ...matchStatus.value, status: 'ended' }
    disconnectSocket()
    await showSuccess('对话已结束')
  } catch (error) {
    // 请求层统一提示
  } finally {
    ending.value = false
  }
}

onMounted(() => {
  bootstrap()
})

onUnmounted(() => {
  disconnectSocket()
})
</script>

<style scoped>
.page-container {
  max-width: 980px;
  margin: 0 auto;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  padding: 24px;
}

.chat-shell {
  min-height: 720px;
  display: flex;
  flex-direction: column;
}

.chat-header,
.chat-actions,
.composer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.chat-header {
  margin-bottom: 20px;
}

.chat-title {
  font-family: 'Noto Serif SC', serif;
  color: #1A2623;
  margin: 0 0 8px;
}

.chat-subtitle,
.composer-tip {
  color: #798C87;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.message-row {
  display: flex;
  justify-content: flex-start;
}

.message-row.self {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 72%;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.message-row.self .message-bubble {
  background: rgba(47, 77, 68, 0.12);
}

.message-content {
  color: #1A2623;
  line-height: 1.8;
}

.message-time {
  margin-top: 8px;
  color: #798C87;
  font-size: 12px;
  text-align: right;
}

.composer {
  padding-top: 18px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.composer-actions {
  margin-top: 12px;
}

.ended-banner,
.empty-message,
.empty-state {
  color: #798C87;
  text-align: center;
}

.ended-banner {
  padding-top: 20px;
}

.empty-state {
  margin: auto;
}
</style>
