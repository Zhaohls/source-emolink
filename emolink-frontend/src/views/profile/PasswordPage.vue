<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">修改密码</h1>
      <p class="page-subtitle">定期更新密码可以让账号更安全。</p>
    </div>

    <div class="glass-surface">
      <el-form :model="form" label-position="top">
        <el-form-item label="旧密码">
          <el-input v-model="form.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="form.confirmPassword" type="password" show-password />
        </el-form-item>

        <div class="actions">
          <el-button type="primary" :loading="saving" @click="handleSave">确认修改</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { updateUserPassword } from '@/api/user'
import { showSuccess, showWarning } from '@/utils/feedback'

const saving = ref(false)
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const handleSave = async () => {
  if (!form.oldPassword || !form.newPassword) {
    await showWarning('请填写完整密码信息')
    return
  }
  if (form.newPassword.length < 6) {
    await showWarning('新密码至少 6 位')
    return
  }
  if (form.newPassword !== form.confirmPassword) {
    await showWarning('两次输入的新密码不一致')
    return
  }

  saving.value = true
  try {
    await updateUserPassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword,
    })
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    await showSuccess('密码已更新')
  } catch (error) {
    // 请求层统一提示
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.page-container {
  max-width: 720px;
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

.actions {
  display: flex;
  justify-content: flex-end;
}
</style>
