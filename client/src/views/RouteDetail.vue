<template>
  <div class="container mx-auto px-4 py-8 max-w-6xl">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center space-x-4">
        <Button @click="$router.push('/routes')" variant="outline" size="sm">
          <ArrowLeft class="mr-2 h-4 w-4" />
          Back to Routes
        </Button>
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Route Details</h1>
          <p class="mt-2 text-gray-600">Detailed information about the selected route</p>
        </div>
      </div>

      <div v-if="isLoading" class="flex items-center justify-center h-32">
        <Loader2 class="h-6 w-6 animate-spin" />
        <span class="ml-2">Loading route details...</span>
      </div>
      
      <div v-else-if="error" class="text-center py-8">
        <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
        <p class="text-gray-600">{{ error }}</p>
        <Button @click="loadRoute" class="mt-4" variant="outline">
          Try Again
        </Button>
      </div>

      <div v-else-if="route" class="space-y-6">
        <!-- Route Information Card -->
        <Card>
          <CardHeader>
            <div class="flex items-center justify-between">
              <div>
                <CardTitle class="text-xl">{{ route.originPort }} → {{ route.destinationPort }}</CardTitle>
                <CardDescription>Route ID: {{ route.routeId }}</CardDescription>
              </div>
              <Badge :variant="getModeBadgeVariant(route.transportationMode)" class="text-sm">
                {{ route.transportationMode }}
              </Badge>
            </div>
          </CardHeader>
          <CardContent>
            <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
              <div class="text-center">
                <div class="text-2xl font-bold text-blue-600">{{ formatNumber(route.distance) }} km</div>
                <p class="text-sm text-muted-foreground">Distance</p>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-green-600">{{ route.duration }} days</div>
                <p class="text-sm text-muted-foreground">Duration</p>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-yellow-600">${{ formatNumber(route.cost) }}</div>
                <p class="text-sm text-muted-foreground">Cost</p>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-purple-600">{{ assignedShipments.length }}</div>
                <p class="text-sm text-muted-foreground">Assigned Shipments</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Assigned Shipments -->
        <Card>
          <CardHeader>
            <CardTitle>Assigned Shipments</CardTitle>
            <CardDescription>
              Shipments currently using this route
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div v-if="assignedShipments.length === 0" class="text-center py-8">
              <Package class="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <p class="text-gray-600">No shipments assigned to this route</p>
            </div>
            <div v-else>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Shipment ID</TableHead>
                    <TableHead>Origin</TableHead>
                    <TableHead>Destination</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Estimated Delivery</TableHead>
                    <TableHead class="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  <TableRow v-for="shipment in assignedShipments" :key="shipment.shipmentId">
                    <TableCell class="font-medium">#{{ shipment.shipmentId }}</TableCell>
                    <TableCell>{{ shipment.origin }}</TableCell>
                    <TableCell>{{ shipment.destination }}</TableCell>
                    <TableCell>
                      <Badge :variant="getStatusBadgeVariant(shipment.status)">
                        {{ shipment.status }}
                      </Badge>
                    </TableCell>
                    <TableCell>{{ formatDate(shipment.estimatedDelivery) }}</TableCell>
                    <TableCell class="text-right">
                      <Button @click="viewShipment(shipment)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>

        <!-- Route Actions -->
        <Card>
          <CardHeader>
            <CardTitle>Route Actions</CardTitle>
            <CardDescription>
              Manage this route
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div class="flex space-x-4">
              <Button @click="editRoute" variant="outline">
                <Edit class="mr-2 h-4 w-4" />
                Edit Route
              </Button>
              <Button @click="deleteRoute" variant="outline" class="text-red-600 hover:text-red-700">
                <Trash2 class="mr-2 h-4 w-4" />
                Delete Route
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { 
  ArrowLeft, 
  Loader2, 
  AlertCircle, 
  Package,
  Eye,
  Edit, 
  Trash2 
} from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Route as RouteType, Shipment } from '@/types'

const router = useRouter()
const route = useRoute()
const { getRoute, getShipments, deleteRoute: apiDeleteRoute } = useApi()

const routeData = ref<RouteType | null>(null)
const shipments = ref<Shipment[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const assignedShipments = computed(() => {
  return shipments.value.filter(shipment => 
    shipment.assignedRoute?.routeId === routeData.value?.routeId
  )
})

const loadRoute = async () => {
  const routeId = Number(route.params.id)
  if (!routeId) {
    error.value = 'Invalid route ID'
    return
  }

  isLoading.value = true
  error.value = null
  try {
    const [routeResult, shipmentsResult] = await Promise.all([
      getRoute(routeId),
      getShipments()
    ])
    routeData.value = routeResult
    shipments.value = shipmentsResult
  } catch (err) {
    error.value = 'Failed to load route details'
    console.error('Error loading route:', err)
  } finally {
    isLoading.value = false
  }
}

const getModeBadgeVariant = (mode: string) => {
  switch (mode) {
    case 'Sea':
      return 'default'
    case 'Air':
      return 'secondary'
    case 'Land':
      return 'outline'
    case 'Rail':
      return 'destructive'
    default:
      return 'outline'
  }
}

const getStatusBadgeVariant = (status: string) => {
  switch (status) {
    case 'Delivered':
      return 'default'
    case 'In Transit':
    case 'Shipped':
      return 'secondary'
    case 'Pending':
    case 'Created':
      return 'outline'
    case 'Cancelled':
      return 'destructive'
    default:
      return 'outline'
  }
}

const formatNumber = (num: number) => {
  return new Intl.NumberFormat().format(num)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const viewShipment = (shipment: Shipment) => {
  router.push(`/shipments/${shipment.shipmentId}`)
}

const editRoute = () => {
  // Navigate to edit route page (when implemented)
  console.log('Edit route:', routeData.value)
}

const deleteRoute = async () => {
  if (!routeData.value) return
  
  if (!confirm(`Are you sure you want to delete route "${routeData.value.originPort} → ${routeData.value.destinationPort}"?`)) {
    return
  }
  
  try {
    await apiDeleteRoute(routeData.value.routeId)
    router.push('/routes')
  } catch (error) {
    console.error('Error deleting route:', error)
    alert('Failed to delete route. Please try again.')
  }
}

onMounted(() => {
  loadRoute()
})
</script>