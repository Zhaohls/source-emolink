import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

export const formatDateTime = (value, pattern = 'YYYY年M月D日 HH:mm') => {
  if (!value) return ''
  return dayjs(value).format(pattern)
}

export const formatDate = (value, pattern = 'YYYY年M月D日') => {
  if (!value) return ''
  return dayjs(value).format(pattern)
}

export const formatDateValue = (value, pattern = 'YYYY-MM-DD') => {
  if (!value) return ''
  return dayjs(value).format(pattern)
}

export const formatFriendlyDate = (value) => {
  if (!value) return ''
  return dayjs(value).format('M月D日 dddd')
}

export const formatMonthLabel = (value, pattern = 'YYYY年M月') => {
  if (!value) return ''
  return dayjs(value).format(pattern)
}

export const formatRelativeTime = (value) => {
  if (!value) return ''
  const target = dayjs(value)
  const now = dayjs()

  if (target.isSame(now, 'day')) {
    return target.format('HH:mm')
  }
  if (target.isSame(now.subtract(1, 'day'), 'day')) {
    return `昨天 ${target.format('HH:mm')}`
  }
  if (target.isSame(now, 'year')) {
    return target.format('M月D日 HH:mm')
  }
  return target.format('YYYY年M月D日 HH:mm')
}

export const getWeekStart = (value = new Date()) => {
  const date = dayjs(value)
  const day = date.day()
  const diff = day === 0 ? 6 : day - 1
  return date.subtract(diff, 'day').startOf('day')
}

export const getInitial = (text) => {
  if (!text) return '用'
  return String(text).trim().slice(0, 1) || '用'
}
