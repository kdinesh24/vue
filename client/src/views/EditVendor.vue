<template>
  <div class="container mx-auto px-4 py-8 max-w-4xl">
    <div class="space-y-6">
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/vendors')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Vendors
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Edit Vendor</h1>
          <p class="mt-2 text-gray-600">Update vendor information</p>
        </div>
      </div>

      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading vendor...</span>
      </div>

      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600">{{ error }}</p>
        <Button @click="$router.push('/vendors')" class="mt-4" variant="outline">
          Back to Vendors
        </Button>
      </div>

      <Card v-else class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">Vendor Information</CardTitle>
          <CardDescription>
            Update the vendor details below
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

            <div class="space-y-2">
              <label class="text-sm font-medium">Contact Information</label>
              <Input
                v-model="formData.contactInfo"
                placeholder="Enter contact information (email, phone, address)"
                required
              />
              <p class="text-xs text-gray-500">Enter all contact details (email, phone, address)</p>
            </div>

            <div class="flex items-center space-x-2">
              <input
                type="checkbox"
                v-model="formData.isActive"
                id="isActive"
                class="h-4 w-4 rounded border-gray-300"
              />
              <label for="isActive" class="text-sm font-medium">
                Active Vendor
              </label>
            </div>

            <div class="flex justify-end space-x-4 pt-6">
              <Button type="button" @click="$router.push('/vendors')" variant="outline">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Save v-if="!isSubmitting" class="mr-2 h-4 w-4" />
                <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Updating...' : 'Update Vendor' }}
              </Button>
            </div>
          </form>
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
const { getVendor, updateVendor } = useApi()

const vendorId = Number(route.params.id)
const isLoading = ref(true)
const isSubmitting = ref(false)
const error = ref<string | null>(null)

const formData = reactive({
  name: '',
  serviceType: '',
  contactInfo: '',
  isActive: true
})

const loadVendor = async () => {
  isLoading.value = true
  error.value = null
  try {
    const vendor = await getVendor(vendorId)
    formData.name = vendor.name
    formData.serviceType = vendor.serviceType
    formData.contactInfo = vendor.contactInfo
    formData.isActive = vendor.isActive !== false
  } catch (err) {
    error.value = 'Failed to load vendor. Please try again.'
    console.error('Error loading vendor:', err)
  } finally {
    isLoading.value = false
  }
}

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    const vendorData = {
      name: formData.name,
      serviceType: formData.serviceType,
      contactInfo: formData.contactInfo,
      isActive: formData.isActive
    }
    
    await updateVendor(vendorId, vendorData)
    router.push('/vendors')
  } catch (error) {
    console.error('Error updating vendor:', error)
    alert('Failed to update vendor. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  loadVendor()
})
</script>
