<template>
  <div class="page-container glass-theme">
    <div class="page-header flex-between">
      <div>
        <h1 class="page-title">情绪日报</h1>
        <p class="page-subtitle">深入探寻你今天情绪的潮汐起伏</p>
      </div>
      <el-date-picker
        v-model="queryDate"
        type="date"
        placeholder="选择日期"
        class="premium-datepicker"
        format="YYYY年MM月DD日"
        :clearable="false"
        value-format="YYYY-MM-DD"
        @change="loadReport"
      />
    </div>

    <el-skeleton :loading="loading" animated class="glass-surface">
      <template #template>
        <div style="padding: 20px;">
          <el-skeleton-item variant="h3" style="width: 30%; margin-bottom: 20px;" />
          <el-skeleton-item variant="image" style="width: 100%; height: 300px;" />
        </div>
      </template>

      <template #default>
        <div class="report-content" v-if="hasReportData">
          <div class="summary-section glass-surface mb-24">
            <div class="summary-content">
              <div class="icon-blob">✨</div>
              <div class="summary-text">
                <h3>心之密语</h3>
                <p>{{ summaryText }}</p>
              </div>
            </div>
          </div>

          <div class="chart-section glass-surface">
            <h3 class="chart-title">今日情绪强度波动</h3>
            <div class="chart-container" ref="chartRef"></div>
          </div>
        </div>

        <div v-else class="empty-state glass-surface">
          <div class="empty-emoji">🍃</div>
          <p>这一天还没有留下情绪印记呢</p>
          <el-button plain class="premium-button-outline" @click="$router.push('/emotion/create')">
            去记录今天的第一笔
          </el-button>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getDailyReport } from '@/api/emotion'
import { EMOTION_MAP } from '@/utils/emotion'
import { formatDateValue } from '@/utils/format'

const queryDate = ref(formatDateValue(new Date()))
const loading = ref(false)
const reportData = ref(null)
const chartRef = ref(null)
let chartInstance = null

const hasReportData = computed(() => (reportData.value?.totalCount || 0) > 0)

const summaryText = computed(() => {
  const distribution = reportData.value?.emotionDistribution || []
  const top = distribution[0]
  if (!reportData.value?.totalCount) {
    return ''
  }
  if (!top) {
    return `这一天共记录了 ${reportData.value.totalCount} 次情绪变化。`
  }
  const emotion = EMOTION_MAP[top.emotionType]?.label || top.emotionType
  return `这一天共记录了 ${reportData.value.totalCount} 次情绪变化，其中「${emotion}」最常出现，占比 ${top.percentage}% 。从波动里看见自己，本身就是一种修复。`
})

const initChart = () => {
  if (!chartRef.value || !hasReportData.value) return
  if (chartInstance) chartInstance.dispose()

  const data = reportData.value.emotionTimeline || []
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: 'transparent',
      textStyle: { color: '#1A2623' },
      formatter: (params) => {
        const item = data[params[0].dataIndex]
        const emotion = EMOTION_MAP[item.emotionType]
        return `
          <div style="font-weight:600;margin-bottom:4px;">${item.time}</div>
          <div>情绪: ${emotion?.emoji || '🍃'} ${emotion?.label || item.emotionType}</div>
          <div>强度: ${item.emotionLevel} / 5</div>
        `
      },
    },
    grid: {
      top: '15%',
      left: '3%',
      right: '4%',
      bottom: '5%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map((item) => item.time),
      axisLine: { lineStyle: { color: '#E2E8E6' } },
      axisLabel: { color: '#798C87' },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 5,
      splitLine: { lineStyle: { color: 'rgba(0,0,0,0.03)' } },
      axisLabel: { color: '#798C87' },
    },
    series: [
      {
        name: '情绪强度',
        type: 'line',
        smooth: true,
        data: data.map((item) => item.emotionLevel),
        lineStyle: {
          color: '#2F4D44',
          width: 3,
        },
        itemStyle: {
          color: (params) => EMOTION_MAP[data[params.dataIndex]?.emotionType]?.color || '#2F4D44',
          borderWidth: 2,
          borderColor: '#FFF',
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(47, 77, 68, 0.2)' },
            { offset: 1, color: 'rgba(47, 77, 68, 0)' },
          ]),
        },
      },
    ],
  })
}

const loadReport = async () => {
  loading.value = true
  let shouldInitChart = false
  try {
    const { data } = await getDailyReport({ date: queryDate.value })
    reportData.value = data
    shouldInitChart = (data.totalCount || 0) > 0
  } catch (error) {
    reportData.value = null
    chartInstance?.dispose()
    chartInstance = null
  } finally {
    loading.value = false
  }

  await nextTick()
  if (shouldInitChart) {
    initChart()
  }
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  loadReport()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.page-container {
  max-width: 1000px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header {
  margin-bottom: 32px;
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

.mb-24 {
  margin-bottom: 24px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.03);
}

.premium-datepicker :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.7) !important;
  box-shadow: none !important;
  border: 1px solid rgba(0,0,0,0.05);
  border-radius: 12px;
  height: 40px;
}

.summary-content {
  display: flex;
  gap: 20px;
}

.icon-blob {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: rgba(232, 211, 195, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.summary-text h3,
.chart-title {
  color: #1A2623;
  margin-bottom: 12px;
}

.summary-text p {
  color: #5B6B67;
  line-height: 1.8;
}

.chart-container {
  width: 100%;
  height: 340px;
}

.empty-state {
  text-align: center;
  color: #798C87;
}

.empty-emoji {
  font-size: 56px;
  margin-bottom: 16px;
}

.premium-button-outline {
  margin-top: 20px;
  border-color: #2F4D44;
  color: #2F4D44;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
