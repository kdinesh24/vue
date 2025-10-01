<template>
  <div class="container mx-auto px-4 py-8 max-w-7xl">
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Shipments</h1>
          <p class="mt-2 text-gray-600">Manage and track your shipments</p>
        </div>
        <Button @click="$router.push('/shipments/create')">
          <Plus class="mr-2 h-4 w-4" />
          Create Shipment
        </Button>
      </div>

      <Card class="border-0 shadow-sm">
        <CardHeader class="pb-4">
          <CardTitle class="text-xl font-semibold">All Shipments</CardTitle>
          <CardDescription>
            View and manage your shipment records
          </CardDescription>
        </CardHeader>
        <CardContent class="pt-2">
          <div v-if="isLoading" class="text-center py-12">
            <Loader2 class="h-6 w-6 animate-spin mx-auto" />
            <p class="mt-2 text-sm text-gray-500">Loading shipments...</p>
          </div>
          
          <div v-else-if="!shipments || shipments.length === 0" class="text-center py-12 text-gray-500">
            <Truck class="mx-auto h-16 w-16 text-gray-300 mb-4" />
            <h3 class="text-lg font-medium text-gray-900 mb-2">No shipments found</h3>
            <p class="text-sm text-gray-500 mb-4">Create your first shipment to get started.</p>
            <Button @click="$router.push('/shipments/create')">
              <Plus class="mr-2 h-4 w-4" />
              Create Shipment
            </Button>
          </div>
          
          <div v-else class="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead class="w-[100px]">ID</TableHead>
                  <TableHead>Origin → Destination</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Estimated Delivery</TableHead>
                  <TableHead>Route</TableHead>
                  <TableHead>Vendor</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-for="shipment in shipments" :key="shipment.shipmentId">
                  <TableCell class="font-medium">#{{ shipment.shipmentId }}</TableCell>
                  <TableCell>
                    <div class="flex flex-col">
                      <span class="font-medium">{{ shipment.origin }} → {{ shipment.destination }}</span>
                      <span v-if="shipment.cargoItems && shipment.cargoItems.length > 0" class="text-xs text-gray-500">
                        {{ shipment.cargoItems.length }} cargo item(s)
                      </span>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge :variant="getStatusVariant(shipment.status)" class="text-xs">
                      {{ shipment.status }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ formatDate(shipment.estimatedDelivery) }}</TableCell>
                  <TableCell>
                    <span v-if="shipment.assignedRoute">
                      {{ shipment.assignedRoute.originPort }} → {{ shipment.assignedRoute.destinationPort }}
                    </span>
                    <span v-else class="text-gray-400">No route assigned</span>
                  </TableCell>
                  <TableCell>
                    <span v-if="shipment.assignedVendor">
                      {{ shipment.assignedVendor.name }}
                    </span>
                    <span v-else class="text-gray-400">No vendor assigned</span>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex justify-end space-x-2">
                      <Button 
                        @click="$router.push(`/shipments/${shipment.shipmentId}`)"
                        variant="outline" 
                        size="sm"
                      >
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button 
                        @click="handleEdit(shipment.shipmentId!)"
                        variant="outline" 
                        size="sm"
                      >
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button 
                        @click="handleDelete(shipment.shipmentId!)"
                        variant="destructive" 
                        size="sm"
                      >
                        <Trash2 class="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onActivated, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Plus, Edit, Trash2, Truck, Loader2, Eye } from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Shipment } from '@/types'

const shipments = ref<Shipment[]>([])
const isLoading = ref(false)

const router = useRouter()
const { getShipments, deleteShipment } = useApi()

const loadShipments = async () => {
  isLoading.value = true
  try {
    shipments.value = await getShipments()
  } catch (error) {
    console.error('Error loading shipments:', error)
  } finally {
    isLoading.value = false
  }
}

// Refresh function to be called from anywhere
const refresh = () => {
  loadShipments()
}

const handleEdit = (id: number) => {
  router.push(`/shipments/${id}/edit`)
}

const handleDelete = async (id: number) => {
  if (confirm('Are you sure you want to delete this shipment?')) {
    try {
      await deleteShipment(id)
      await refresh() // Use refresh instead of loadShipments
    } catch (error) {
      console.error('Error deleting shipment:', error)
      alert('Failed to delete shipment')
    }
  }
}

const getStatusVariant = (status: string): "default" | "destructive" | "outline" | "secondary" => {
  const statusVariants: Record<string, "default" | "destructive" | "outline" | "secondary"> = {
    'Shipped': 'default',
    'In Transit': 'secondary', 
    'Delivered': 'default',
    'Cancelled': 'destructive',
    'Pending': 'outline',
    'Created': 'outline',
    'Picked Up': 'secondary'
  }
  return statusVariants[status] || 'secondary'
}

const formatDate = (dateString: string | Date) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

onMounted(() => {
  loadShipments()
  
  // Listen for shipment updates from other components
  window.addEventListener('shipment-updated', refresh)
})

// Refresh data when component is activated (navigating back to this page)
onActivated(() => {
  loadShipments()
})

// Cleanup event listeners
onUnmounted(() => {
  window.removeEventListener('shipment-updated', refresh)
})
</script>