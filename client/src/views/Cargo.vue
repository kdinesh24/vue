<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Cargo</h1>
          <p class="mt-2 text-gray-600">Manage your cargo inventory</p>
        </div>
        <Button @click="$router.push('/cargo/create')">
          <Plus class="mr-2 h-4 w-4" />
          Add Cargo
        </Button>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-blue-600">{{ stats.total }}</div>
              <Package class="ml-auto h-8 w-8 text-blue-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Total Cargo</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-green-600">{{ stats.totalWeight }}kg</div>
              <Weight class="ml-auto h-8 w-8 text-green-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Total Weight</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-yellow-600">${{ stats.totalValue }}</div>
              <DollarSign class="ml-auto h-8 w-8 text-yellow-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Total Value</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-purple-600">{{ stats.electronics }}</div>
              <Cpu class="ml-auto h-8 w-8 text-purple-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Electronics</p>
          </CardContent>
        </Card>
      </div>

      <!-- Cargo Table -->
      <Card>
        <CardHeader>
          <CardTitle>All Cargo</CardTitle>
          <CardDescription>
            A comprehensive list of all cargo in your inventory
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">Loading cargo...</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadCargo" class="mt-4" variant="outline">
              Try Again
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Weight (kg)</TableHead>
                  <TableHead>Value ($)</TableHead>
                  <TableHead>Origin</TableHead>
                  <TableHead>Destination</TableHead>
                  <TableHead>Shipment</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="cargo.length === 0">
                  <TableCell :colspan="8" class="h-24 text-center">
                    No cargo found.
                  </TableCell>
                </TableRow>
                <TableRow v-for="item in cargo" :key="item.cargoId">
                  <TableCell class="font-medium">{{ item.cargoId }}</TableCell>
                  <TableCell>
                    <Badge :variant="getTypeBadgeVariant(item.type)">
                      {{ item.type }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ item.weight ? formatNumber(item.weight) : 'N/A' }}</TableCell>
                  <TableCell>${{ formatNumber(item.value) }}</TableCell>
                  <TableCell>{{ item.shipment?.origin || 'N/A' }}</TableCell>
                  <TableCell>{{ item.shipment?.destination || 'N/A' }}</TableCell>
                  <TableCell>
                    <span v-if="item.shipment" class="text-blue-600">
                      #{{ item.shipment.shipmentId }}
                    </span>
                    <span v-else class="text-gray-400">Unassigned</span>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="editCargo(item)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="deleteCargo(item)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { 
  Plus, 
  Package, 
  Weight, 
  DollarSign, 
  Cpu, 
  Loader2, 
  AlertCircle, 
  Edit, 
  Trash2 
} from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Cargo } from '@/types'

const router = useRouter()
const { getCargo, deleteCargo: apiDeleteCargo } = useApi()

const cargo = ref<Cargo[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const stats = computed(() => {
  const total = cargo.value.length
  const totalWeight = cargo.value.reduce((sum, item) => sum + item.weight, 0)
  const totalValue = cargo.value.reduce((sum, item) => sum + item.value, 0)
  const electronics = cargo.value.filter(item => item.type === 'Electronics').length
  
  return { total, totalWeight, totalValue, electronics }
})

const loadCargo = async () => {
  isLoading.value = true
  error.value = null
  try {
    cargo.value = await getCargo()
  } catch (err) {
    error.value = 'Failed to load cargo'
    console.error('Error loading cargo:', err)
  } finally {
    isLoading.value = false
  }
}

const getTypeBadgeVariant = (type: string) => {
  switch (type) {
    case 'Electronics':
      return 'default'
    case 'Clothing':
      return 'secondary'
    case 'Food':
      return 'outline'
    case 'Machinery':
      return 'destructive'
    default:
      return 'outline'
  }
}

const formatNumber = (num: number) => {
  return new Intl.NumberFormat().format(num)
}

const editCargo = (item: Cargo) => {
  router.push(`/cargo/${item.cargoId}/edit`)
}

const deleteCargo = async (item: Cargo) => {
  if (!item.cargoId || !confirm(`Are you sure you want to delete cargo "${item.type}" (ID: ${item.cargoId})?`)) {
    return
  }
  
  try {
    await apiDeleteCargo(item.cargoId)
    await loadCargo() // Reload cargo after deletion
  } catch (error) {
    console.error('Error deleting cargo:', error)
    alert('Failed to delete cargo. Please try again.')
  }
}

onMounted(() => {
  loadCargo()
  
  // Listen for real-time updates from WebSocket
  window.addEventListener('cargo-updated', loadCargo)
})

onUnmounted(() => {
  window.removeEventListener('cargo-updated', loadCargo)
})
</script>