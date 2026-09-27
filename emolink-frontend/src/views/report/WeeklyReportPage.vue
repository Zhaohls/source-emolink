<template>
  <div class="page-container glass-theme">
    <div class="page-header flex-between">
      <div>
        <h1 class="page-title">周度疗愈报告</h1>
        <p class="page-subtitle">过去七天，看见你每一份微小的改变</p>
      </div>
      <el-date-picker
        v-model="queryWeek"
        type="week"
        format="YYYY 第 ww 周"
        value-format="YYYY-MM-DD"
        placeholder="选择周"
        class="premium-datepicker"
        :clearable="false"
        @change="loadReport"
      />
    </div>

    <el-skeleton :loading="loading" animated class="glass-surface">
      <template #template>
        <div style="padding: 20px;">
          <el-skeleton-item variant="h3" style="width: 20%; margin-bottom: 20px;" />
          <el-skeleton-item variant="text" style="width: 100%; height: 150px; margin-bottom: 30px;" />
          <div style="display: flex; gap: 20px;">
            <el-skeleton-item variant="image" style="width: 60%; height: 350px;" />
            <el-skeleton-item variant="image" style="width: 40%; height: 350px;" />
          </div>
        </div>
      </template>

      <template #default>
        <div class="report-content" v-if="hasReportData">
          <div class="insight-section glass-surface mb-24">
            <div class="insight-content">
              <span class="insight-quote">“</span>
              <p class="insight-text">{{ reportData.aiAnalysis }}</p>
            </div>
          </div>

          <div class="charts-grid">
            <div class="chart-card glass-surface">
              <h3 class="chart-title">本周情绪图谱</h3>
              <div class="chart-container" ref="pieChartRef"></div>
            </div>

            <div class="chart-card glass-surface">
              <h3 class="chart-title">七日情绪波动</h3>
              <div class="chart-container" ref="barChartRef"></div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state glass-surface">
          <div class="empty-emoji">🕊️</div>
          <p>这一周，好像风平浪静，没有留下太多痕迹呢</p>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import { getWeeklyReport } from '@/api/emotion'
import { EMOTION_MAP } from '@/utils/emotion'
import { getWeekStart } from '@/utils/format'

const queryWeek = ref(getWeekStart().format('YYYY-MM-DD'))
const loading = ref(false)
const reportData = ref(null)

const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChartInstance = null
let barChartInstance = null

const hasReportData = computed(() => (reportData.value?.totalCount || 0) > 0)

const initPieChart = () => {
  if (!pieChartRef.value || !hasReportData.value) return
  pieChartInstance?.dispose()
  pieChartInstance = echarts.init(pieChartRef.value)

  const data = (reportData.value.emotionDistribution || []).map((item) => ({
    name: EMOTION_MAP[item.emotionType]?.label || item.emotionType,
    value: item.count,
    itemStyle: { color: EMOTION_MAP[item.emotionType]?.color || '#2F4D44' },
  }))

  pieChartInstance.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      orient: 'horizontal',
      bottom: '5%',
      textStyle: { color: '#798C87' },
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: { show: false },
        data,
      },
    ],
  })
}

const initBarChart = () => {
  if (!barChartRef.value || !hasReportData.value) return
  barChartInstance?.dispose()
  barChartInstance = echarts.init(barChartRef.value)

  const trends = reportData.value.dailyTrend || []
  barChartInstance.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: {
      top: '10%',
      left: '3%',
      right: '4%',
      bottom: '5%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: trends.map((item) => dayjs(item.date).format('dd')),
      axisLabel: { color: '#798C87' },
      axisLine: { lineStyle: { color: '#E2E8E6' } },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 5,
      splitLine: { lineStyle: { color: 'rgba(0,0,0,0.03)' } },
    },
    series: [
      {
        name: '平均强度',
        type: 'bar',
        data: trends.map((item) => ({
          value: item.avgLevel,
          itemStyle: {
            color: EMOTION_MAP[item.dominantEmotion]?.color || '#2F4D44',
            borderRadius: [8, 8, 0, 0],
          },
        })),
        barWidth: '40%',
      },
    ],
  })
}

const loadReport = async () => {
  loading.value = true
  let shouldInitCharts = false
  try {
    const { data } = await getWeeklyReport({ startDate: queryWeek.value })
    reportData.value = data
    shouldInitCharts = (data.totalCount || 0) > 0
  } catch (error) {
    reportData.value = null
    pieChartInstance?.dispose()
    barChartInstance?.dispose()
    pieChartInstance = null
    barChartInstance = null
  } finally {
    loading.value = false
  }

  await nextTick()
  if (shouldInitCharts) {
    initPieChart()
    initBarChart()
  }
}

const handleResize = () => {
  pieChartInstance?.resize()
  barChartInstance?.resize()
}

onMounted(() => {
  loadReport()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChartInstance?.dispose()
  barChartInstance?.dispose()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
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

.mb-24 {
  margin-bottom: 24px;
}

.insight-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.insight-quote {
  font-size: 54px;
  line-height: 1;
  color: rgba(47, 77, 68, 0.18);
}

.insight-text {
  color: #5B6B67;
  line-height: 1.9;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.chart-title {
  color: #1A2623;
  margin-bottom: 16px;
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

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 960px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
