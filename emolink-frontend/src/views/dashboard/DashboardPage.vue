<template>
  <div class="dashboard-container" v-loading="loading">
    <div class="welcome-header">
      <div class="header-content">
        <h1 class="greeting">{{ greetingText }}</h1>
        <p class="date-text">{{ currentDate }} · 已连续记录 {{ dashboard?.consecutiveDays || 0 }} 天</p>
      </div>
      <div class="header-action">
        <el-button class="premium-button" @click="$router.push('/emotion/create')">
          <el-icon class="mr-1"><EditPen /></el-icon> 记录此刻
        </el-button>
      </div>
    </div>

    <div class="dashboard-grid" v-if="dashboard">
      <div class="grid-card glass-surface recent-card">
        <h3 class="card-title"><el-icon><Compass /></el-icon> 今天的情绪概览</h3>
        <template v-if="dashboard.todayEmotion?.diaryCount">
          <div class="recent-emotion">
            <div class="emoji-circle">{{ todayEmotionMeta.emoji }}</div>
            <div class="emotion-info">
              <h4>{{ todayEmotionMeta.label }}</h4>
              <p>今天记录了 {{ dashboard.todayEmotion.diaryCount }} 次，平均强度 {{ dashboard.todayEmotion.avgLevel }} / 5。</p>
              <span class="time-stamp">累计写下 {{ dashboard.totalDiaryCount }} 条情绪日记</span>
            </div>
          </div>
        </template>
        <div v-else class="card-empty">
          还没有留下今天的情绪片段，先写下第一条记录吧。
        </div>
      </div>

      <div class="grid-card glass-surface calendar-card">
        <h3 class="card-title"><el-icon><Calendar /></el-icon> 本周情绪强度</h3>
        <div class="spectrum-container">
          <div class="day-slot" v-for="day in weeklyTrend" :key="day.date">
            <span class="day-label">{{ day.label }}</span>
            <div class="day-dot" :style="{ background: day.color, opacity: day.avgLevel ? 1 : 0.35 }"></div>
            <span class="day-value">{{ day.avgLevel ? `${day.avgLevel}` : '-' }}</span>
          </div>
        </div>
        <div class="card-footer">
          记录越稳定，情绪变化的轮廓就越清晰。
        </div>
      </div>

      <div class="grid-card glass-surface feed-card">
        <div class="feed-columns">
          <div class="feed-block">
            <h3 class="card-title"><el-icon><Reading /></el-icon> 为你推荐</h3>
            <div v-if="dashboard.recommendArticles?.length" class="list-stack">
              <button
                v-for="article in dashboard.recommendArticles"
                :key="article.id"
                type="button"
                class="feed-item"
                @click="$router.push(`/knowledge/detail/${article.id}`)"
              >
                <div class="feed-item-title">{{ article.title }}</div>
                <div class="feed-item-meta">{{ articleCategoryMeta(article.category).label }}</div>
              </button>
            </div>
            <div v-else class="card-empty small">写下几条情绪记录后，这里会出现更贴近你的文章。</div>
          </div>

          <div class="feed-block">
            <h3 class="card-title"><el-icon><Microphone /></el-icon> 树洞回声</h3>
            <div v-if="dashboard.hotPosts?.length" class="list-stack">
              <button
                v-for="post in dashboard.hotPosts"
                :key="post.id"
                type="button"
                class="feed-item"
                @click="$router.push(`/treehole/detail/${post.id}`)"
              >
                <div class="feed-item-title">{{ post.content }}</div>
                <div class="feed-item-meta">{{ post.likeCount }} 次共鸣 · {{ post.commentCount }} 条评论</div>
              </button>
            </div>
            <div v-else class="card-empty small">树洞里还很安静，等第一批心声落下之后再来看看。</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { EditPen, Compass, Calendar, Reading, Microphone } from '@element-plus/icons-vue'
import { getDashboard } from '@/api/dashboard'
import { EMOTION_MAP } from '@/utils/emotion'
import { formatFriendlyDate } from '@/utils/format'
import { getArticleCategoryMeta } from '@/utils/article'
import { useSessionStore } from '@/stores/session'

const sessionStore = useSessionStore()
const loading = ref(false)
const dashboard = ref(null)

