const PLACEHOLDER_MEDIA = new Set([
  '/avatars/default.png',
])

export const sanitizeMediaPath = (value) => {
  const path = typeof value === 'string' ? value.trim() : ''
  if (!path || PLACEHOLDER_MEDIA.has(path)) {
    return ''
  }
  return path
}

export const resolveMediaUrl = (value) => {
  const path = sanitizeMediaPath(value)
  if (!path) {
    return ''
  }
  if (/^(https?:)?\/\//.test(path) || path.startsWith('data:')) {
    return path
  }
  return path.startsWith('/') ? path : `/${path}`
}
