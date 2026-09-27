import request from './request'

export const uploadImage = (file, folder = 'common') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
