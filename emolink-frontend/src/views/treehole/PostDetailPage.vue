<template>
  <div class="page-container">
    <div class="header-actions">
      <el-button text @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
    </div>

    <div class="glass-surface" v-loading="loading" v-if="post">
      <div class="post-header">
        <div>
          <div class="author-line">{{ post.anonymousName }}</div>
          <div class="time-line">{{ post.createdAtText }}</div>
        </div>
        <el-button @click="handleLike">{{ post.isLiked ? '已抱抱' : '送个抱抱' }} · {{ post.likeCount }}</el-button>
      </div>

      <p class="post-content">{{ post.content }}</p>

      <div class="comment-form">
        <el-input
          v-model="commentText"
          type="textarea"
          :rows="4"
          maxlength="300"
          show-word-limit
          placeholder="留下你的回应"
        />
        <div class="comment-actions">
          <el-button type="primary" :loading="commenting" @click="handleComment">发表评论</el-button>
        </div>
      </div>

      <div class="comment-list">
        <div v-for="comment in post.comments || []" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span>{{ comment.anonymousName }}</span>
            <span>{{ formatRelativeTime(comment.createdAt) }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="glass-surface empty-state">
      帖子不存在或已被隐藏。
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { createComment, getPostDetail, togglePostLike } from '@/api/treehole'
import { formatRelativeTime } from '@/utils/format'
import { showInfo, showSuccess, showWarning } from '@/utils/feedback'

const route = useRoute()
const loading = ref(false)
const commenting = ref(false)
const commentText = ref('')
const post = ref(null)

const loadPost = async () => {
  loading.value = true
  try {
    const { data } = await getPostDetail(route.params.id)
    post.value = {
      ...data,
      createdAtText: formatRelativeTime(data.createdAt),
    }
  } catch (error) {
    post.value = null
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!post.value) return
  try {
    const { data } = await togglePostLike(post.value.id)
    post.value.isLiked = data.isLiked
    post.value.likeCount = data.likeCount
  } catch (error) {
    // 请求层统一提示
  }
}

const handleComment = async () => {
  if (!commentText.value.trim()) {
    await showWarning('写点什么再发送吧')
    return
  }
  commenting.value = true
  try {
    const res = await createComment({
      postId: Number(route.params.id),
      content: commentText.value.trim(),
    })
    commentText.value = ''
    await loadPost()
    if (res.message?.includes('审核')) {
      await showInfo(res.message)
    } else {
      await showSuccess(res.message || '评论成功')
    }
  } catch (error) {
    // 请求层统一提示
  } finally {
    commenting.value = false
  }
}

onMounted(() => {
  loadPost()
})
</script>

<style scoped>
.page-container {
  max-width: 900px;
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
  padding: 28px;
}

.post-header,
.comment-header,
.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-line {
  color: #1A2623;
  font-size: 18px;
  margin-bottom: 6px;
}

.time-line,
.comment-header {
  color: #798C87;
  font-size: 13px;
}

.post-content {
  color: #2C3634;
  line-height: 1.9;
  margin: 24px 0 28px;
}

.comment-form {
  margin-bottom: 28px;
}

.comment-actions {
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.comment-item {
  padding: 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.55);
}

.comment-content {
  color: #2C3634;
  line-height: 1.8;
  margin-top: 8px;
}

.empty-state {
  text-align: center;
  color: #798C87;
}
</style>
