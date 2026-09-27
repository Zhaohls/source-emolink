<template>
  <div class="image-uploader">
    <div class="preview-panel" :class="{ circle }">
      <el-avatar v-if="circle" :size="96" :src="previewUrl || undefined" class="preview-avatar">
        {{ placeholderText }}
      </el-avatar>
      <div v-else-if="previewUrl" class="preview-image">
        <img :src="previewUrl" alt="预览图" />
      </div>
      <div v-else class="preview-empty">
        <span class="empty-icon">🖼️</span>
        <span>上传后可在这里预览</span>
      </div>
    </div>

    <div class="action-panel">
      <el-upload
        :show-file-list="false"
        :http-request="handleUpload"
        accept="image/*"
      >
        <el-button type="primary" :loading="uploading">上传图片</el-button>
      </el-upload>
      <el-button v-if="previewUrl" plain @click="handleRemove">移除图片</el-button>
      <p class="upload-hint">支持常见图片格式，单张不超过 20MB。</p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { uploadImage } from '@/api/file'
import { showWarning } from '@/utils/feedback'
import { resolveMediaUrl } from '@/utils/media'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  folder: {
    type: String,
    default: 'common',
  },
  placeholderText: {
    type: String,
    default: '图',
  },
  circle: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue'])

const uploading = ref(false)
const previewUrl = computed(() => resolveMediaUrl(props.modelValue))

const validateFile = (file) => {
  if (!file.type?.startsWith('image/')) {
    void showWarning('请选择图片文件')
    return false
  }
  if (file.size > 20 * 1024 * 1024) {
    void showWarning('图片大小不能超过 20MB')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  const { file, onSuccess, onError } = options
  if (!validateFile(file)) {
    onError?.(new Error('文件校验失败'))
    return
  }

  uploading.value = true
  try {
    const { data } = await uploadImage(file, props.folder)
    emit('update:modelValue', data.url || '')
    onSuccess?.(data)
  } catch (error) {
    onError?.(error)
  } finally {
    uploading.value = false
  }
}

const handleRemove = () => {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.image-uploader {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.preview-panel {
  width: 140px;
  height: 140px;
  border-radius: 8px;
  border: 1px dashed rgba(47, 77, 68, 0.25);
  background: rgba(255, 255, 255, 0.72);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.preview-panel.circle {
  width: auto;
  height: auto;
  border: none;
  background: transparent;
}

.preview-avatar {
  background: rgba(47, 77, 68, 0.12);
  color: #2F4D44;
  font-size: 30px;
}

.preview-image,
.preview-image img {
  width: 100%;
  height: 100%;
}

.preview-image img {
  object-fit: cover;
}

.preview-empty {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  color: #798C87;
  font-size: 13px;
  text-align: center;
}

.empty-icon {
  font-size: 24px;
}

.action-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.upload-hint {
  margin: 0;
  color: #798C87;
  font-size: 13px;
}
</style>
