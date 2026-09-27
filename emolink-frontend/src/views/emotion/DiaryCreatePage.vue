<template>
  <div class="page-container glass-theme">
    <div class="page-header">
      <h1 class="page-title">倾听今天的你...</h1>
      <p class="page-subtitle">无论是阳光灿烂还是烟雨迷蒙，这里都会温柔接住你的所有情绪。</p>
    </div>

    <div class="glass-surface content-body">
      <el-form :model="diary" class="diary-form" @submit.prevent>
        <div class="section-block">
          <h3 class="section-title">
            <span class="step-num">01</span>
            此时此刻，你最主要的感受是？
          </h3>
          <div class="emotion-grid">
            <div
              v-for="(info, type) in EMOTION_MAP"
              :key="type"
              class="emotion-card"
              :class="{ 'is-active': diary.emotionType === type }"
              :style="{
                '--hover-color': info.color,
                '--bg-color': info.bgColor,
                '--border-color': info.borderColor
              }"
              @click="diary.emotionType = type"
            >
              <div class="emoji-wrapper">{{ info.emoji }}</div>
              <span class="emotion-label">{{ info.label }}</span>
              <div class="active-ring"></div>
            </div>
          </div>
        </div>

        <div class="section-block" v-if="diary.emotionType">
          <h3 class="section-title">
            <span class="step-num">02</span>
            这种感受有多强烈？
          </h3>
          <div class="slider-wrapper">
            <el-slider
              v-model="diary.emotionLevel"
              :min="1"
              :max="5"
              :step="1"
              :format-tooltip="(val) => EMOTION_LEVEL_LABELS[val]"
              class="premium-slider"
            />
            <div class="slider-labels">
              <span class="lvl-text">微风拂过 (1)</span>
              <span class="lvl-text active-lvl">{{ EMOTION_LEVEL_LABELS[diary.emotionLevel] }}</span>
              <span class="lvl-text">惊涛骇浪 (5)</span>
            </div>
          </div>
        </div>

        <div class="section-block" v-if="diary.emotionType">
          <h3 class="section-title">
            <span class="step-num">03</span>
            是什么触发了这种感受呢？
          </h3>
          <el-input
            v-model="diary.triggerEvent"
            placeholder="简短记录一下发生的事情（选填）"
            class="premium-input"
            maxlength="100"
            show-word-limit
          />
        </div>

        <div class="section-block" v-if="diary.emotionType">
          <h3 class="section-title">
            <span class="step-num">04</span>
            随便写点什么吧，不用管逻辑...
          </h3>
          <el-input
            v-model="diary.feeling"
            type="textarea"
            :rows="6"
            placeholder="在这里安全地倾倒你的思绪，我会慢慢听完..."
            class="premium-textarea"
          />
        </div>

        <div class="action-footer" v-if="diary.emotionType">
          <el-button class="submit-btn" :loading="loading" @click="submitDiary">
            <el-icon class="mr-2"><Promotion /></el-icon> 封存这刻记忆，听听心之回馈
          </el-button>
        </div>
      </el-form>
    </div>

    <el-dialog
      v-model="showAiFeedback"
      class="glass-dialog"
      width="600px"
      center
      :show-close="false"
      align-center
    >
      <div class="ai-feedback-container">
        <div class="ai-icon">✨</div>
        <h3 class="ai-title">来自心海的回响</h3>
        <p class="ai-content">{{ aiSuggestion }}</p>
        <div class="dialog-actions">
          <el-button class="premium-button" @click="finishDiary">收下这份温暖，返回日记本</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Promotion } from '@element-plus/icons-vue'
import { createDiary } from '@/api/emotion'
import { EMOTION_LEVEL_LABELS, EMOTION_MAP } from '@/utils/emotion'
import { showWarning } from '@/utils/feedback'

const router = useRouter()
const loading = ref(false)
const showAiFeedback = ref(false)
const aiSuggestion = ref('')

const diary = reactive({
  emotionType: '',
  emotionLevel: 3,
  triggerEvent: '',
  feeling: '',
})

const submitDiary = async () => {
  if (!diary.emotionType) {
    await showWarning('先选一种最贴近当下的情绪吧')
    return
  }
  if (!diary.feeling.trim()) {
    await showWarning('请至少写下一点点感受吧')
    return
  }

  loading.value = true
  try {
    const { data } = await createDiary({
      emotionType: diary.emotionType,
      emotionLevel: diary.emotionLevel,
      triggerEvent: diary.triggerEvent.trim(),
      feeling: diary.feeling.trim(),
    })
    aiSuggestion.value = data.aiSuggestion || '你已经认真照看了自己的情绪，这本身就很珍贵。'
    showAiFeedback.value = true
  } catch (error) {
    // 错误消息由请求层统一处理
  } finally {
    loading.value = false
  }
}

const finishDiary = () => {
  showAiFeedback.value = false
  router.push('/emotion/list')
}
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  animation: fadeIn 0.6s ease-out;
}

.page-header {
  margin-bottom: 40px;
  padding: 20px 0;
}

.page-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 36px;
  color: #1A2623;
  margin-bottom: 12px;
}

.page-subtitle {
  color: #798C87;
  font-size: 16px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.03);
}

.section-block {
  margin-bottom: 48px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1A2623;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.step-num {
  font-family: 'Outfit';
  color: #2F4D44;
  font-size: 20px;
  font-weight: 700;
  margin-right: 12px;
  opacity: 0.6;
}

.emotion-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 20px;
}

.emotion-card {
  position: relative;
  background: #FFF;
  border: 1px solid rgba(0,0,0,0.04);
  border-radius: 16px;
  padding: 24px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
}

.emotion-card:hover,
.emotion-card.is-active {
  transform: translateY(-4px);
  border-color: var(--border-color);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);
  background: var(--bg-color);
}

.emoji-wrapper {
  font-size: 40px;
  margin-bottom: 12px;
}

.emotion-label {
  font-size: 14px;
  color: #5B6B67;
  font-weight: 500;
}

.premium-textarea :deep(.el-textarea__inner),
.premium-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.75);
  box-shadow: none !important;
  border-radius: 16px;
}

.action-footer {
  display: flex;
  justify-content: flex-end;
}

.submit-btn,
.premium-button {
  background: #2F4D44;
  color: #FFF;
  border: none;
  border-radius: 18px;
  padding: 0 24px;
  min-height: 46px;
}

.ai-feedback-container {
  text-align: center;
  padding: 16px 8px;
}

.ai-icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.ai-title {
  font-family: 'Noto Serif SC', serif;
  color: #1A2623;
  font-size: 24px;
  margin-bottom: 16px;
}

.ai-content {
  color: #5B6B67;
  line-height: 1.8;
  text-align: left;
}

.dialog-actions {
  margin-top: 24px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
