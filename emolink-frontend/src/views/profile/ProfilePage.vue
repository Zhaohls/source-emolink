<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">个人资料</h1>
      <p class="page-subtitle">更新你的昵称、联系信息和个人简介。</p>
    </div>

    <div class="glass-surface" v-loading="loading">
      <el-form :model="form" label-position="top">
        <el-form-item label="头像">
          <app-image-uploader
            v-model="form.avatar"
            folder="avatar"
            :placeholder-text="sessionStore.avatarText"
            circle
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择">
                <el-option label="未设置" :value="0" />
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="个人简介">
          <el-input v-model="form.bio" type="textarea" :rows="4" maxlength="200" show-word-limit />
        </el-form-item>

        <div class="footer-row">
          <span class="created-at">加入时间：{{ createdAtText || '暂无' }}</span>
          <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getUserInfo, updateUserInfo } from '@/api/user'
import { formatDateTime } from '@/utils/format'
import { useSessionStore } from '@/stores/session'
import { sanitizeMediaPath } from '@/utils/media'
import { showSuccess } from '@/utils/feedback'
import AppImageUploader from '@/components/common/AppImageUploader.vue'

const sessionStore = useSessionStore()
const loading = ref(false)
const saving = ref(false)
const createdAt = ref('')

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  gender: 0,
  bio: '',
  avatar: '',
})

const createdAtText = computed(() => formatDateTime(createdAt.value))

const loadProfile = async () => {
  loading.value = true
  try {
    const { data } = await getUserInfo()
    form.username = data.username || ''
    form.nickname = data.nickname || ''
    form.email = data.email || ''
    form.gender = data.gender ?? 0
    form.bio = data.bio || ''
    form.avatar = sanitizeMediaPath(data.avatar)
    createdAt.value = data.createdAt || ''
    sessionStore.setUser(data)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateUserInfo({
      nickname: form.nickname.trim(),
      email: form.email.trim(),
      gender: form.gender,
      bio: form.bio.trim(),
      avatar: form.avatar.trim(),
    })
    await sessionStore.fetchUser(true)
    await showSuccess('资料已更新')
  } catch (error) {
    // 请求层统一提示
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.page-container {
  max-width: 960px;
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
  padding: 28px;
}

.footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.created-at {
  color: #798C87;
}
</style>
