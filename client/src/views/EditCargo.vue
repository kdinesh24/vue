<template>
  <div class="container mx-auto px-4 py-8 max-w-4xl">
    <div class="space-y-6">
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/cargo')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Cargo
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Edit Cargo</h1>
          <p class="mt-2 text-gray-600">Update cargo information</p>
        </div>
      </div>

      <div v-if="isLoading" class="flex items-center justify-center h-64">
        <Loader2 class="h-8 w-8 animate-spin" />
        <span class="ml-2">Loading cargo details...</span>
      </div>

      <Card v-else-if="!loadError" class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">Cargo Details</CardTitle>
          <CardDescription>
            Update the cargo information below
          </CardDescription>
        </CardHeader>
        <CardContent class="pt-2">
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Cargo Type</label>
                <select 
                  v-model="formData.type" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                >
                  <option value="">Select cargo type</option>
                  <option value="Electronics">Electronics</option>
                  <option value="Perishables">Perishables</option>
                  <option value="Hazardous">Hazardous</option>
                  <option value="General">General</option>
                  <option value="Clothing">Clothing</option>
                  <option value="Food">Food</option>
                  <option value="Machinery">Machinery</option>
                  <option value="Raw Materials">Raw Materials</option>
                  <option value="Pharmaceuticals">Pharmaceuticals</option>
                  <option value="Automotive">Automotive</option>
                  <option value="Textiles">Textiles</option>
                  <option value="Chemicals">Chemicals</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Weight (kg)</label>
                <Input
                  v-model.number="formData.weight"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="Enter weight in kg"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Value ($)</label>
                <Input
                  v-model.number="formData.value"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="Enter cargo value"
                  required
                />
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Assign to Shipment (Optional)</label>
                <select 
                  v-model="formData.shipmentId" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <option value="">Select shipment (optional)</option>
                  <option v-for="shipment in shipments" :key="shipment.shipmentId" :value="shipment.shipmentId">
                    #{{ shipment.shipmentId }} - {{ shipment.origin }} → {{ shipment.destination }}
                  </option>
                </select>
              </div>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Description (Optional)</label>
              <textarea
                v-model="formData.description"
                class="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                placeholder="Enter cargo description (optional)"
              ></textarea>
            </div>

            <div class="flex justify-end space-x-4 pt-6">
              <Button type="button" @click="$router.push('/cargo')" variant="outline">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Save v-if="!isSubmitting" class="mr-2 h-4 w-4" />
                <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Updating...' : 'Update Cargo' }}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <Card v-else class="border-0 shadow-sm">
        <CardContent class="text-center py-12">
          <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
          <p class="text-gray-600 mb-4">{{ loadError }}</p>
          <Button @click="$router.push('/cargo')" variant="outline">
            Back to Cargo List
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
import type { Shipment } from '@/types'

const router = useRouter()
const route = useRoute()
const { updateCargo, getCargoItem, getShipments } = useApi()

const isLoading = ref(true)
const isSubmitting = ref(false)
const loadError = ref<string | null>(null)
const shipments = ref<Shipment[]>([])
const cargoId = Number(route.params.id)

const formData = reactive({
  type: '',
  weight: 0,
  value: 0,
  volume: 0,
  weightUnit: 'kg',
  description: '',
  shipmentId: '' as string | number
})

const loadCargo = async () => {
  try {
    const cargo = await getCargoItem(cargoId)
    if (!cargo) {
      loadError.value = 'Cargo not found'
      return
    }

    // Populate form with existing data
    formData.type = cargo.type || ''
    formData.weight = cargo.weight || 0
    formData.value = cargo.value || 0
    formData.volume = cargo.volume || 0
    formData.weightUnit = cargo.weightUnit || 'kg'
    formData.description = cargo.description || ''
    formData.shipmentId = cargo.shipment?.shipmentId || ''
  } catch (error) {
    console.error('Error loading cargo:', error)
    loadError.value = 'Failed to load cargo details'
  } finally {
    isLoading.value = false
  }
}

const loadShipments = async () => {
  try {
    shipments.value = await getShipments()
  } catch (error) {
    console.error('Error loading shipments:', error)
  }
}

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    const cargoData: any = {
      type: formData.type,
      weight: formData.weight,
      value: formData.value,
      volume: formData.volume,
      weightUnit: formData.weightUnit,
      description: formData.description || null,
      shipmentId: formData.shipmentId ? Number(formData.shipmentId) : null
    }

    console.log('Updating cargo with data:', cargoData) // Debug log
    await updateCargo(cargoId, cargoData)
    router.push('/cargo')
  } catch (error) {
    console.error('Error updating cargo:', error)
    alert('Failed to update cargo. Please check the console and try again.')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadCargo(), loadShipments()])
})
</script>
