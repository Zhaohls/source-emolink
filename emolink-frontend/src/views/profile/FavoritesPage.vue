<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
      <p class="page-subtitle">把那些值得反复阅读的内容留在身边。</p>
    </div>

    <div class="glass-surface search-bar">
      <el-input
        v-model="keyword"
        clearable
        placeholder="搜索收藏文章标题或内容"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
    </div>

    <div class="card-list" v-loading="loading">
      <button
        v-for="article in articles"
        :key="article.id"
        type="button"
        class="card glass-surface"
        @click="$router.push(`/knowledge/detail/${article.id}`)"
      >
        <div class="card-title">{{ article.title }}</div>
        <div class="card-summary">{{ article.summary }}</div>
        <div class="card-meta">{{ article.meta.label }} · {{ article.viewCount }} 次阅读</div>
      </button>

      <div v-if="!loading && !articles.length" class="glass-surface empty-state">
        你还没有收藏任何文章。
      </div>
    </div>

    <app-pagination
      v-model:page-num="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      @change="loadFavorites"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getFavoriteArticleList } from '@/api/article'
import { getArticleCategoryMeta, getArticleSummary } from '@/utils/article'
import AppPagination from '@/components/common/AppPagination.vue'

const loading = ref(false)
const articles = ref([])
const keyword = ref('')
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const loadFavorites = async () => {
  loading.value = true
  try {
    const { data } = await getFavoriteArticleList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value.trim() || undefined,
    })
    articles.value = (data.records || []).map((item) => ({
      ...item,
      meta: getArticleCategoryMeta(item.category),
      summary: getArticleSummary(item.content, 110),
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
  pageNum.value = 1
  loadFavorites()
}

onMounted(() => {
  loadFavorites()
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

.search-bar {
  margin-bottom: 16px;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  padding: 20px;
}

.card {
  width: 100%;
  text-align: left;
  cursor: pointer;
}

.card-title {
  color: #1A2623;
  font-size: 18px;
  margin-bottom: 12px;
}

.card-summary {
  color: #5B6B67;
  line-height: 1.8;
  margin-bottom: 12px;
}

.card-meta,
.empty-state {
  color: #798C87;
}

.empty-state {
  text-align: center;
}
</style>
