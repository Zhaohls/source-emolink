import request from './request'

export const registerUser = (data) => request.post('/user/register', data)
export const loginUser = (data) => request.post('/user/login', data)
export const getUserInfo = () => request.get('/user/info')
export const updateUserInfo = (data) => request.put('/user/update', data)
export const updateUserPassword = (data) => request.put('/user/password', data)
