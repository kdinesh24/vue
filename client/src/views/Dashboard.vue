<template>
  <div class="flex-1 space-y-6">
    <!-- Header Section -->
    <div class="rounded-lg border bg-card text-card-foreground shadow-sm p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-semibold tracking-tight">Good morning 👋</h1>
          <p class="text-sm text-muted-foreground mt-1">Here's what's happening with your supply chain today</p>
        </div>
        <div class="flex items-center space-x-4"></div>
      </div>
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

    <!-- Supply Chain Overview Stats -->
    <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-5">
      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-blue-100 rounded-full">
              <Package class="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <p class="text-sm font-medium text-muted-foreground">Total Shipments</p>
              <p class="text-3xl font-bold">{{ stats.shipments }}</p>
              <p class="text-xs text-emerald-600 mt-1">↗️ Active operations</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-green-100 rounded-full">
              <Users class="h-6 w-6 text-green-600" />
            </div>
            <div>
              <p class="text-sm font-medium text-muted-foreground">Active Vendors</p>
              <p class="text-3xl font-bold">{{ stats.vendors }}</p>
              <p class="text-xs text-emerald-600 mt-1">✓ Partnerships</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-yellow-100 rounded-full">
              <Route class="h-6 w-6 text-yellow-600" />
            </div>
            <div>
              <p class="text-sm font-medium text-muted-foreground">Active Routes</p>
              <p class="text-3xl font-bold">{{ stats.routes }}</p>
              <p class="text-xs text-blue-600 mt-1">🚚 In service</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-purple-100 rounded-full">
              <Truck class="h-6 w-6 text-purple-600" />
            </div>
            <div>
              <p class="text-sm font-medium text-muted-foreground">Total Cargo</p>
              <p class="text-3xl font-bold">{{ stats.cargo }}</p>
              <p class="text-xs text-purple-600 mt-1">📦 Items tracked</p>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="flex items-center space-x-4">
            <div class="p-3 bg-emerald-100 rounded-full">
              <CheckCircle class="h-6 w-6 text-emerald-600" />
            </div>
            <div>
              <p class="text-sm font-medium text-muted-foreground">Completed Deliveries</p>
              <p class="text-3xl font-bold">{{ stats.deliveries }}</p>
              <p class="text-xs text-emerald-600 mt-1">✅ Successful</p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Quick Actions -->
    <Card class="hover:shadow-md transition-shadow">
      <CardContent class="p-6">
        <div class="space-y-4">
          <div>
            <h3 class="text-lg font-semibold">Quick Actions</h3>
            <p class="text-sm text-muted-foreground">Frequently used actions to manage your supply chain</p>
          </div>
          
          <div class="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4">
            <Button @click="$router.push('/shipments/create')" class="h-11 shadow-sm">
              <Plus class="mr-2 h-4 w-4" />
              New Shipment
            </Button>
            
            <Button @click="$router.push('/vendors/create')" class="h-11 shadow-sm">
              <Plus class="mr-2 h-4 w-4" />
              Add Vendor
            </Button>
            
            <Button @click="$router.push('/routes/create')" class="h-11 shadow-sm">
              <Plus class="mr-2 h-4 w-4" />
              Add Route
            </Button>
            
            <Button @click="$router.push('/cargo/create')" class="h-11 shadow-sm">
              <Plus class="mr-2 h-4 w-4" />
              Add Cargo
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Recent Activity Grid -->
    <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
      <!-- Recent Shipments -->
      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="space-y-4">
            <div>
              <h3 class="text-lg font-semibold">Recent Shipments</h3>
              <p class="text-sm text-muted-foreground">Latest shipment updates and status</p>
            </div>
            
            <div v-if="isLoading" class="flex items-center justify-center py-12">
              <div class="flex items-center space-x-2">
                <Loader2 class="h-4 w-4 animate-spin" />
                <span class="text-sm text-muted-foreground">Loading shipments...</span>
              </div>
            </div>
            
            <div v-else-if="recentShipments.length === 0" class="text-center py-8">
              <Package class="mx-auto h-12 w-12 text-muted-foreground mb-4" />
              <h3 class="text-sm font-semibold">No shipments found</h3>
              <p class="text-xs text-muted-foreground mb-4">
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
                class="flex items-center justify-between p-3 border rounded-lg hover:bg-muted/50 transition-colors cursor-pointer"
                @click="$router.push(`/shipments/${shipment.shipmentId}`)"
              >
                <div class="flex items-center space-x-3">
                  <div class="p-2 bg-blue-100 rounded-full">
                    <Package class="h-4 w-4 text-blue-600" />
                  </div>
                  <div>
                    <p class="text-sm font-medium">#{{ shipment.shipmentId }}</p>
                    <p class="text-xs text-muted-foreground">{{ shipment.origin }} → {{ shipment.destination }}</p>
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
                  <ArrowRight class="ml-2 h-3 w-3" />
                </Button>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Recent Routes -->
      <Card class="hover:shadow-md transition-shadow">
        <CardContent class="p-6">
          <div class="space-y-4">
            <div>
              <h3 class="text-lg font-semibold">Recent Routes</h3>
              <p class="text-sm text-muted-foreground">Active and recently created routes</p>
            </div>
            
            <div v-if="isLoading" class="flex items-center justify-center py-12">
              <div class="flex items-center space-x-2">
                <Loader2 class="h-4 w-4 animate-spin" />
                <span class="text-sm text-muted-foreground">Loading routes...</span>
              </div>
            </div>
            
            <div v-else-if="recentRoutes.length === 0" class="text-center py-8">
              <Route class="mx-auto h-12 w-12 text-muted-foreground mb-4" />
              <h3 class="text-sm font-semibold">No routes found</h3>
              <p class="text-xs text-muted-foreground mb-4">
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
                class="flex items-center justify-between p-3 border rounded-lg hover:bg-muted/50 transition-colors cursor-pointer"
                @click="$router.push(`/routes/${route.routeId}`)"
              >
                <div class="flex items-center space-x-3">
                  <div class="p-2 bg-yellow-100 rounded-full">
                    <Route class="h-4 w-4 text-yellow-600" />
                  </div>
                  <div>
                    <p class="text-sm font-medium">#{{ route.routeId }}</p>
                    <p class="text-xs text-muted-foreground">{{ route.originPort }} → {{ route.destinationPort }}</p>
                    <p class="text-xs text-muted-foreground">{{ route.duration }} days</p>
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
                  <ArrowRight class="ml-2 h-3 w-3" />
                </Button>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- System Status section removed for cleaner UI -->
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, onActivated } from 'vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { 
  Package, 
  Users, 
  Route, 
  Truck, 
  CheckCircle,
  Plus,
  ArrowRight,
  Loader2
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

