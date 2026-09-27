<template>
  <div class="page-container glass-theme">
    <div class="page-header">
      <h1 class="page-title">知识拾光</h1>
      <p class="page-subtitle">探索情绪奥秘，收获自我疗愈的力量</p>
    </div>

    <div class="search-section glass-surface filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索文章、专栏..."
        class="premium-search-input"
        clearable
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <div class="tags-wrapper">
        <span
          v-for="tag in ARTICLE_CATEGORY_OPTIONS"
          :key="tag.value || 'all'"
          class="premium-tag"
          :class="{ 'is-active': activeTag === tag.value }"
          @click="handleTagChange(tag.value)"
        >
          {{ tag.label }}
        </span>
      </div>
    </div>

    <el-skeleton :loading="loading" animated class="glass-surface">
      <template #template>
        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 24px; padding: 20px;">
          <div v-for="i in 4" :key="i" style="padding: 24px; border-radius: 24px; background: rgba(255,255,255,0.45);">
            <el-skeleton-item variant="text" style="width: 72px; height: 24px; margin-bottom: 16px;" />
            <el-skeleton-item variant="h3" style="width: 70%; height: 28px; margin-bottom: 12px;" />
            <el-skeleton-item variant="text" style="width: 100%; height: 18px; margin-bottom: 8px;" />
            <el-skeleton-item variant="text" style="width: 90%; height: 18px; margin-bottom: 8px;" />
            <el-skeleton-item variant="text" style="width: 60%; height: 18px; margin-top: 20px;" />
          </div>
        </div>
      </template>

      <template #default>
        <div v-if="articles.length" class="article-grid">
          <div
            v-for="article in articles"
            :key="article.id"
            class="article-card glass-surface"
          >
            <div class="article-content">
              <div class="article-meta">
                <span class="category-badge">{{ article.meta.label }}</span>
                <span class="read-time"><el-icon><View /></el-icon> {{ article.viewCount }} 次阅读</span>
              </div>
              <h3 class="article-title">{{ article.title }}</h3>
              <p class="article-summary">{{ article.summary }}</p>

              <div class="article-footer">
                <span class="author">{{ article.createdAtText }}</span>
                <el-button link class="read-more-btn" @click="$router.push(`/knowledge/detail/${article.id}`)">
                  阅读全文 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state glass-surface">
          暂时没有找到相关文章。
        </div>
      </template>
    </el-skeleton>

    <app-pagination
      v-model:page-num="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      @change="loadArticles"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Search, View, ArrowRight } from '@element-plus/icons-vue'
import { getArticleList } from '@/api/article'
import { ARTICLE_CATEGORY_OPTIONS, getArticleCategoryMeta, getArticleSummary } from '@/utils/article'
import { formatDate } from '@/utils/format'
import AppPagination from '@/components/common/AppPagination.vue'

const searchQuery = ref('')
const activeTag = ref('')
const loading = ref(false)
const articles = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(8)

const loadArticles = async () => {
  loading.value = true
  try {
    const { data } = await getArticleList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      category: activeTag.value || undefined,
      keyword: searchQuery.value.trim() || undefined,
    })

    articles.value = (data.records || []).map((item) => ({
      ...item,
      meta: getArticleCategoryMeta(item.category),
      summary: getArticleSummary(item.content),
      createdAtText: formatDate(item.createdAt),
    }))
    total.value = data.total || 0
  } catch (error) {
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleTagChange = (value) => {
  activeTag.value = value
  pageNum.value = 1
  loadArticles()
}

const handleSearch = () => {
  pageNum.value = 1
  loadArticles()
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
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

.search-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.filter-bar {
  padding: 24px;
}

.premium-search-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.7) !important;
  box-shadow: none !important;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16px;
  height: 48px;
  font-size: 16px;
  padding: 0 20px;
}

.tags-wrapper {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.premium-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  color: #5B6B67;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.8);
  cursor: pointer;
}

.premium-tag.is-active {
  background: #2F4D44;
  color: #FFF;
  border-color: #2F4D44;
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 32px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.03);
}

.article-card {
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0;
  transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.4s ease;
}

.article-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.06);
}

.article-content {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.article-meta,
.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.article-meta {
  margin-bottom: 16px;
  color: #798C87;
  font-size: 13px;
}

.category-badge {
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(47, 77, 68, 0.1);
  color: #2F4D44;
  font-weight: 600;
}

.article-title {
  color: #1A2623;
  margin-bottom: 12px;
  font-size: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  color: #5B6B67;
  line-height: 1.8;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.author {
  color: #A0B0AB;
  font-size: 13px;
}

.read-more-btn {
  color: #2F4D44;
}

.empty-state {
  padding: 48px;
  text-align: center;
  color: #798C87;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
