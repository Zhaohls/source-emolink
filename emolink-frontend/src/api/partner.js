import request from './request'

export const joinPartnerMatch = () => request.post('/partner/match')
export const getPartnerMatchStatus = () => request.get('/partner/match/status')
export const getPartnerMessageList = (params) => request.get('/partner/message/list', { params })
export const endPartnerMatch = (data) => request.post('/partner/match/end', data)
