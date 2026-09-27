const CATEGORY_META = {
  anxiety: {
    label: '焦虑缓解',
    icon: '🌊',
    gradient: 'linear-gradient(135deg, rgba(77, 150, 255, 0.22) 0%, rgba(77, 150, 255, 0.06) 100%)',
  },
  stress: {
    label: '压力疏导',
    icon: '🍃',
    gradient: 'linear-gradient(135deg, rgba(107, 203, 119, 0.22) 0%, rgba(107, 203, 119, 0.06) 100%)',
  },
  emotion: {
    label: '情绪调节',
    icon: '✨',
    gradient: 'linear-gradient(135deg, rgba(255, 217, 61, 0.22) 0%, rgba(255, 217, 61, 0.06) 100%)',
  },
  social: {
    label: '社交沟通',
    icon: '🕊️',
    gradient: 'linear-gradient(135deg, rgba(255, 140, 66, 0.22) 0%, rgba(255, 140, 66, 0.06) 100%)',
  },
  growth: {
    label: '自我成长',
    icon: '🌱',
    gradient: 'linear-gradient(135deg, rgba(155, 89, 182, 0.18) 0%, rgba(155, 89, 182, 0.05) 100%)',
  },
}

export const ARTICLE_CATEGORY_OPTIONS = [
  { label: '全部', value: '' },
  { label: '焦虑缓解', value: 'anxiety' },
  { label: '压力疏导', value: 'stress' },
  { label: '情绪调节', value: 'emotion' },
  { label: '社交沟通', value: 'social' },
  { label: '自我成长', value: 'growth' },
]

export const getArticleCategoryMeta = (category) => {
  return CATEGORY_META[category] || {
    label: category || '未分类',
    icon: '📘',
    gradient: 'linear-gradient(135deg, rgba(47, 77, 68, 0.16) 0%, rgba(47, 77, 68, 0.05) 100%)',
  }
}

export const getArticleSummary = (content, maxLength = 90) => {
  if (!content) return '暂无内容摘要'
  const plainText = String(content)
    .replace(/!\[[^\]]*\]\([^)]+\)/g, ' ')
    .replace(/\[[^\]]*\]\([^)]+\)/g, ' ')
    .replace(/[#>*`_-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()

  if (plainText.length <= maxLength) {
    return plainText
  }
  return `${plainText.slice(0, maxLength)}...`
}
