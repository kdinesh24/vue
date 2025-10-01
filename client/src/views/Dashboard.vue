<template>
  <div class="min-h-screen bg-gray-50/30">
    <div class="container mx-auto px-4 py-8 max-w-7xl">
      <div class="space-y-8">
        <!-- Page Header -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p class="text-gray-600 mt-2">
            Overview of your supply chain operations
          </p>
        </div>

        <!-- Connection Status Alert -->
        <div v-if="connectionError" class="mb-6">
          <Card class="border-red-200 bg-red-50">
            <CardContent class="p-4">
              <div class="flex items-start space-x-3">
                <div class="h-2 w-2 bg-red-500 rounded-full mt-2"></div>
                <div>
                  <h4 class="text-sm font-medium text-red-800">Backend Connection Failed</h4>
                  <p class="text-sm text-red-700 mt-1">
                    Unable to connect to API server at {{ apiBaseUrl }}. 
                    Please check if your backend server is running.
                  </p>
                  <Button 
                    @click="retryConnection" 
                    variant="outline" 
                    size="sm" 
                    class="mt-2 border-red-300 text-red-800 hover:bg-red-100"
                  >
                    Retry Connection
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Stats Cards -->
        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-5">
          <Card class="border-0 shadow-sm">
            <CardContent class="p-6">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-blue-100 rounded-full">
                  <Package class="h-6 w-6 text-blue-600" />
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Total Shipments</p>
                  <p class="text-3xl font-bold">{{ stats.shipments }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="border-0 shadow-sm">
            <CardContent class="p-6">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-green-100 rounded-full">
                  <Users class="h-6 w-6 text-green-600" />
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Active Vendors</p>
                  <p class="text-3xl font-bold">{{ stats.vendors }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="border-0 shadow-sm">
            <CardContent class="p-6">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-yellow-100 rounded-full">
                  <Route class="h-6 w-6 text-yellow-600" />
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Active Routes</p>
                  <p class="text-3xl font-bold">{{ stats.routes }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="border-0 shadow-sm">
            <CardContent class="p-6">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-purple-100 rounded-full">
                  <Truck class="h-6 w-6 text-purple-600" />
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Total Cargo</p>
                  <p class="text-3xl font-bold">{{ stats.cargo }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card class="border-0 shadow-sm">
            <CardContent class="p-6">
              <div class="flex items-center space-x-4">
                <div class="p-3 bg-emerald-100 rounded-full">
                  <CheckCircle class="h-6 w-6 text-emerald-600" />
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-500">Completed Deliveries</p>
                  <p class="text-3xl font-bold">{{ stats.deliveries }}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Quick Actions -->
        <Card class="border-0 shadow-sm">
          <CardHeader class="pb-4">
            <CardTitle class="text-xl font-semibold">Quick Actions</CardTitle>
            <CardDescription>
              Frequently used actions to manage your supply chain
            </CardDescription>
          </CardHeader>
          <CardContent class="pt-2">
            <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <Button @click="$router.push('/shipments/create')" class="h-12 shadow-sm">
                <Plus class="mr-2 h-4 w-4" />
                New Shipment
              </Button>
              
              <Button @click="$router.push('/vendors/create')" variant="outline" class="h-12 shadow-sm">
                <Plus class="mr-2 h-4 w-4" />
                Add Vendor
              </Button>
              
              <Button @click="$router.push('/routes/create')" variant="secondary" class="h-12 shadow-sm">
                <Plus class="mr-2 h-4 w-4" />
                Add Route
              </Button>
              
              <Button @click="$router.push('/cargo/create')" variant="ghost" class="h-12 border shadow-sm">
                <Plus class="mr-2 h-4 w-4" />
                Add Cargo
              </Button>
            </div>

            <!-- View All Actions Row -->
            <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5 mt-4">
              <Button @click="$router.push('/shipments')" variant="outline" class="h-10 shadow-sm">
                <Eye class="mr-2 h-4 w-4" />
                View Shipments
              </Button>
              
              <Button @click="$router.push('/vendors')" variant="outline" class="h-10 shadow-sm">
                <Eye class="mr-2 h-4 w-4" />
                View Vendors
              </Button>
              
              <Button @click="$router.push('/routes')" variant="outline" class="h-10 shadow-sm">
                <Eye class="mr-2 h-4 w-4" />
                View Routes
              </Button>
              
              <Button @click="$router.push('/cargo')" variant="outline" class="h-10 shadow-sm">
                <Eye class="mr-2 h-4 w-4" />
                View Cargo
              </Button>
              
              <Button @click="$router.push('/deliveries')" variant="outline" class="h-10 shadow-sm">
                <CheckCircle class="mr-2 h-4 w-4" />
                View Deliveries
              </Button>
            </div>
          </CardContent>
        </Card>

        <!-- Recent Activity Grid -->
        <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
          
          <!-- Recent Shipments -->
          <Card class="border-0 shadow-sm">
            <CardHeader class="pb-4">
              <CardTitle class="text-xl font-semibold">Recent Shipments</CardTitle>
              <CardDescription>
                Latest shipment updates and status
              </CardDescription>
            </CardHeader>
            <CardContent class="pt-2">
              <div v-if="isLoading" class="flex items-center justify-center py-12">
                <div class="flex items-center space-x-2">
                  <Loader2 class="h-4 w-4 animate-spin" />
                  <span class="text-sm text-gray-500">Loading shipments...</span>
                </div>
              </div>
              
              <div v-else-if="recentShipments.length === 0" class="text-center py-8">
                <Package class="mx-auto h-12 w-12 text-gray-300 mb-4" />
                <h3 class="text-sm font-semibold">No shipments found</h3>
                <p class="text-xs text-gray-500 mb-4">
                  {{ connectionError ? 'Unable to load shipments.' : 'Create your first shipment.' }}
                </p>
                <Button v-if="!connectionError" @click="$router.push('/shipments/create')" size="sm" class="mt-3">
                  <Plus class="mr-2 h-3 w-3" />
                  Create Shipment
                </Button>
              </div>
              
              <div v-else class="space-y-3">
                <div 
                  v-for="shipment in recentShipments" 
                  :key="shipment.shipmentId"
                  class="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
                  @click="$router.push(`/shipments/${shipment.shipmentId}`)"
                >
                  <div class="flex items-center space-x-3">
                    <div class="p-2 bg-blue-100 rounded-full">
                      <Package class="h-4 w-4 text-blue-600" />
                    </div>
                    <div>
                      <p class="text-sm font-medium">#{{ shipment.shipmentId }}</p>
                      <p class="text-xs text-gray-500">{{ shipment.origin }} → {{ shipment.destination }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <Badge :variant="getStatusVariant(shipment.status)" class="text-xs">
                      {{ shipment.status }}
                    </Badge>
                  </div>
                </div>
                
                <div class="pt-2 border-t">
                  <Button @click="$router.push('/shipments')" variant="ghost" size="sm" class="w-full">
                    View All Shipments
                    <Eye class="ml-2 h-3 w-3" />
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Recent Routes -->
          <Card class="border-0 shadow-sm">
            <CardHeader class="pb-4">
              <CardTitle class="text-xl font-semibold">Recent Routes</CardTitle>
              <CardDescription>
                Active and recently created routes
              </CardDescription>
            </CardHeader>
            <CardContent class="pt-2">
              <div v-if="isLoading" class="flex items-center justify-center py-12">
                <div class="flex items-center space-x-2">
                  <Loader2 class="h-4 w-4 animate-spin" />
                  <span class="text-sm text-gray-500">Loading routes...</span>
                </div>
              </div>
              
              <div v-else-if="recentRoutes.length === 0" class="text-center py-8">
                <Route class="mx-auto h-12 w-12 text-gray-300 mb-4" />
                <h3 class="text-sm font-semibold">No routes found</h3>
                <p class="text-xs text-gray-500 mb-4">
                  {{ connectionError ? 'Unable to load routes.' : 'Create your first route.' }}
                </p>
                <Button v-if="!connectionError" @click="$router.push('/routes/create')" size="sm" class="mt-3">
                  <Plus class="mr-2 h-3 w-3" />
                  Create Route
                </Button>
              </div>
              
              <div v-else class="space-y-3">
                <div 
                  v-for="route in recentRoutes" 
                  :key="route.routeId"
                  class="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
                  @click="$router.push(`/routes/${route.routeId}`)"
                >
                  <div class="flex items-center space-x-3">
                    <div class="p-2 bg-yellow-100 rounded-full">
                      <Route class="h-4 w-4 text-yellow-600" />
                    </div>
                    <div>
                      <p class="text-sm font-medium">#{{ route.routeId }}</p>
                      <p class="text-xs text-gray-500">{{ route.originPort }} → {{ route.destinationPort }}</p>
                      <p class="text-xs text-gray-500">{{ route.duration }} days</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <Badge :variant="getRouteStatusVariant(route.status)" class="text-xs">
                      {{ route.status }}
                    </Badge>
                  </div>
                </div>
                
                <div class="pt-2 border-t">
                  <Button @click="$router.push('/routes')" variant="ghost" size="sm" class="w-full">
                    View All Routes
                    <Eye class="ml-2 h-3 w-3" />
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- System Status -->
        <Card class="border-0 shadow-sm">
          <CardHeader class="pb-4">
            <CardTitle class="text-xl font-semibold">System Status</CardTitle>
            <CardDescription>
              Current system health and connectivity
            </CardDescription>
          </CardHeader>
          <CardContent class="pt-2">
            <div v-if="connectionError" class="flex items-center justify-between p-4 bg-red-50 rounded-lg border border-red-200">
              <div class="flex items-center space-x-3">
                <div class="h-2 w-2 bg-red-500 rounded-full"></div>
                <span class="text-sm font-medium text-red-800">API Server Disconnected</span>
              </div>
              <Badge variant="destructive">Offline</Badge>
            </div>
            <div v-else class="flex items-center justify-between p-4 bg-green-50 rounded-lg border border-green-200">
              <div class="flex items-center space-x-3">
                <div class="h-2 w-2 bg-green-500 rounded-full"></div>
                <span class="text-sm font-medium text-green-800">All Systems Operational</span>
              </div>
              <Badge variant="secondary">API Connected</Badge>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, onActivated } from 'vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { 
  Package, 
  Users, 
  Route, 
  Truck, 
  Plus, 
  Eye, 
  Loader2,
  CheckCircle
} from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Shipment, Route as RouteType } from '@/types'

const apiBaseUrl = 'http://localhost:8081/api'

const stats = reactive({
  shipments: 0,
  vendors: 0,
  routes: 0,
  cargo: 0,
  deliveries: 0
})

const connectionError = ref(false)
const isLoading = ref(false)
const recentShipments = ref<Shipment[]>([])
const recentRoutes = ref<RouteType[]>([])

// Load all data and update stats
const loadDashboardData = async () => {
  isLoading.value = true
  try {
    const { getShipments, getVendors, getCargo, getRoutes, getDeliveries } = useApi()
    
    const [shipments, vendors, cargo, routes, deliveries] = await Promise.all([
      getShipments(),
      getVendors(),
      getCargo(),
      getRoutes(),
      getDeliveries()
    ])
    
    // Update stats
    stats.shipments = shipments.length
    stats.vendors = vendors.length
    stats.cargo = cargo.length
    stats.routes = routes.length
    stats.deliveries = deliveries.length
    
    // Set recent items
    recentShipments.value = shipments.slice(0, 3)
    recentRoutes.value = routes.slice(0, 3)
    
    connectionError.value = false
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
    connectionError.value = true
  } finally {
    isLoading.value = false
  }
}

// Refresh function to be called from anywhere
const refresh = () => {
  loadDashboardData()
}

const retryConnection = async () => {
  connectionError.value = false
  await loadDashboardData()
}

onMounted(() => {
  loadDashboardData()
  
  // Add window focus listener to refresh data when returning to tab
  window.addEventListener('focus', loadDashboardData)
  
  // Listen for shipment updates from other components
  window.addEventListener('shipment-updated', refresh)
})

// Refresh data when component is activated (navigating back to this page)
onActivated(() => {
  loadDashboardData()
})

onUnmounted(() => {
  // Cleanup event listener
  window.removeEventListener('focus', loadDashboardData)
  window.removeEventListener('shipment-updated', refresh)
})

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

const getRouteStatusVariant = (status: string): "default" | "destructive" | "outline" | "secondary" => {
  const statusVariants: Record<string, "default" | "destructive" | "outline" | "secondary"> = {
    'Active': 'default',
    'Inactive': 'secondary',
    'Delayed': 'destructive',
    'Closed': 'outline'
  }
  return statusVariants[status] || 'secondary'
}
</script>