import { ref, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export interface WebSocketMessage {
  topic: string
  message: string
  timestamp: Date
}

export const useWebSocket = () => {
  const client = ref<Client | null>(null)
  const connected = ref(false)
  const messages = ref<WebSocketMessage[]>([])
  const reconnectAttempts = ref(0)
  const maxReconnectAttempts = 5

  const connect = () => {
    if (client.value && client.value.active) {
      console.log('WebSocket already connected')
      return
    }

    client.value = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/ws'),
      
      connectHeaders: {},
      
      debug: (str) => {
        console.log('STOMP Debug:', str)
      },
      
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        console.log('WebSocket connected successfully')
        connected.value = true
        reconnectAttempts.value = 0
        
        // Subscribe to all topics
        subscribeToTopic('/topic/shipments')
        subscribeToTopic('/topic/deliveries')
        subscribeToTopic('/topic/routes')
        subscribeToTopic('/topic/cargo')
        subscribeToTopic('/topic/vendors')
      },
      
      onDisconnect: () => {
        console.log('WebSocket disconnected')
        connected.value = false
      },
      
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message'])
        console.error('Details:', frame.body)
        connected.value = false
        
        // Attempt to reconnect
        if (reconnectAttempts.value < maxReconnectAttempts) {
          reconnectAttempts.value++
          console.log(`Reconnecting... Attempt ${reconnectAttempts.value}`)
          setTimeout(() => connect(), 5000)
        }
      },

      onWebSocketError: (event) => {
        console.error('WebSocket error:', event)
        connected.value = false
      }
    })

    client.value.activate()
  }

  const subscribeToTopic = (topic: string) => {
    if (client.value && client.value.connected) {
      client.value.subscribe(topic, (message) => {
        const webSocketMessage: WebSocketMessage = {
          topic: topic.replace('/topic/', ''),
          message: message.body,
          timestamp: new Date()
        }
        messages.value.push(webSocketMessage)
        console.log(`📨 Received from ${topic}:`, message.body)
        
        // Dispatch custom event for page-specific updates
        window.dispatchEvent(new CustomEvent(`${topic.replace('/topic/', '')}-updated`, {
          detail: webSocketMessage
        }))
      })
      console.log(`✅ Subscribed to ${topic}`)
    } else {
      console.warn(`Cannot subscribe to ${topic} - client not connected`)
    }
  }

  const disconnect = () => {
    if (client.value) {
      client.value.deactivate()
      console.log('WebSocket disconnected manually')
    }
  }

  const clearMessages = () => {
    messages.value = []
  }

  const getRecentMessages = (count: number = 10) => {
    return messages.value.slice(-count)
  }

  // Auto cleanup on unmount
  onUnmounted(() => {
    disconnect()
  })

  return {
    connect,
    disconnect,
    connected,
    messages,
    clearMessages,
    getRecentMessages,
    reconnectAttempts
  }
}
