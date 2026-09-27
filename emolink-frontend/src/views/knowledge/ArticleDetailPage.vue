<template>
  <div class="page-container">
    <div class="header-actions">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
    </div>

    <div class="glass-surface article-detail" v-loading="loading" v-if="article">
      <div class="article-header">
        <div>
          <span class="category-badge">{{ meta.label }}</span>
          <h1 class="article-title">{{ article.title }}</h1>
          <p class="meta-line">{{ article.createdAtText }} · {{ article.viewCount }} 次阅读</p>
        </div>
        <el-button :type="article.isFavorited ? 'primary' : 'default'" @click="handleFavorite">
          {{ article.isFavorited ? '已收藏' : '收藏文章' }}
        </el-button>
      </div>

      <div class="article-body markdown-body" v-html="articleHtml"></div>
    </div>

    <div v-else-if="!loading" class="glass-surface empty-state">
      文章不存在或已下线。
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getArticleDetail, toggleArticleFavorite } from '@/api/article'
import { getArticleCategoryMeta } from '@/utils/article'
import { formatDateTime } from '@/utils/format'

const route = useRoute()
const loading = ref(false)
const article = ref(null)

const meta = computed(() => getArticleCategoryMeta(article.value?.category))
const articleHtml = computed(() => DOMPurify.sanitize(marked.parse(article.value?.content || '')))

const loadArticle = async () => {
  loading.value = true
  try {
    const { data } = await getArticleDetail(route.params.id)
    article.value = {
      ...data,
      createdAtText: formatDateTime(data.createdAt),
    }
  } catch (error) {
    article.value = null
  } finally {
    loading.value = false
  }
}

const handleFavorite = async () => {
  if (!article.value) return
  try {
    const { data } = await toggleArticleFavorite(article.value.id)
    article.value.isFavorited = data.isFavorited
  } catch (error) {
    // 请求层统一提示
  }
}

onMounted(() => {
  loadArticle()
})
</script>

<style scoped>
.page-container {
  max-width: 980px;
  margin: 0 auto;
}

.header-actions {
  margin-bottom: 16px;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.66);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  padding: 32px;
}

.article-header {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-start;
  margin-bottom: 28px;
}

.category-badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(47, 77, 68, 0.1);
  color: #2F4D44;
  font-weight: 600;
  margin-bottom: 16px;
}

.article-title {
  font-family: 'Noto Serif SC', serif;
  color: #1A2623;
  margin: 0 0 12px;
}

.meta-line {
  color: #798C87;
}

.article-body {
  color: #2C3634;
  line-height: 1.9;
}

.article-body :deep(h1),
.article-body :deep(h2),
.article-body :deep(h3) {
  color: #1A2623;
  margin-top: 28px;
}

.article-body :deep(p),
.article-body :deep(li),
.article-body :deep(blockquote) {
  line-height: 1.9;
}

.empty-state {
  text-align: center;
  color: #798C87;
}
</style>
