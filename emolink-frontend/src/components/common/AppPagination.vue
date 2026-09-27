<template>
  <div class="app-pagination" v-if="total > 0">
    <el-pagination
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      @update:current-page="handleCurrentChange"
      @update:page-size="handleSizeChange"
    />
  </div>
</template>

<script setup>
const props = defineProps({
  total: {
    type: Number,
    default: 0,
  },
  pageNum: {
    type: Number,
    default: 1,
  },
  pageSize: {
    type: Number,
    default: 10,
  },
  pageSizes: {
    type: Array,
    default: () => [8, 12, 20, 50],
  },
})

const emit = defineEmits(['update:pageNum', 'update:pageSize', 'change'])

const handleCurrentChange = (value) => {
  emit('update:pageNum', value)
  emit('change')
}

const handleSizeChange = (value) => {
  emit('update:pageSize', value)
  emit('update:pageNum', 1)
  emit('change')
}
</script>

<style scoped>
.app-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.app-pagination :deep(.el-pagination button),
.app-pagination :deep(.el-pager li),
.app-pagination :deep(.el-input__wrapper) {
  border-radius: 8px;
}
</style>
