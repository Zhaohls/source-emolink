import request from './request'

export const createDiary = (data) => request.post('/emotion/diary', data)
export const getDiaryList = (params) => request.get('/emotion/diary/list', { params })
export const getDiaryDetail = (id) => request.get(`/emotion/diary/${id}`)
export const updateDiary = (id, data) => request.put(`/emotion/diary/${id}`, data)
export const deleteDiary = (id) => request.delete(`/emotion/diary/${id}`)
export const getDailyReport = (params) => request.get('/emotion/report/daily', { params })
export const getWeeklyReport = (params) => request.get('/emotion/report/weekly', { params })
export const getCalendarData = (params) => request.get('/emotion/calendar', { params })
