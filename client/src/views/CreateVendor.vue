<template>
  <div class="container mx-auto px-4 py-8 max-w-4xl">
    <div class="space-y-6">
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/vendors')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Vendors
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Create New Vendor</h1>
          <p class="mt-2 text-gray-600">Add a new vendor to your supply chain</p>
        </div>
      </div>

      <Card class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">Vendor Information</CardTitle>
          <CardDescription>
            Enter the vendor details below
          </CardDescription>
        </CardHeader>
        <CardContent class="pt-2">
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Vendor Name</label>
                <Input
                  v-model="formData.name"
                  placeholder="Enter vendor name"
                  required
                />
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Service Type</label>
                <select 
                  v-model="formData.serviceType" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                >
                  <option value="">Select service type</option>
                  <option value="Logistics">Logistics</option>
                  <option value="Transportation">Transportation</option>
                  <option value="Warehousing">Warehousing</option>
                  <option value="Freight">Freight</option>
                  <option value="Shipping">Shipping</option>
                  <option value="Customs">Customs</option>
                </select>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Contact Email</label>
                <Input
                  v-model="formData.contactEmail"
                  type="email"
                  placeholder="Enter contact email"
                  required
                />
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Contact Phone</label>
                <Input
                  v-model="formData.contactPhone"
                  type="tel"
                  placeholder="Enter contact phone"
                  required
                />
              </div>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Address</label>
              <Input
                v-model="formData.address"
                placeholder="Enter vendor address"
                required
              />
            </div>

            <div class="flex justify-end space-x-4 pt-6">
              <Button type="button" @click="$router.push('/vendors')" variant="outline">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Plus v-if="!isSubmitting" class="mr-2 h-4 w-4" />
                <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Creating...' : 'Create Vendor' }}
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
const { createVendor } = useApi()

const isSubmitting = ref(false)

const formData = reactive({
  name: '',
  serviceType: '',
  contactEmail: '',
  contactPhone: '',
  address: ''
})

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    await createVendor(formData)
    router.push('/vendors')
  } catch (error) {
    console.error('Error creating vendor:', error)
    alert('Failed to create vendor. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}
</script>