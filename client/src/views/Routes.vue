<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Routes</h1>
          <p class="mt-2 text-gray-600">Manage your shipping routes</p>
        </div>
        <Button @click="$router.push('/routes/create')">
          <Plus class="mr-2 h-4 w-4" />
          Add Route
        </Button>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-blue-600">{{ stats.total }}</div>
              <Route class="ml-auto h-8 w-8 text-blue-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Total Routes</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-green-600">{{ stats.avgDuration }}</div>
              <Clock class="ml-auto h-8 w-8 text-green-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Avg Duration (days)</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-yellow-600">${{ stats.avgCost }}</div>
              <DollarSign class="ml-auto h-8 w-8 text-yellow-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Avg Cost</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-purple-600">{{ stats.seaRoutes }}</div>
              <Ship class="ml-auto h-8 w-8 text-purple-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Sea Routes</p>
          </CardContent>
        </Card>
      </div>

      <!-- Routes Table -->
      <Card>
        <CardHeader>
          <CardTitle>All Routes</CardTitle>
          <CardDescription>
            A comprehensive list of all shipping routes
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">Loading routes...</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadRoutes" class="mt-4" variant="outline">
              Try Again
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Origin Port</TableHead>
                  <TableHead>Destination Port</TableHead>
                  <TableHead>Distance (km)</TableHead>
                  <TableHead>Duration (days)</TableHead>
                  <TableHead>Mode</TableHead>
                  <TableHead>Cost ($)</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="routes.length === 0">
                  <TableCell :colspan="8" class="h-24 text-center">
                    No routes found.
                  </TableCell>
                </TableRow>
                <TableRow v-for="route in routes" :key="route.routeId">
                  <TableCell class="font-medium">{{ route.routeId }}</TableCell>
                  <TableCell>{{ route.originPort }}</TableCell>
                  <TableCell>{{ route.destinationPort }}</TableCell>
                  <TableCell>{{ formatNumber(route.distance) }}</TableCell>
                  <TableCell>{{ route.duration }}</TableCell>
                  <TableCell>
                    <Badge :variant="getModeBadgeVariant(route.transportationMode)">
                      {{ route.transportationMode }}
                    </Badge>
                  </TableCell>
                  <TableCell>${{ formatNumber(route.cost) }}</TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="viewRoute(route)" size="sm" variant="outline">
                        <Eye class="h-4 w-4" />
                      </Button>
                      <Button @click="editRoute(route)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="deleteRoute(route)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { 
  Plus, 
  Route, 
  Clock, 
  DollarSign, 
  Ship, 
  Loader2, 
  AlertCircle, 
  Eye,
  Edit, 
  Trash2 
} from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Route as RouteType } from '@/types'

const router = useRouter()
const { getRoutes, deleteRoute: apiDeleteRoute } = useApi()

const routes = ref<RouteType[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const stats = computed(() => {
  const total = routes.value.length
  const avgDuration = total > 0 
    ? Math.round(routes.value.reduce((sum, route) => sum + route.duration, 0) / total)
    : 0
  const avgCost = total > 0 
    ? Math.round(routes.value.reduce((sum, route) => sum + route.cost, 0) / total)
    : 0
  const seaRoutes = routes.value.filter(route => route.transportationMode === 'Sea').length
  
  return { total, avgDuration, avgCost, seaRoutes }
})

const loadRoutes = async () => {
  isLoading.value = true
  error.value = null
  try {
    routes.value = await getRoutes()
  } catch (err) {
    error.value = 'Failed to load routes'
    console.error('Error loading routes:', err)
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

const formatNumber = (num: number) => {
  return new Intl.NumberFormat().format(num)
}

const viewRoute = (route: RouteType) => {
  router.push(`/routes/${route.routeId}`)
}

const editRoute = (route: RouteType) => {
  // Navigate to edit route page (when implemented)
  console.log('Edit route:', route)
}

const deleteRoute = async (route: RouteType) => {
  if (!confirm(`Are you sure you want to delete route "${route.originPort} → ${route.destinationPort}"?`)) {
    return
  }
  
  try {
    await apiDeleteRoute(route.routeId)
    await loadRoutes() // Reload routes after deletion
  } catch (error) {
    console.error('Error deleting route:', error)
    alert('Failed to delete route. Please try again.')
  }
}

onMounted(() => {
  loadRoutes()
})
</script>