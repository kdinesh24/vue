<template>
  <div class="container mx-auto px-4 py-8 max-w-4xl">
    <div class="space-y-6">
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/routes')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Routes
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Edit Route</h1>
          <p class="mt-2 text-gray-600">Update route information</p>
        </div>
      </div>

      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading route details...</span>
      </div>

      <Card v-else-if="!loadError" class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">Route Information</CardTitle>
          <CardDescription>
            Update the route details below
          </CardDescription>
        </CardHeader>
        <CardContent class="pt-2">
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Origin Port</label>
                <Input
                  v-model="formData.originPort"
                  placeholder="Enter origin port"
                  required
                />
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Destination Port</label>
                <Input
                  v-model="formData.destinationPort"
                  placeholder="Enter destination port"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Distance (km)</label>
                <Input
                  v-model.number="formData.distance"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="Enter distance in kilometers"
                  required
                />
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Duration (days)</label>
                <Input
                  v-model.number="formData.duration"
                  type="number"
                  min="1"
                  placeholder="Enter duration in days"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Transportation Mode</label>
                <select 
                  v-model="formData.transportationMode" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                >
                  <option value="">Select transportation mode</option>
                  <option value="Sea">Sea</option>
                  <option value="Air">Air</option>
                  <option value="Land">Land</option>
                  <option value="Rail">Rail</option>
                  <option value="Multimodal">Multimodal</option>
                </select>
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Cost ($)</label>
                <Input
                  v-model.number="formData.cost"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="Enter route cost"
                  required
                />
              </div>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Status</label>
              <select 
                v-model="formData.status" 
                class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                required
              >
                <option value="Active">Active</option>
                <option value="Delayed">Delayed</option>
                <option value="Closed">Closed</option>
              </select>
            </div>

            <div class="flex justify-end space-x-4 pt-6">
              <Button type="button" @click="$router.push('/routes')" variant="outline">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Save v-if="!isSubmitting" class="mr-2 h-4 w-4" />
                <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Updating...' : 'Update Route' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <Card v-else class="border-0 shadow-sm">
        <CardContent class="text-center py-12">
          <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
          <p class="text-gray-600 mb-4">{{ loadError }}</p>
          <Button @click="$router.push('/routes')" variant="outline">
            Back to Routes List
          </Button>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { ArrowLeft, Save, Loader2, AlertCircle } from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'

const router = useRouter()
const route = useRoute()
const { updateRoute, getRoute } = useApi()

const isLoading = ref(true)
const isSubmitting = ref(false)
const loadError = ref<string | null>(null)
const routeId = Number(route.params.id)

const formData = reactive({
  originPort: '',
  destinationPort: '',
  distance: 0,
  duration: 0,
  transportationMode: '',
  cost: 0,
  status: 'Active'
})

const loadRoute = async () => {
  try {
    const routeData = await getRoute(routeId)
    if (!routeData) {
      loadError.value = 'Route not found'
      return
    }

    // Populate form with existing data
    formData.originPort = routeData.originPort || ''
    formData.destinationPort = routeData.destinationPort || ''
    formData.distance = routeData.distance || 0
    formData.duration = routeData.duration || 0
    formData.transportationMode = routeData.transportationMode || ''
    formData.cost = routeData.cost || 0
    formData.status = routeData.status || 'Active'
  } catch (error) {
    console.error('Error loading route:', error)
    loadError.value = 'Failed to load route details'
  } finally {
    isLoading.value = false
  }
}

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    await updateRoute(routeId, formData)
    router.push('/routes')
  } catch (error) {
    console.error('Error updating route:', error)
    alert('Failed to update route. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  loadRoute()
})
</script>
