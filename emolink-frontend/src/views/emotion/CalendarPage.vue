<template>
  <div class="page-container glass-theme">
    <div class="page-header">
      <h1 class="page-title">情绪图鉴</h1>
      <p class="page-subtitle">时间的画布上，印着你不同色彩的印记</p>
    </div>

    <div class="calendar-wrapper glass-surface" v-loading="loading">
      <el-calendar v-model="currentDate" class="premium-calendar">
        <template #header>
          <div class="calendar-header">
            <h2 class="calendar-title">{{ formatMonthLabel(currentDate) }}</h2>
            <div class="calendar-actions">
              <el-button @click="changeMonth(-1)">上个月</el-button>
              <el-button @click="goToday">今天</el-button>
              <el-button @click="changeMonth(1)">下个月</el-button>
            </div>
          </div>
        </template>

        <template #date-cell="{ data }">
          <div class="custom-date-cell" @click="handleDateClick(data)">
            <span class="date-text" :class="{ 'is-today': data.isToday }">
              {{ Number(data.day.split('-')[2]) }}
            </span>

            <div class="emotion-mark" v-if="getEmotionForDate(data.day)">
              <div
                class="emoji-blob"
                :style="{ background: getEmotionForDate(data.day).bgColor }"
                :title="getEmotionForDate(data.day).label"
              >
                {{ getEmotionForDate(data.day).emoji }}
              </div>
            </div>
          </div>
        </template>
      </el-calendar>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCalendarData } from '@/api/emotion'
import { EMOTION_MAP } from '@/utils/emotion'
import { formatMonthLabel } from '@/utils/format'

const router = useRouter()
const currentDate = ref(new Date())
const loading = ref(false)
const calendarData = ref({})

const yearMonth = computed(() => ({
  year: currentDate.value.getFullYear(),
  month: currentDate.value.getMonth() + 1,
}))

const loadCalendar = async () => {
  loading.value = true
  try {
    const { data } = await getCalendarData(yearMonth.value)
    calendarData.value = (data || []).reduce((acc, item) => {
      acc[item.date] = item.dominantEmotion
      return acc
    }, {})
  } catch (error) {
    calendarData.value = {}
  } finally {
    loading.value = false
  }
}

const getEmotionForDate = (dateStr) => {
  const type = calendarData.value[dateStr]
  if (type && EMOTION_MAP[type]) {
    return EMOTION_MAP[type]
  }
  return null
}

const handleDateClick = (data) => {
  if (calendarData.value[data.day]) {
    router.push(`/emotion/list?date=${data.day}`)
  }
}

const changeMonth = (offset) => {
  currentDate.value = new Date(
    currentDate.value.getFullYear(),
    currentDate.value.getMonth() + offset,
    1
  )
}

const goToday = () => {
  currentDate.value = new Date()
}

watch(yearMonth, () => {
  loadCalendar()
}, { immediate: true })
</script>

<style scoped>
.page-container {
  max-width: 1000px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
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

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  width: 100%;
}

.calendar-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  color: #1A2623;
  margin: 0;
}

.calendar-actions {
  display: flex;
  gap: 8px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 32px;
  padding: 32px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.03);
  overflow: hidden;
}

.premium-calendar {
  background: transparent;
}

.premium-calendar :deep(.el-calendar__header) {
  border-bottom: none;
  padding: 0 0 24px 0;
}

.premium-calendar :deep(.el-calendar__title) {
  font-family: 'Outfit';
  font-size: 20px;
  font-weight: 600;
  color: #1A2623;
}

.premium-calendar :deep(.el-button-group > .el-button) {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(0, 0, 0, 0.05);
  color: #5B6B67;
}

.premium-calendar :deep(.el-calendar-table th) {
  color: #A0B0AB;
  font-weight: 500;
}

.premium-calendar :deep(.el-calendar-table td) {
  border: none;
  background: transparent;
}

.premium-calendar :deep(.el-calendar-day) {
  height: 100px;
  padding: 8px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.3);
  margin: 4px;
}

.custom-date-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.date-text {
  font-family: 'Outfit';
  font-weight: 500;
  color: #798C87;
  font-size: 14px;
}

.date-text.is-today {
  color: #2F4D44;
  font-weight: 700;
}

.emotion-mark {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji-blob {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
