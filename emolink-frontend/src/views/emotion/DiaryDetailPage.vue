<template>
  <div class="page-container glass-theme">
    <div class="header-actions">
      <el-button class="premium-button text-btn" @click="$router.back()">
        <el-icon class="mr-1"><ArrowLeft /></el-icon> 返回足迹
      </el-button>
    </div>

    <el-skeleton :loading="loading" animated class="glass-surface">
      <template #template>
        <div style="padding: 14px">
          <el-skeleton-item variant="p" style="width: 50%" />
          <div style="display: flex; align-items: center; margin-top: 16px; height: 16px;">
            <el-skeleton-item variant="text" style="margin-right: 16px" />
            <el-skeleton-item variant="text" style="width: 30%" />
          </div>
        </div>
      </template>

      <template #default>
        <div class="diary-detail-card glass-surface" v-if="diary">
          <div class="detail-header">
            <div class="emoji-hero" :style="{ background: emotionMeta.bgColor }">
              {{ emotionMeta.emoji }}
            </div>
            <div class="header-info">
              <h2 class="diary-title">
                {{ emotionMeta.label }}
                <span class="level-badge" :style="{ borderColor: emotionMeta.color, color: emotionMeta.color }">
                  强度: {{ diary.emotionLevel }} / 5
                </span>
              </h2>
              <div class="diary-meta">
                <span>{{ formattedCreatedAt }}</span>
              </div>
            </div>
          </div>

          <div class="detail-body">
            <div class="trigger-box" v-if="diary.triggerEvent">
              <h4 class="box-label">触发事件</h4>
              <p class="box-content">{{ diary.triggerEvent }}</p>
            </div>

            <div class="feeling-box">
              <h4 class="box-label">内心感受</h4>
              <p class="box-content feeling-text">{{ diary.feeling }}</p>
            </div>
          </div>

          <div class="ai-feedback-box" v-if="diary.aiSuggestion">
            <div class="ai-icon">✨</div>
            <div class="ai-content-wrap">
              <h4 class="ai-title">来自心海的回响</h4>
              <p class="ai-text">{{ diary.aiSuggestion }}</p>
            </div>
          </div>
        </div>

        <div v-else class="glass-surface empty-state">
          当前记录不存在，可能已经被删除。
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getDiaryDetail } from '@/api/emotion'
import { EMOTION_MAP } from '@/utils/emotion'
import { formatDateTime } from '@/utils/format'

const route = useRoute()
const loading = ref(true)
const diary = ref(null)

const emotionMeta = computed(() => {
  return EMOTION_MAP[diary.value?.emotionType] || {
    label: '未知情绪',
    emoji: '🍃',
    color: '#798C87',
    bgColor: 'rgba(160, 176, 171, 0.16)',
  }
})

const formattedCreatedAt = computed(() => formatDateTime(diary.value?.createdAt))

const loadDiary = async () => {
  loading.value = true
  try {
    const { data } = await getDiaryDetail(route.params.id)
    diary.value = data
  } catch (error) {
    diary.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDiary()
})
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
}

.header-actions {
  margin-bottom: 24px;
}

.text-btn {
  background: transparent;
  border: none;
  color: #5B6B67;
  padding: 0;
  height: auto;
  font-size: 15px;
}

.text-btn:hover {
  color: #1A2623;
  box-shadow: none;
  background: transparent;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 32px;
  padding: 48px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.03);
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
  border-bottom: 1px solid rgba(0,0,0,0.05);
  padding-bottom: 32px;
}

.emoji-hero {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin-right: 24px;
}

.header-info {
  flex: 1;
}

.diary-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  color: #1A2623;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.level-badge {
  font-family: 'Outfit';
  font-size: 14px;
  font-weight: 500;
  margin-left: 16px;
  padding: 2px 10px;
  border: 1px solid currentColor;
  border-radius: 12px;
}

.diary-meta {
  color: #798C87;
  font-size: 14px;
  font-family: 'Outfit';
}

.detail-body {
  margin-bottom: 48px;
}

.box-label {
  font-size: 14px;
  color: #A0B0AB;
  margin-bottom: 12px;
  font-weight: 500;
}

.box-content {
  font-size: 16px;
  color: #2C3634;
  line-height: 1.8;
}

.trigger-box {
  background: rgba(255, 255, 255, 0.4);
  padding: 20px 24px;
  border-radius: 16px;
  margin-bottom: 32px;
}

.feeling-box {
  padding: 0 12px;
}

.feeling-text {
  font-size: 18px;
  color: #1A2623;
}

.ai-feedback-box {
  background: linear-gradient(135deg, rgba(232, 211, 195, 0.4) 0%, rgba(212, 207, 203, 0.2) 100%);
  border: 1px solid rgba(232, 211, 195, 0.6);
  border-radius: 24px;
  padding: 40px;
  display: flex;
  position: relative;
  overflow: hidden;
}

.ai-icon {
  font-size: 32px;
  margin-right: 24px;
}

.ai-content-wrap {
  flex: 1;
}

.ai-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 20px;
  color: #554A42;
  margin-bottom: 16px;
}

.ai-text {
  font-size: 15px;
  color: #6C5F56;
  line-height: 1.8;
}

.empty-state {
  color: #798C87;
  text-align: center;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .glass-surface { padding: 24px; border-radius: 24px; }
  .ai-feedback-box { flex-direction: column; padding: 24px; }
  .ai-icon { margin-bottom: 16px; margin-right: 0; }
}
</style>
