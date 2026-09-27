import { Client } from '@stomp/stompjs'

const resolveBrokerUrl = (token) => {
  const rawBase = import.meta.env.VITE_WS_BASE_URL || '/ws'
  const url = new URL(rawBase, window.location.origin)
  if (url.protocol === 'http:') {
    url.protocol = 'ws:'
  } else if (url.protocol === 'https:') {
    url.protocol = 'wss:'
  }
  if (token) {
    url.searchParams.set('token', token)
  }
  return url.toString()
}

export const createPartnerSocketClient = ({
  token,
  onConnect,
  onMessage,
  onMatchEvent,
  onError,
}) => {
  const client = new Client({
    brokerURL: resolveBrokerUrl(token),
    connectHeaders: token
      ? {
          Authorization: `Bearer ${token}`,
        }
      : {},
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    debug: () => {},
  })

  client.onConnect = () => {
    client.subscribe('/user/queue/partner/message', (frame) => {
      onMessage?.(JSON.parse(frame.body))
    })
    client.subscribe('/user/queue/partner/match', (frame) => {
      onMatchEvent?.(JSON.parse(frame.body))
    })
    onConnect?.(client)
  }

  client.onStompError = (frame) => {
    onError?.(frame.headers.message || '连接信箱服务失败')
  }

  client.onWebSocketError = () => {
    onError?.('信箱连接异常，请稍后重试')
  }

  client.activate()
  return client
}

export const sendPartnerSocketMessage = (client, payload) => {
  if (!client?.connected) {
    throw new Error('信箱尚未连接')
  }

  client.publish({
    destination: '/app/partner/send',
    body: JSON.stringify(payload),
  })
}