// Status variant helpers
const getStatusVariant = (status: string) => {
  switch (status?.toLowerCase()) {
    case 'pending': return 'secondary'
    case 'in_transit': return 'default'
    case 'delivered': return 'default'
    case 'cancelled': return 'destructive'
    default: return 'secondary'
  }
}

const getRouteStatusVariant = (status: string) => {
  switch (status?.toLowerCase()) {
    case 'active': return 'default'
    case 'inactive': return 'secondary'
    case 'scheduled': return 'secondary'
    default: return 'secondary'
  }
}

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
  
  // Listen for real-time updates from WebSocket
  window.addEventListener('shipments-updated', refresh)
  window.addEventListener('deliveries-updated', refresh)
  window.addEventListener('routes-updated', refresh)
  window.addEventListener('cargo-updated', refresh)
})

// Refresh data when component is activated (navigating back to this page)
onActivated(() => {
  loadDashboardData()
})

onUnmounted(() => {
  // Cleanup event listener
  window.removeEventListener('focus', loadDashboardData)
  window.removeEventListener('shipment-updated', refresh)
  window.removeEventListener('shipments-updated', refresh)
  window.removeEventListener('deliveries-updated', refresh)
  window.removeEventListener('routes-updated', refresh)
  window.removeEventListener('cargo-updated', refresh)
})
</script>