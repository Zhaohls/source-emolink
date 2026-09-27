<template>
  <div class="admin-page">
    <h1 class="page-title">数据概览</h1>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" v-for="card in statCards" :key="card.label" :style="{ background: card.bg }">
        <div class="stat-icon">{{ card.icon }}</div>
        <div class="stat-body">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
        <div class="stat-badge" v-if="card.badge">{{ card.badge }}</div>
      </div>
    </div>

    <!-- 图表行 -->
    <div class="chart-row">
      <div class="chart-card">
        <h3 class="card-title">近7天新增用户趋势</h3>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
      <div class="chart-card">
        <h3 class="card-title">全站情绪分布</h3>
        <div ref="emotionChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 待处理事项 -->
    <div class="pending-section">
      <h3 class="card-title">⏳ 待处理事项</h3>
      <div class="pending-list">
        <div class="pending-item" v-for="item in pendingItems" :key="item.label">
          <div class="pending-info">
            <span class="pending-dot" :style="{ background: item.color }"></span>
            <span>{{ item.label }}</span>
          </div>
          <el-button size="small" type="primary" plain @click="$router.push(item.route)">
            去处理 ({{ item.count }})
          </el-button>
        </div>
        <div class="pending-empty" v-if="pendingItems.every(i => i.count === 0)">
          🎉 太棒了，没有待处理的事项！
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getAdminDashboard } from '@/api/admin'
import { EMOTION_MAP } from '@/utils/emotion'

const trendChartRef = ref(null)
const emotionChartRef = ref(null)
const dashboardData = ref({})

const statCards = computed(() => {
  const d = dashboardData.value
  return [
    { icon: '👥', label: '总用户数', value: d.totalUsers || 0, badge: d.todayNewUsers ? `今日+${d.todayNewUsers}` : null, bg: 'linear-gradient(135deg, #1A2623 0%, #2F4D44 100%)' },
    { icon: '📝', label: '日记总数', value: d.totalDiaries || 0, badge: d.todayDiaries ? `今日+${d.todayDiaries}` : null, bg: 'linear-gradient(135deg, #2F4D44 0%, #4A7A6C 100%)' },
    { icon: '📚', label: '文章总数', value: d.totalArticles || 0, badge: null, bg: 'linear-gradient(135deg, #3D6B5E 0%, #5E9B8A 100%)' },
    { icon: '💬', label: '树洞帖子', value: d.totalPosts || 0, badge: d.pendingPosts ? `${d.pendingPosts}条待审` : null, bg: 'linear-gradient(135deg, #4A7A6C 0%, #6BAF9E 100%)' },
  ]
})

const pendingItems = computed(() => {
  const d = dashboardData.value
  return [
    { label: '待审核帖子', count: d.pendingPosts || 0, color: '#E6A23C', route: '/admin/posts' },
    { label: '待审核评论', count: d.pendingComments || 0, color: '#F56C6C', route: '/admin/comments' },
  ]
})

const initCharts = () => {
  const d = dashboardData.value

  // 用户趋势折线图
  if (trendChartRef.value && d.recentUserTrend?.length) {
    const chart = echarts.init(trendChartRef.value)
    chart.setOption({
      grid: { top: 20, right: 20, bottom: 30, left: 45 },
      xAxis: { type: 'category', data: d.recentUserTrend.map(i => i.date.slice(5)), axisLine: { lineStyle: { color: '#DDD' } }, axisLabel: { color: '#888' } },
      yAxis: { type: 'value', minInterval: 1, axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F0F0' } }, axisLabel: { color: '#888' } },
      series: [{ data: d.recentUserTrend.map(i => i.count), type: 'line', smooth: true, lineStyle: { color: '#2F4D44', width: 3 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(47,77,68,0.3)' }, { offset: 1, color: 'rgba(47,77,68,0.02)' }]) }, itemStyle: { color: '#2F4D44' } }],
      tooltip: { trigger: 'axis' },
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 情绪饼图
  if (emotionChartRef.value && d.emotionDistribution?.length) {
    const chart = echarts.init(emotionChartRef.value)
    const colorMap = { happy: '#6BCB77', calm: '#5B9BD5', anxious: '#F0A500', sad: '#7B8EC8', angry: '#E8726D', fearful: '#A97CBF' }
    chart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, textStyle: { color: '#666' } },
      series: [{
        type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
        label: { show: false },
        data: d.emotionDistribution.map(i => ({
          name: EMOTION_MAP[i.emotionType]?.label || i.emotionType,
          value: i.count,
          itemStyle: { color: colorMap[i.emotionType] || '#CCC' }
        }))
      }]
    })
    window.addEventListener('resize', () => chart.resize())
  }
}

onMounted(async () => {
  try {
    const { data } = await getAdminDashboard()
    dashboardData.value = data || {}
  } catch (e) {
    // Mock 数据用于展示
    dashboardData.value = {
      totalUsers: 156, todayNewUsers: 5, totalDiaries: 1280, todayDiaries: 23,
      totalArticles: 25, totalPosts: 320, pendingPosts: 3, totalComments: 890, pendingComments: 2,
      recentUserTrend: [
        { date: '2026-04-09', count: 3 }, { date: '2026-04-10', count: 5 }, { date: '2026-04-11', count: 2 },
        { date: '2026-04-12', count: 8 }, { date: '2026-04-13', count: 4 }, { date: '2026-04-14', count: 6 }, { date: '2026-04-15', count: 5 }
      ],
      emotionDistribution: [
        { emotionType: 'happy', count: 380 }, { emotionType: 'anxious', count: 290 },
        { emotionType: 'calm', count: 250 }, { emotionType: 'sad', count: 180 },
        { emotionType: 'angry', count: 100 }, { emotionType: 'fearful', count: 80 }
      ]
    }
  }
  await nextTick()
  initCharts()
})
</script>

<style scoped>
.admin-page { width: 100%; }

.page-title {
  font-size: 26px; font-weight: 700; color: #1A2623; margin-bottom: 28px;
}

/* 统计卡片 */
.stat-cards {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 28px;
}

.stat-card {
  border-radius: 16px; padding: 24px; color: #FFF; display: flex; align-items: center; gap: 16px; position: relative; overflow: hidden;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1); transition: transform 0.3s ease;
}
.stat-card:hover { transform: translateY(-4px); }

.stat-icon { font-size: 36px; }
.stat-value { font-size: 32px; font-weight: 700; font-family: 'Outfit'; line-height: 1; }
.stat-label { font-size: 13px; opacity: 0.8; margin-top: 4px; }

.stat-badge {
  position: absolute; top: 12px; right: 12px;
  background: rgba(255,255,255,0.2); border-radius: 20px; padding: 4px 12px;
  font-size: 12px; font-weight: 600; backdrop-filter: blur(6px);
}

/* 图表行 */
.chart-row { display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 20px; margin-bottom: 28px; }
.chart-card {
  background: #FFF; border-radius: 16px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.card-title { font-size: 16px; font-weight: 600; color: #1A2623; margin-bottom: 16px; }
.chart-container { height: 280px; }

/* 待处理 */
.pending-section {
  background: #FFF; border-radius: 16px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.pending-list { display: flex; flex-direction: column; gap: 12px; margin-top: 16px; }
.pending-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px; background: #FFF9EC; border-radius: 12px; border: 1px solid #FAECD8;
}
.pending-info { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #333; }
.pending-dot { width: 8px; height: 8px; border-radius: 50%; }
.pending-empty { text-align: center; padding: 24px; color: #999; font-size: 14px; }
</style>
