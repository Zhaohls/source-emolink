<template>
  <div class="admin-page">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <div class="header-actions">
        <el-input v-model="keyword" placeholder="搜索用户名 / 昵称" clearable class="search-input" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="用户状态" clearable @change="loadData" style="width: 140px;">
          <el-option label="正常" :value="1" />
          <el-option label="已禁用" :value="0" />
        </el-select>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe class="admin-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'" size="small">
              {{ row.role === 'admin' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="diaryCount" label="日记数" width="90" />
        <el-table-column prop="createdAt" label="注册时间" width="170" :formatter="dateFormatter" />
        <el-table-column label="状态" width="100" fixed="right">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              :loading="row._switching"
              @change="(val) => handleStatusChange(row, val)"
              active-text="正常"
              inactive-text="禁用"
              inline-prompt
              :disabled="row.role === 'admin'"
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getAdminUserList, updateUserStatus } from '@/api/admin'
import { showConfirm, showSuccess, showError } from '@/utils/feedback'
import { dateFormatter } from '@/utils/datetime'

const keyword = ref('')
const statusFilter = ref(null)
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
    if (statusFilter.value !== null && statusFilter.value !== '') params.status = statusFilter.value
    const { data } = await getAdminUserList(params)
    tableData.value = (data?.records || []).map(r => ({ ...r, _switching: false }))
    total.value = data?.total || 0
  } catch (e) {
    // Mock 数据
    tableData.value = [
      { id: 1, username: 'admin', nickname: '管理员', email: 'admin@emolink.com', role: 'admin', status: 1, diaryCount: 0, createdAt: '2026-04-01 10:00:00', _switching: false },
      { id: 2, username: 'zhangsan', nickname: '小张', email: 'zhangsan@email.com', role: 'user', status: 1, diaryCount: 23, createdAt: '2026-04-02 14:30:00', _switching: false },
      { id: 3, username: 'lisi', nickname: '李四', email: null, role: 'user', status: 0, diaryCount: 5, createdAt: '2026-04-05 09:00:00', _switching: false },
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row, val) => {
  const newStatus = val ? 1 : 0
  const action = newStatus === 1 ? '启用' : '禁用'
  try {
    await showConfirm(`确定要${action}用户「${row.nickname || row.username}」吗？`, { title: `${action}确认` })
  } catch {
    return // 用户取消
  }
  row._switching = true
  try {
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    await showSuccess(`已${action}用户「${row.nickname || row.username}」`)
  } catch (e) {
    await showError(`${action}失败`)
  } finally {
    row._switching = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.admin-page { width: 100%; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
.page-title { font-size: 26px; font-weight: 700; color: #1A2623; }
.header-actions { display: flex; gap: 12px; }
.search-input { width: 260px; }

.table-card {
  background: #FFF; border-radius: 16px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.admin-table { width: 100%; }
.admin-table :deep(.el-table__header th) { background: #F8F9FA !important; color: #555; font-weight: 600; }

.pagination-bar {
  display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 16px; border-top: 1px solid #F0F0F0;
}
</style>
