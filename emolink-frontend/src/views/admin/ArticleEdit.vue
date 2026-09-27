<template>
  <div class="admin-page">
    <div class="page-header">
      <h1 class="page-title">{{ isEdit ? '编辑文章' : '新增文章' }}</h1>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>

    <div class="edit-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" label-position="top">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="选择分类" style="width: 100%;">
                <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">上架</el-radio>
                <el-radio :value="0">下架（草稿）</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="正文内容（Markdown）" prop="content">
          <div class="editor-area">
            <div class="editor-pane">
              <el-input v-model="form.content" type="textarea" :rows="18" placeholder="在此输入 Markdown 内容..." resize="none" />
            </div>
            <div class="preview-pane">
              <div class="preview-label">预览</div>
              <div class="preview-body" v-html="renderedContent"></div>
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSubmit">{{ isEdit ? '保存修改' : '发布文章' }}</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { marked } from 'marked'
import { getAdminArticleList, createArticle, updateArticle } from '@/api/admin'
import { showSuccess } from '@/utils/feedback'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const articleId = computed(() => route.query.id)
const isEdit = computed(() => !!articleId.value)

const categories = [
  { value: 'emotion', label: '情绪调节' }, { value: 'communication', label: '沟通技巧' },
  { value: 'growth', label: '自我成长' }, { value: 'psychology', label: '心理科普' },
  { value: 'anxiety', label: '焦虑应对' }, { value: 'stress', label: '压力管理' },
]

const form = ref({
  title: '', content: '', category: '', status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

const renderedContent = computed(() => {
  try { return marked(form.value.content || '*暂无内容*') } catch { return '' }
})

onMounted(async () => {
  if (isEdit.value) {
    try {
      const { data } = await getAdminArticleList({ pageNum: 1, pageSize: 1 })
      // 实际应调用详情接口，这里先做兼容
      const article = data?.records?.find(a => String(a.id) === String(articleId.value))
      if (article) {
        form.value = { title: article.title, content: article.content || '', category: article.category, status: article.status }
      }
    } catch (e) { /* 可能需要单独的详情接口 */ }
  }
})

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateArticle(articleId.value, form.value)
      await showSuccess('文章已更新')
    } else {
      await createArticle(form.value)
      await showSuccess('文章已发布')
    }
    router.push('/admin/articles')
  } catch (e) { /* 错误由拦截器处理 */ }
  finally { saving.value = false }
}
</script>

<style scoped>
.admin-page { width: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 26px; font-weight: 700; color: #1A2623; }
.edit-card { background: #FFF; border-radius: 16px; padding: 32px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }

/* Markdown 编辑区 */
.editor-area { display: flex; gap: 16px; width: 100%; min-height: 400px; }
.editor-pane { flex: 1; }
.editor-pane :deep(.el-textarea__inner) { height: 100% !important; min-height: 400px; font-family: 'SFMono-Regular', Consolas, monospace; font-size: 14px; }
.preview-pane { flex: 1; border: 1px solid #eee; border-radius: 8px; padding: 16px; overflow-y: auto; max-height: 450px; background: #FAFAFA; }
.preview-label { font-size: 12px; color: #999; margin-bottom: 12px; text-transform: uppercase; letter-spacing: 1px; }
.preview-body { font-size: 15px; line-height: 1.8; color: #333; }
.preview-body :deep(h1) { font-size: 24px; margin: 16px 0 8px; }
.preview-body :deep(h2) { font-size: 20px; margin: 14px 0 6px; }
.preview-body :deep(h3) { font-size: 17px; margin: 12px 0 4px; }
.preview-body :deep(code) { background: #F0F0F0; padding: 2px 6px; border-radius: 4px; font-size: 13px; }
.preview-body :deep(pre) { background: #F5F5F5; padding: 16px; border-radius: 8px; overflow-x: auto; }
</style>
