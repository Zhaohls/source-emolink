/**
 * 格式化日期时间字符串
 * 将 "2026-04-15T18:30:32" 格式转为 "2026-04-15 18:30:32"
 */
export const formatDateTime = (val) => {
  if (!val) return ''
  return String(val).replace('T', ' ').replace(/\.\d+$/, '')
}

/**
 * el-table 列格式化器
 * 用法: <el-table-column :formatter="dateFormatter" />
 */
export const dateFormatter = (row, column, cellValue) => {
  return formatDateTime(cellValue)
}
