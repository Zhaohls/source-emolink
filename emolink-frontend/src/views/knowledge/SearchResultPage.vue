<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">搜索结果</h1>
      <p class="page-subtitle">关键词：{{ keyword || '未填写' }}</p>
    </div>

    <div class="glass-surface search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="换个关键词试试"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
      />
    </div>

    <div class="results-list" v-loading="loading">
      <button
        v-for="article in articles"
        :key="article.id"
        type="button"
        class="result-item glass-surface"
        @click="$router.push(`/knowledge/detail/${article.id}`)"
      >
        <div class="result-main">
          <div class="result-title">{{ article.title }}</div>
          <div class="result-summary">{{ article.summary }}</div>
        </div>
        <div class="result-meta">{{ article.meta.label }} · {{ article.viewCount }} 阅读</div>
      </button>

      <div v-if="!loading && !articles.length" class="glass-surface empty-state">
        没有找到匹配内容。
      </div>
    </div>

    <app-pagination
      v-model:page-num="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      @change="loadArticles"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { searchArticleList } from '@/api/article'
import { getArticleCategoryMeta, getArticleSummary } from '@/utils/article'
import AppPagination from '@/components/common/AppPagination.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const articles = ref([])
const searchQuery = ref('')
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const keyword = computed(() => typeof route.query.keyword === 'string' ? route.query.keyword.trim() : '')

const loadArticles = async () => {
  if (!keyword.value) {
    articles.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const { data } = await searchArticleList({
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    })
    articles.value = (data.records || []).map((item) => ({
      ...item,
      meta: getArticleCategoryMeta(item.category),
      summary: getArticleSummary(item.content, 120),
    }))
    total.value = data.total || 0
  } catch (error) {
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  router.replace({
    path: '/knowledge/search',
    query: { keyword: searchQuery.value.trim() },
  })
}

watch(keyword, (value) => {
  searchQuery.value = value
  pageNum.value = 1
  loadArticles()
}, { immediate: true })

onMounted(() => {
  searchQuery.value = keyword.value
})
</script>

<style scoped>
.page-container {
  max-width: 980px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-family: 'Noto Serif SC', serif;
  color: #1A2623;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #798C87;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  padding: 20px;
}

.search-bar {
  margin-bottom: 24px;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  width: 100%;
  text-align: left;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  gap: 24px;
}

.result-title {
  color: #1A2623;
  font-size: 18px;
  margin-bottom: 10px;
}

.result-summary {
  color: #5B6B67;
  line-height: 1.8;
}

.result-meta {
  color: #798C87;
  white-space: nowrap;
}

.empty-state {
  text-align: center;
  color: #798C87;
}
</style>
