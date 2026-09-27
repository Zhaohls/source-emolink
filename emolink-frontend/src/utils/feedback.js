import { ElMessageBox } from 'element-plus'

const baseOptions = {
  customClass: 'app-feedback-box',
  closeOnClickModal: false,
  closeOnPressEscape: false,
  closeOnHashChange: false,
  showClose: false,
  autofocus: false,
}

const normalizeMessage = (message) => {
  if (message instanceof Error) {
    return message.message || '操作未完成'
  }
  return String(message || '操作未完成')
}

export const showAlert = (message, options = {}) => ElMessageBox.alert(
  normalizeMessage(message),
  options.title || '提示',
  {
    ...baseOptions,
    confirmButtonText: options.confirmButtonText || '我知道了',
    type: options.type || 'info',
  }
)

export const showSuccess = (message, title = '操作成功') => showAlert(message, {
  title,
  type: 'success',
})

export const showWarning = (message, title = '请注意') => showAlert(message, {
  title,
  type: 'warning',
})

export const showInfo = (message, title = '提示') => showAlert(message, {
  title,
  type: 'info',
})

export const showError = (message, title = '操作失败') => showAlert(message, {
  title,
  type: 'error',
})

export const showConfirm = (message, options = {}) => ElMessageBox.confirm(
  normalizeMessage(message),
  options.title || '请确认',
  {
    ...baseOptions,
    distinguishCancelAndClose: true,
    confirmButtonText: options.confirmButtonText || '确定',
    cancelButtonText: options.cancelButtonText || '取消',
    type: options.type || 'warning',
  }
)
