<template>
  <div class="page-container glass-theme">
    <div class="page-header">
      <h1 class="page-title">匿名树洞</h1>
      <p class="page-subtitle">在这里卸下所有伪装，诉说那些无处安放的秘密</p>
    </div>

    <div class="treehole-layout">
      <div class="compose-section">
        <div class="compose-card glass-surface">
          <h3 class="compose-title">你想对树洞说些什么？</h3>
          <el-input
            v-model="newPost"
            type="textarea"
            :rows="5"
            placeholder="此时此刻，你的心声..."
            class="premium-textarea"
            maxlength="500"
            show-word-limit
          />
          <div class="compose-actions">
            <span class="safe-text"><el-icon><Lock /></el-icon> 完全匿名且安全</span>
            <el-button class="premium-button" :loading="publishing" @click="publishPost">
              投入树洞
            </el-button>
          </div>
        </div>
      </div>

      <div class="feed-section">
        <div class="feed-header flex-between">
          <h3 class="feed-title">树洞里的回声</h3>
          <el-radio-group v-model="feedSort" class="premium-radio-group" size="small" @change="handleFilterChange">
            <el-radio-button value="latest">最新</el-radio-button>
            <el-radio-button value="hot">共鸣最多</el-radio-button>
          </el-radio-group>
        </div>

        <div class="feed-toolbar glass-surface">
          <el-input
            v-model="keyword"
            clearable
            placeholder="搜索树洞内容或匿名昵称"
            @keyup.enter="handleFilterChange"
            @clear="handleFilterChange"
          />
          <el-button @click="handleFilterChange">搜索</el-button>
        </div>

        <el-skeleton :loading="loading" animated>
          <template #template>
            <div class="glass-surface mb-20" style="padding: 24px;" v-for="i in 3" :key="i">
              <el-skeleton-item variant="text" style="width: 100%; height: 60px;" />
            </div>
          </template>

          <template #default>
            <div class="post-list">
              <div class="post-card glass-surface" v-for="post in posts" :key="post.id">
                <div class="post-header">
                  <div class="author-info">
                    <span class="avatar-blob">{{ post.avatarIcon }}</span>
                    <span class="author-name">{{ post.anonymousName }}</span>
                  </div>
                  <span class="post-time">{{ post.createdAtText }}</span>
                </div>

                <p class="post-content" @click="$router.push(`/treehole/detail/${post.id}`)">{{ post.content }}</p>

                <div class="post-footer">
                  <button class="action-btn" :class="{ 'is-active': post.isLiked }" @click="toggleHug(post)">
                    <span class="hug-icon">🫂</span>
                    <span class="count">{{ post.likeCount }} 抱抱</span>
                  </button>
                  <button class="detail-btn" @click="$router.push(`/treehole/detail/${post.id}`)">
                    {{ post.commentCount }} 条评论
                  </button>
                </div>
              </div>

              <div v-if="!posts.length" class="glass-surface empty-state">
                树洞里还很安静，写下第一条心声吧。
              </div>
            </div>

            <app-pagination
              v-model:page-num="pageNum"
              v-model:page-size="pageSize"
              :total="total"
              @change="loadPosts"
            />
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Lock } from '@element-plus/icons-vue'
import { createPost, getPostList, togglePostLike } from '@/api/treehole'
import { formatRelativeTime, getInitial } from '@/utils/format'
import { showSuccess, showWarning } from '@/utils/feedback'
import AppPagination from '@/components/common/AppPagination.vue'

const newPost = ref('')
const publishing = ref(false)
const loading = ref(false)
const feedSort = ref('latest')
const posts = ref([])
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const normalizePost = (post) => ({
  ...post,
  createdAtText: formatRelativeTime(post.createdAt),
  avatarIcon: getInitial(post.anonymousName),
})

const loadPosts = async () => {
  loading.value = true
  try {
    const { data } = await getPostList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      sort: feedSort.value,
      keyword: keyword.value.trim() || undefined,
    })
    posts.value = (data.records || []).map(normalizePost)
    total.value = data.total || 0
  } catch (error) {
    posts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const publishPost = async () => {
  if (!newPost.value.trim()) {
    await showWarning('不要让风把空白的纸条吹走了哦')
    return
  }
  publishing.value = true
  try {
    await createPost({ content: newPost.value.trim() })
    newPost.value = ''
    pageNum.value = 1
    await showSuccess('你的心声已经安全落入树洞')
    loadPosts()
  } catch (error) {
    // 请求层统一提示
  } finally {
    publishing.value = false
  }
}

const toggleHug = async (post) => {
  try {
    const { data } = await togglePostLike(post.id)
    post.isLiked = data.isLiked
    post.likeCount = data.likeCount
  } catch (error) {
    // 请求层统一提示
  }
}

const handleFilterChange = () => {
  pageNum.value = 1
  loadPosts()
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
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

.treehole-layout {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.compose-section {
  flex: 4;
  position: sticky;
  top: 24px;
}

.feed-section {
  flex: 6;
}

.feed-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
}

.feed-toolbar :deep(.el-input) {
  flex: 1;
}

.glass-surface {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.03);
}

.compose-title {
  font-size: 18px;
  color: #1A2623;
  margin-bottom: 20px;
  font-weight: 600;
}

.premium-textarea :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.7);
  box-shadow: none !important;
  border: 1px solid rgba(0,0,0,0.06);
  border-radius: 16px;
  padding: 16px;
  color: #1A2623;
  resize: none;
}

.compose-actions,
.feed-header,
.post-footer,
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.compose-actions {
  margin-top: 24px;
}

.safe-text,
.post-time,
.detail-btn {
  color: #798C87;
  font-size: 13px;
}

.premium-button {
  background: #2F4D44;
  color: #FFF;
  border: none;
  border-radius: 16px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-blob {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(47, 77, 68, 0.12);
  color: #2F4D44;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.author-name {
  color: #1A2623;
}

.post-content {
  color: #2C3634;
  line-height: 1.9;
  cursor: pointer;
}

.post-footer {
  margin-top: 20px;
}

.action-btn,
.detail-btn {
  border: none;
  background: transparent;
  cursor: pointer;
}

.action-btn.is-active {
  color: #2F4D44;
}

.empty-state {
  text-align: center;
  color: #798C87;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
