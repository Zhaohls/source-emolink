import request from './request'

export const createPost = (data) => request.post('/treehole/post', data)
export const getPostList = (params) => request.get('/treehole/post/list', { params })
export const getPostDetail = (id) => request.get(`/treehole/post/${id}`)
export const createComment = (data) => request.post('/treehole/comment', data)
export const togglePostLike = (postId) => request.post(`/treehole/like/${postId}`)