const currentDate = computed(() => formatFriendlyDate(new Date()))

const greetingText = computed(() => {
  const hour = dayjs().hour()
  const prefix = hour < 12 ? '早安' : hour < 18 ? '午安' : '晚上好'
  return `${prefix}，${sessionStore.displayName}`
})

const todayEmotionMeta = computed(() => {
  const type = dashboard.value?.todayEmotion?.dominantEmotion
  return EMOTION_MAP[type] || {
    label: '待记录',
    emoji: '🍃',
    color: '#A0B0AB',
  }
})

const weeklyTrend = computed(() => {
  return (dashboard.value?.weeklyTrend || []).map((item) => ({
    ...item,
    label: dayjs(item.date).format('dd'),
    color: resolveTrendColor(item.avgLevel),
  }))
})

const resolveTrendColor = (avgLevel) => {
  if (!avgLevel) return '#E2E8E6'
  if (avgLevel >= 4) return EMOTION_MAP.anxious.color
  if (avgLevel >= 3) return EMOTION_MAP.happy.color
  if (avgLevel >= 2) return EMOTION_MAP.calm.color
  return '#A0B0AB'
}

const articleCategoryMeta = (category) => getArticleCategoryMeta(category)

const loadDashboard = async () => {
  loading.value = true
  try {
    const { data } = await getDashboard()
    dashboard.value = data
  } catch (error) {
    dashboard.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeIn 0.6s ease-out;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding: 32px 40px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.02);
}

.greeting {
  font-family: 'Noto Serif SC', serif;
  font-size: 32px;
  color: #1A2623;
  margin-bottom: 8px;
}

.date-text {
  font-size: 15px;
  color: #5B6B67;
}

.premium-button {
  height: 48px;
  padding: 0 32px;
  border-radius: 24px;
  background-color: #2F4D44;
  color: #FFF;
  font-size: 16px;
  border: none;
  box-shadow: 0 8px 16px rgba(47, 77, 68, 0.15);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.premium-button:hover {
  background-color: #1A2623;
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(26, 38, 35, 0.25);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.45);
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;
  padding: 32px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.03), inset 0 0 0 1px rgba(255, 255, 255, 0.4);
}

.card-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #2F4D44;
  margin-bottom: 24px;
  font-weight: 600;
}

.card-title .el-icon {
  margin-right: 8px;
  font-size: 20px;
}

.recent-emotion {
  display: flex;
  gap: 20px;
}

.emoji-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(107, 203, 119, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  flex-shrink: 0;
}

.emotion-info h4 {
  font-size: 18px;
  color: #1A2623;
  margin-bottom: 8px;
}

.emotion-info p {
  font-size: 14px;
  color: #5B6B67;
  line-height: 1.6;
  margin-bottom: 12px;
}

.time-stamp {
  font-size: 12px;
  color: #A0B0AB;
}

.spectrum-container {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  margin-bottom: 24px;
  gap: 12px;
}

.day-slot {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.day-label {
  font-size: 14px;
  color: #798C87;
}

.day-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.day-value {
  font-size: 12px;
  color: #798C87;
}

.card-footer {
  font-size: 13px;
  color: #A0B0AB;
  text-align: center;
  border-top: 1px solid rgba(0,0,0,0.05);
  padding-top: 20px;
}

.feed-card {
  grid-column: span 2;
}

.feed-columns {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.feed-block {
  min-width: 0;
}

.list-stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feed-item {
  width: 100%;
  text-align: left;
  border: 1px solid rgba(255, 255, 255, 0.75);
  background: rgba(255, 255, 255, 0.55);
  border-radius: 16px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.feed-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.06);
}

.feed-item-title {
  color: #1A2623;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.feed-item-meta {
  color: #798C87;
  font-size: 12px;
}

.card-empty {
  color: #798C87;
  line-height: 1.7;
}

.card-empty.small {
  font-size: 14px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 960px) {
  .welcome-header,
  .dashboard-grid,
  .feed-columns {
    grid-template-columns: 1fr;
    display: grid;
  }

  .welcome-header {
    gap: 20px;
  }

  .feed-card {
    grid-column: span 1;
  }
}
</style>
