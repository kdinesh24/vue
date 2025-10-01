<template>
  <div class="min-h-screen bg-gray-50 flex">
    <!-- Sidebar -->
    <div class="fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg border-r border-gray-200">
      <!-- Logo/Header -->
      <div class="flex h-16 items-center justify-center border-b border-gray-200">
        <h1 class="text-xl font-bold text-gray-900">Supply Chain</h1>
      </div>
      
      <!-- Navigation -->
      <nav class="mt-8 px-4 pb-24 overflow-y-auto">
        <div class="space-y-2">
          <router-link
            to="/"
            class="flex items-center rounded-lg px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700 border-r-2 border-blue-700': $route.path === '/' }"
          >
            <svg class="mr-3 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z" />
            </svg>
            Dashboard
          </router-link>
          
          <router-link
            to="/shipments"
            class="flex items-center rounded-lg px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700 border-r-2 border-blue-700': $route.path.startsWith('/shipments') }"
          >
            <svg class="mr-3 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10" />
            </svg>
            Shipments
          </router-link>
          
          <router-link
            to="/vendors"
            class="flex items-center rounded-lg px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700 border-r-2 border-blue-700': $route.path.startsWith('/vendors') }"
          >
            <svg class="mr-3 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            Vendors
          </router-link>
          
          <router-link
            to="/cargo"
            class="flex items-center rounded-lg px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700 border-r-2 border-blue-700': $route.path.startsWith('/cargo') }"
          >
            <svg class="mr-3 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10" />
            </svg>
            Cargo
          </router-link>
          
          <router-link
            to="/routes"
            class="flex items-center rounded-lg px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700 border-r-2 border-blue-700': $route.path.startsWith('/routes') }"
          >
            <svg class="mr-3 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
            </svg>
            Routes
          </router-link>
          
          <router-link
            to="/deliveries"
            class="flex items-center rounded-lg px-4 py-3 text-sm font-medium text-gray-700 hover:bg-gray-100 hover:text-gray-900 transition-colors"
            :class="{ 'bg-blue-50 text-blue-700 border-r-2 border-blue-700': $route.path.startsWith('/deliveries') }"
          >
            <svg class="mr-3 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Deliveries
          </router-link>
        </div>
      </nav>
      
      <!-- Bottom section -->
      <div class="absolute bottom-0 left-0 right-0 p-4">
        <div class="bg-gray-50 rounded-lg p-3">
          <div class="flex items-center">
            <div class="h-8 w-8 rounded-full bg-blue-600 flex items-center justify-center">
              <span class="text-sm font-medium text-white">SC</span>
            </div>
            <div class="ml-3">
              <p class="text-xs font-medium text-gray-900">Supply Chain</p>
              <p class="text-xs text-gray-500">Admin Panel</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 ml-64">
      <!-- Top header bar -->
      <header class="bg-white border-b border-gray-200 px-8 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-4">
            <h2 class="text-lg font-semibold text-gray-900">
              {{ getPageTitle() }}
            </h2>
          </div>
          <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-2">
              <div class="h-2 w-2 bg-green-400 rounded-full"></div>
              <span class="text-sm text-gray-600">Online</span>
            </div>
          </div>
        </div>
      </header>
      
      <!-- Page content -->
      <main class="p-8">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()

const getPageTitle = () => {
  const titles: Record<string, string> = {
    '/': 'Dashboard',
    '/shipments': 'Shipments',
    '/vendors': 'Vendors',
    '/cargo': 'Cargo Management',
    '/routes': 'Routes',
    '/deliveries': 'Deliveries'
  }
  
  const path = route.path
  return titles[path] || titles[Object.keys(titles).find(key => path.startsWith(key)) || '/'] || 'Supply Chain'
}
</script>
