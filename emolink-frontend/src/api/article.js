import request from './request'

export const getArticleList = (params) => request.get('/article/list', { params })
export const getArticleDetail = (id) => request.get(`/article/${id}`)
export const searchArticleList = (params) => request.get('/article/search', { params })
export const toggleArticleFavorite = (articleId) => request.post(`/article/favorite/${articleId}`)
export const getFavoriteArticleList = (params) => request.get('/article/favorite/list', { params })
