<template>
  <div class="fixed bottom-4 right-4 z-50 space-y-2 max-w-md">
    <!-- Connection Status -->
    <Transition name="fade">
      <div v-if="!connected" class="bg-yellow-50 border border-yellow-200 rounded-lg p-3 shadow-lg">
        <div class="flex items-center space-x-2">
          <div class="h-4 w-4 border-2 border-yellow-600 border-t-transparent rounded-full animate-spin"></div>
          <span class="text-sm text-yellow-800">Connecting to real-time updates...</span>
        </div>
      </div>
    </Transition>

    <!-- Success Connection -->
    <Transition name="fade">
      <div v-if="showConnectedMessage" class="bg-green-50 border border-green-200 rounded-lg p-3 shadow-lg">
        <div class="flex items-center space-x-2">
          <svg class="h-4 w-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
          <span class="text-sm text-green-800">Real-time updates connected!</span>
        </div>
      </div>
    </Transition>

    <!-- Notifications -->
    <TransitionGroup name="notification">
      <div
        v-for="notification in visibleNotifications"
        :key="notification.id"
        class="bg-white border border-gray-200 rounded-lg p-4 shadow-lg hover:shadow-xl transition-shadow"
      >
        <div class="flex items-start space-x-3">
          <div class="flex-shrink-0">
            <div class="h-8 w-8 rounded-full flex items-center justify-center" :class="getIconClass(notification.topic)">
              <svg class="h-5 w-5" :class="getIconColor(notification.topic)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="getIconPath(notification.topic)"></path>
              </svg>
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-semibold text-gray-900 capitalize">{{ notification.topic }}</p>
            <p class="text-sm text-gray-600 mt-1 break-words">{{ notification.message }}</p>
            <p class="text-xs text-gray-400 mt-1">{{ formatTime(notification.timestamp) }}</p>
          </div>
          <button
            @click="dismissNotification(notification.id)"
            class="flex-shrink-0 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useWebSocket } from '@/composables/useWebSocket'

const { connect, disconnect, connected, messages } = useWebSocket()

interface Notification {
  id: number
  topic: string
  message: string
  timestamp: Date
}

const notifications = ref<Notification[]>([])
const showConnectedMessage = ref(false)
let notificationId = 0

const visibleNotifications = computed(() => {
  return notifications.value.slice(-3) // Show only last 3 notifications
})

// Watch for connection status
watch(connected, (isConnected) => {
  if (isConnected) {
    showConnectedMessage.value = true
    setTimeout(() => {
      showConnectedMessage.value = false
    }, 3000)
  }
})

// Watch for new messages
watch(messages, (newMessages) => {
  const latestMessage = newMessages[newMessages.length - 1]
  if (latestMessage) {
    notifications.value.push({
      id: notificationId++,
      topic: latestMessage.topic,
      message: latestMessage.message,
      timestamp: latestMessage.timestamp
    })

    // Auto dismiss after 8 seconds
    const currentId = notificationId - 1
    setTimeout(() => {
      dismissNotification(currentId)
    }, 8000)
  }
})

const getIconClass = (topic: string): string => {
  const classes: Record<string, string> = {
    shipments: 'bg-blue-100',
    deliveries: 'bg-green-100',
    routes: 'bg-purple-100',
    cargo: 'bg-orange-100',
    vendors: 'bg-indigo-100'
  }
  return classes[topic] || 'bg-gray-100'
}

const getIconColor = (topic: string): string => {
  const colors: Record<string, string> = {
    shipments: 'text-blue-600',
    deliveries: 'text-green-600',
    routes: 'text-purple-600',
    cargo: 'text-orange-600',
    vendors: 'text-indigo-600'
  }
  return colors[topic] || 'text-gray-600'
}

const getIconPath = (topic: string): string => {
  const paths: Record<string, string> = {
    shipments: 'M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4',
    deliveries: 'M5 13l4 4L19 7',
    routes: 'M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7',
    cargo: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
    vendors: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z'
  }
  return paths[topic] || 'M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9'
}

const formatTime = (timestamp: Date): string => {
  return new Date(timestamp).toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const dismissNotification = (id: number) => {
  notifications.value = notifications.value.filter(n => n.id !== id)
}

onMounted(() => {
  connect()
})

onUnmounted(() => {
  disconnect()
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.notification-enter-active {
  transition: all 0.4s ease;
}

.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.notification-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

.notification-move {
  transition: transform 0.3s ease;
}
</style>
