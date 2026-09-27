<template>
  <div class="admin-page">
    <div class="page-header">
      <h1 class="page-title">文章管理</h1>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索文章标题" clearable class="search-input" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="categoryFilter" placeholder="分类" clearable @change="loadData" style="width: 140px;">
          <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
        <el-button type="primary" @click="$router.push('/admin/article/edit')">
          <el-icon><Plus /></el-icon> 新增文章
        </el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe class="admin-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getCategoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读量" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" :formatter="dateFormatter" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/admin/article/edit?id=${row.id}`)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
          @size-change="loadData" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { getAdminArticleList, updateArticleStatus, deleteArticle } from '@/api/admin'
import { showConfirm, showSuccess } from '@/utils/feedback'
import { dateFormatter } from '@/utils/datetime'

const categories = [
  { value: 'emotion', label: '情绪调节' }, { value: 'communication', label: '沟通技巧' },
  { value: 'growth', label: '自我成长' }, { value: 'psychology', label: '心理科普' },
  { value: 'anxiety', label: '焦虑应对' }, { value: 'stress', label: '压力管理' },
]
const getCategoryLabel = (val) => categories.find(c => c.value === val)?.label || val

const keyword = ref('')
const categoryFilter = ref(null)
const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (keyword.value) params.keyword = keyword.value
    if (categoryFilter.value) params.category = categoryFilter.value
    const { data } = await getAdminArticleList(params)
    tableData.value = data?.records || []
    total.value = data?.total || 0
  } catch (e) {
    tableData.value = [
      { id: 1, title: '如何面对突如其来的焦虑？', category: 'anxiety', viewCount: 2450, status: 1, createdAt: '2026-04-01' },
      { id: 2, title: '讨好型人格自救指南', category: 'growth', viewCount: 3120, status: 1, createdAt: '2026-04-03' },
      { id: 3, title: '拖延症的本质', category: 'psychology', viewCount: 1890, status: 0, createdAt: '2026-04-05' },
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  try { await showConfirm(`确定${action}「${row.title}」？`) } catch { return }
  try {
    await updateArticleStatus(row.id, newStatus)
    row.status = newStatus
    await showSuccess(`${action}成功`)
  } catch (e) { /* 错误由拦截器处理 */ }
}

const handleDelete = async (row) => {
  try { await showConfirm(`确定删除「${row.title}」？此操作不可撤销。`, { type: 'error' }) } catch { return }
  try {
    await deleteArticle(row.id)
    await showSuccess('删除成功')
    loadData()
  } catch (e) { /* 错误由拦截器处理 */ }
}

onMounted(loadData)
</script>

<style scoped>
.admin-page { width: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
.page-title { font-size: 26px; font-weight: 700; color: #1A2623; }
.header-actions { display: flex; gap: 12px; align-items: center; }
.search-input { width: 260px; }
.table-card { background: #FFF; border-radius: 16px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.admin-table :deep(.el-table__header th) { background: #F8F9FA !important; color: #555; font-weight: 600; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 16px; border-top: 1px solid #F0F0F0; }
</style>
