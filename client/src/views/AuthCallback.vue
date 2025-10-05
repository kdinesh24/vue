<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl p-8 max-w-md w-full text-center">
      <div v-if="loading" class="space-y-4">
        <svg class="animate-spin h-12 w-12 text-blue-600 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <h2 class="text-xl font-semibold text-gray-900">Completing sign in...</h2>
        <p class="text-gray-600">Please wait while we set up your account.</p>
      </div>

      <div v-if="error" class="space-y-4">
        <div class="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center mx-auto">
          <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </div>
        <h2 class="text-xl font-semibold text-gray-900">Sign in failed</h2>
        <p class="text-gray-600">{{ error }}</p>
        <button
          @click="goToLogin"
          class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Back to Login
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'

const router = useRouter()
const { get } = useApi()

const loading = ref(true)
const error = ref('')

const fetchUserAndRedirect = async () => {
  try {
    // Fetch user info from backend after OAuth
    const response = await get<any>('/auth/user')
    
    if (response.success && response.user) {
      // Store user info in localStorage
      localStorage.setItem('user', JSON.stringify(response.user))
      
      // Redirect to dashboard
      setTimeout(() => {
        router.push('/dashboard')
      }, 500)
    } else {
      error.value = 'Failed to retrieve user information. Please try again.'
      loading.value = false
    }
  } catch (err: any) {
    console.error('Error fetching user:', err)
    error.value = 'An error occurred during authentication. Please try again.'
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

onMounted(() => {
  fetchUserAndRedirect()
})
</script>
