<template>
  <div class="admin-page">
    <div class="page-header">
      <h1 class="page-title">评论管理</h1>
    </div>

    <el-tabs v-model="currentTab" @tab-change="loadData" class="audit-tabs">
      <el-tab-pane label="待审核" name="0">
        <template #label><span>🟡 待审核 <el-badge :value="pendingCount" v-if="pendingCount > 0" /></span></template>
      </el-tab-pane>
      <el-tab-pane label="已通过" name="1" />
      <el-tab-pane label="违规删除" name="2" />
      <el-tab-pane label="全部" name="" />
    </el-tabs>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe class="admin-table" :row-class-name="rowClassName">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="postId" label="帖子ID" width="90" />
        <el-table-column prop="username" label="真实用户" width="120" />
        <el-table-column prop="anonymousName" label="匿名昵称" width="160" />
        <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="评论时间" width="170" :formatter="dateFormatter" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button link type="success" size="small" @click="handleAudit(row, 1)">通过</el-button>
              <el-button link type="danger" size="small" @click="handleAudit(row, 2)">违规</el-button>
            </template>
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
import { getAdminCommentList, auditComment, deleteComment } from '@/api/admin'
import { showConfirm, showSuccess } from '@/utils/feedback'
import { dateFormatter } from '@/utils/datetime'

const currentTab = ref('0')
const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const pendingCount = ref(0)

const statusLabel = (s) => ({ 0: '待审核', 1: '正常', 2: '违规删除' }[s] || '未知')
const statusTagType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const rowClassName = ({ row }) => row.status === 0 ? 'pending-row' : ''

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (currentTab.value !== '') params.status = Number(currentTab.value)
    const { data } = await getAdminCommentList(params)
    tableData.value = data?.records || []
    total.value = data?.total || 0
    if (currentTab.value === '0') pendingCount.value = total.value
  } catch (e) {
    tableData.value = [
      { id: 1, postId: 1, userId: 3, username: 'lisi', anonymousName: '快乐的星星#1024', content: '加油！你一定能找到方向的', status: 0, createdAt: '2026-04-14 17:00' },
      { id: 2, postId: 2, userId: 6, username: 'wangwu', anonymousName: '安静的月亮#5520', content: '一起努力！', status: 1, createdAt: '2026-04-14 18:30' },
    ]
    total.value = 2
    pendingCount.value = 1
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row, status) => {
  const action = status === 1 ? '通过' : '标记为违规'
  try { await showConfirm(`确定将此评论${action}？`) } catch { return }
  try {
    await auditComment(row.id, status)
    await showSuccess(`已${action}`)
    loadData()
  } catch (e) { /* interceptor */ }
}

const handleDelete = async (row) => {
  try { await showConfirm('确定删除此评论？不可撤销。', { type: 'error' }) } catch { return }
  try {
    await deleteComment(row.id)
    await showSuccess('已删除')
    loadData()
  } catch (e) { /* interceptor */ }
}

onMounted(loadData)
</script>

<style scoped>
.admin-page { width: 100%; }
.page-header { margin-bottom: 16px; }
.page-title { font-size: 26px; font-weight: 700; color: #1A2623; }
.audit-tabs { margin-bottom: 20px; }
.table-card { background: #FFF; border-radius: 16px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.admin-table :deep(.el-table__header th) { background: #F8F9FA !important; color: #555; font-weight: 600; }
.admin-table :deep(.pending-row) { background-color: #FFFBE6 !important; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 16px; border-top: 1px solid #F0F0F0; }
</style>
