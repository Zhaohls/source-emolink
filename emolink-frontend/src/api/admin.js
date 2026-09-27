import request from './request'

// ========== 数据概览 ==========
export const getAdminDashboard = () => request.get('/admin/dashboard')

// ========== 用户管理 ==========
export const getAdminUserList = (params) => request.get('/admin/user/list', { params })
export const updateUserStatus = (userId, status) => request.put(`/admin/user/status/${userId}`, { status })

// ========== 文章管理 ==========
export const getAdminArticleList = (params) => request.get('/admin/article/list', { params })
export const createArticle = (data) => request.post('/admin/article', data)
export const updateArticle = (id, data) => request.put(`/admin/article/${id}`, data)
export const deleteArticle = (id) => request.delete(`/admin/article/${id}`)
export const updateArticleStatus = (id, status) => request.put(`/admin/article/status/${id}`, { status })

// ========== 树洞帖子审核 ==========
export const getAdminPostList = (params) => request.get('/admin/treehole/post/list', { params })
export const auditPost = (id, status) => request.put(`/admin/treehole/post/audit/${id}`, { status })
export const deletePost = (id) => request.delete(`/admin/treehole/post/${id}`)

// ========== 评论管理 ==========
export const getAdminCommentList = (params) => request.get('/admin/treehole/comment/list', { params })
export const auditComment = (id, status) => request.put(`/admin/treehole/comment/audit/${id}`, { status })
export const deleteComment = (id) => request.delete(`/admin/treehole/comment/${id}`)
