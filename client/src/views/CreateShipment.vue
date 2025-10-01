<template>
  <div class="container mx-auto px-4 py-8 max-w-4xl">
    <div class="space-y-6">
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/shipments')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Shipments
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Create New Shipment</h1>
          <p class="mt-2 text-gray-600">Add a new shipment to your supply chain</p>
        </div>
      </div>

      <Card class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">Shipment Details</CardTitle>
          <CardDescription>
            Enter the shipment information below
          </CardDescription>
        </CardHeader>
        <CardContent class="pt-2">
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Origin</label>
                <Input
                  v-model="formData.origin"
                  placeholder="Enter origin location"
                  required
                />
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Destination</label>
                <Input
                  v-model="formData.destination"
                  placeholder="Enter destination location"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Status</label>
                <select 
                  v-model="formData.status" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  required
                >
                  <option value="">Select status</option>
                  <option value="Created">Created</option>
                  <option value="Pending">Pending</option>
                  <option value="Picked Up">Picked Up</option>
                  <option value="In Transit">In Transit</option>
                  <option value="Shipped">Shipped</option>
                  <option value="Delivered">Delivered</option>
                  <option value="Cancelled">Cancelled</option>
                </select>
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Estimated Delivery</label>
                <Input
                  v-model="formData.estimatedDelivery"
                  type="date"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium">Assigned Route (Optional)</label>
                <select 
                  v-model="formData.assignedRouteId" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <option value="">Select route (optional)</option>
                  <option v-for="route in routes" :key="route.routeId" :value="route.routeId">
                    {{ route.originPort }} → {{ route.destinationPort }} ({{ route.duration }} days)
                  </option>
                </select>
              </div>
              
              <div class="space-y-2">
                <label class="text-sm font-medium">Assigned Vendor (Optional)</label>
                <select 
                  v-model="formData.assignedVendorId" 
                  class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <option value="">Select vendor (optional)</option>
                  <option v-for="vendor in vendors" :key="vendor.vendorId" :value="vendor.vendorId">
                    {{ vendor.name }} ({{ vendor.serviceType }})
                  </option>
                </select>
              </div>
            </div>

            <div class="flex justify-end space-x-4 pt-6">
              <Button type="button" @click="$router.push('/shipments')" variant="outline">
                Cancel
              </Button>
              <Button type="submit" :disabled="isSubmitting">
                <Plus v-if="!isSubmitting" class="mr-2 h-4 w-4" />
                <Loader2 v-else class="mr-2 h-4 w-4 animate-spin" />
                {{ isSubmitting ? 'Creating...' : 'Create Shipment' }}
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
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { ArrowLeft, Plus, Loader2 } from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Route, Vendor } from '@/types'

const router = useRouter()
const { createShipment, getRoutes, getVendors } = useApi()

const isSubmitting = ref(false)
const routes = ref<Route[]>([])
const vendors = ref<Vendor[]>([])

const formData = reactive({
  origin: '',
  destination: '',
  status: '',
  estimatedDelivery: '',
  assignedRouteId: '',
  assignedVendorId: ''
})

const loadData = async () => {
  try {
    const [routesData, vendorsData] = await Promise.all([
      getRoutes(),
      getVendors()
    ])
    routes.value = routesData
    vendors.value = vendorsData
  } catch (error) {
    console.error('Error loading data:', error)
  }
}

const handleSubmit = async () => {
  isSubmitting.value = true
  try {
    const shipmentData: any = {
      origin: formData.origin,
      destination: formData.destination,
      status: formData.status,
      estimatedDelivery: formData.estimatedDelivery
    }

    // Add assigned route if selected
    if (formData.assignedRouteId) {
      const selectedRoute = routes.value.find(r => r.routeId === Number(formData.assignedRouteId))
      if (selectedRoute) {
        shipmentData.assignedRoute = selectedRoute
      }
    }

    // Add assigned vendor if selected
    if (formData.assignedVendorId) {
      const selectedVendor = vendors.value.find(v => v.vendorId === Number(formData.assignedVendorId))
      if (selectedVendor) {
        shipmentData.assignedVendor = selectedVendor
      }
    }

    await createShipment(shipmentData)
    router.push('/shipments')
  } catch (error) {
    console.error('Error creating shipment:', error)
    alert('Failed to create shipment. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>