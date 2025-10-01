<template>
  <div class="container mx-auto px-4 py-8 max-w-4xl">
    <div class="space-y-6">
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/routes')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Routes
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Create New Route</h1>
          <p class="mt-2 text-gray-600">Add a new shipping route</p>
        </div>
      </div>

      <Card class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">Route Information</CardTitle>
          <CardDescription>
            Enter the route details below
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

            <div class="flex justify-end space-x-4 pt-6">
              <Button type="button" @click="$router.push('/routes')" variant="outline">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Plus v-if="!isSubmitting" class="mr-2 h-4 w-4" />
                <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Creating...' : 'Create Route' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { ArrowLeft, Plus, Loader2 } from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'

const router = useRouter()
const { createRoute } = useApi()

const isSubmitting = ref(false)

const formData = reactive({
  originPort: '',
  destinationPort: '',
  distance: 0,
  duration: 0,
  transportationMode: '',
  cost: 0
})

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    await createRoute(formData)
    router.push('/routes')
  } catch (error) {
    console.error('Error creating route:', error)
    alert('Failed to create route. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}
</script>