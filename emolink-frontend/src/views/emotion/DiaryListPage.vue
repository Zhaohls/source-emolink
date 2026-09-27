<template>
  <div class="page-container glass-theme">
    <div class="page-header flex-between">
      <div>
        <h1 class="page-title">情绪足迹</h1>
        <p class="page-subtitle">这些都是你一路走来的心路历程</p>
      </div>
      <el-button type="primary" class="premium-button" @click="$router.push('/emotion/create')">
        <el-icon class="mr-2"><Plus /></el-icon> 记录当下
      </el-button>
    </div>

    <div class="filter-bar glass-surface-light">
      <el-input
        v-model="filters.keyword"
        clearable
        placeholder="搜索触发事件、感受或建议"
        class="premium-search"
        @keyup.enter="handleFilter"
        @clear="handleFilter"
      />

      <el-select
        v-model="filters.emotionType"
        placeholder="全部情绪"
        clearable
        class="premium-select"
        @change="handleFilter"
      >
        <el-option
          v-for="(info, key) in EMOTION_MAP"
          :key="key"
          :label="`${info.emoji} ${info.label}`"
          :value="key"
        />
      </el-select>

      <el-date-picker
        v-model="filters.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY年MM月DD日"
        value-format="YYYY-MM-DD"
        class="premium-datepicker"
        @change="handleFilter"
      />
    </div>

    <div class="diary-list" v-loading="loading">
      <div v-if="diaryList.length === 0 && !loading" class="empty-state">
        <div class="empty-emoji">🍃</div>
        <p class="empty-text">还没有留下情绪印记</p>
        <el-button plain class="premium-button-outline" @click="$router.push('/emotion/create')">
          开启第一次记录
        </el-button>
      </div>

      <div class="diary-grid" v-else>
        <div
          v-for="item in diaryList"
          :key="item.id"
          class="diary-card glass-surface"
          @click="$router.push(`/emotion/detail/${item.id}`)"
        >
          <div class="card-header flex-between">
            <div class="emotion-badge" :style="{ background: emotionMeta(item.emotionType).bgColor, color: '#1A2623' }">
              <span class="mr-1">{{ emotionMeta(item.emotionType).emoji }}</span>
              <span>{{ emotionMeta(item.emotionType).label }}</span>
            </div>
            <span class="time-stamp">{{ item.createdAtText }}</span>
          </div>

          <div class="card-body">
            <h4 v-if="item.triggerEvent" class="trigger-text">关于：{{ item.triggerEvent }}</h4>
            <p class="feeling-text">{{ item.feeling }}</p>
          </div>

          <div class="card-footer">
            <span class="level-indicator">
              强度: <span class="dots"><i v-for="i in 5" :key="i" class="dot" :class="{ active: i <= item.emotionLevel }"></i></span>
            </span>
          </div>
        </div>
      </div>

      <app-pagination
        v-model:page-num="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { getDiaryList } from '@/api/emotion'
import { EMOTION_MAP } from '@/utils/emotion'
import { formatDateTime } from '@/utils/format'
import AppPagination from '@/components/common/AppPagination.vue'

const route = useRoute()
const loading = ref(false)
const diaryList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)

const filters = reactive({
  keyword: '',
  emotionType: '',
  dateRange: null,
})

const emotionMeta = (type) => EMOTION_MAP[type] || {
  label: type || '未知',
  emoji: '🍃',
  bgColor: 'rgba(160, 176, 171, 0.16)',
}

const syncRouteDate = () => {
  const date = typeof route.query.date === 'string' ? route.query.date : ''
  filters.dateRange = date ? [date, date] : null
}

const loadData = async () => {
  loading.value = true
  try {
    const [startDate, endDate] = filters.dateRange || []
    const { data } = await getDiaryList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: filters.keyword.trim() || undefined,
      emotionType: filters.emotionType || undefined,
      startDate,
      endDate,
    })

    diaryList.value = (data.records || []).map((item) => ({
      ...item,
      createdAtText: formatDateTime(item.createdAt),
    }))
    total.value = data.total || 0
  } catch (error) {
    diaryList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pageNum.value = 1
  loadData()
}

watch(() => route.query.date, () => {
  syncRouteDate()
  pageNum.value = 1
  loadData()
})

onMounted(() => {
  syncRouteDate()
  loadData()
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

.premium-button {
  background-color: #2F4D44;
  border: none;
  border-radius: 20px;
  padding: 0 24px;
  height: 40px;
  box-shadow: 0 8px 16px rgba(47, 77, 68, 0.15);
  transition: all 0.3s ease;
}

.premium-button:hover {
  background-color: #1A2623;
  transform: translateY(-1px);
  box-shadow: 0 12px 20px rgba(26, 38, 35, 0.2);
}

.premium-button-outline {
  border-color: #2F4D44;
  color: #2F4D44;
  border-radius: 20px;
  background: transparent;
}

.premium-button-outline:hover {
  background: #2F4D44;
  color: #FFF;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.6);
}

.premium-select :deep(.el-input__wrapper),
.premium-datepicker,
.premium-search :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.7) !important;
  box-shadow: none !important;
  border: 1px solid rgba(0,0,0,0.05);
  border-radius: 8px;
}

.premium-search {
  flex: 1;
  min-width: 180px;
}

.premium-select {
  width: 140px;
  flex-shrink: 0;
}

.premium-datepicker {
  width: 280px;
  flex-shrink: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #798C87;
}

.empty-emoji {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.8;
}

.empty-text {
  font-size: 16px;
  margin-bottom: 24px;
}

.diary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.02);
}

.diary-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  display: flex;
  flex-direction: column;
}

.diary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.06);
}

.emotion-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
}

.time-stamp {
  color: #A0B0AB;
  font-size: 12px;
}

.card-body {
  margin: 20px 0;
}

.trigger-text {
  color: #2F4D44;
  font-size: 14px;
  margin-bottom: 12px;
}

.feeling-text {
  color: #1A2623;
  line-height: 1.8;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.level-indicator {
  color: #798C87;
  font-size: 13px;
}

.dots {
  display: inline-flex;
  gap: 6px;
  margin-left: 4px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(0,0,0,0.08);
}

.dot.active {
  background: #2F4D44;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
