<template>
  <div class="container mx-auto px-4 py-8">
    <div class="space-y-6">
      <!-- Header Section -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">Vendors</h1>
          <p class="mt-2 text-gray-600">Manage your supply chain vendors</p>
        </div>
        <Button @click="$router.push('/vendors/create')">
          <Plus class="mr-2 h-4 w-4" />
          Add Vendor
        </Button>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-blue-600">{{ stats.total }}</div>
              <Building2 class="ml-auto h-8 w-8 text-blue-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Total Vendors</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-green-600">{{ stats.active }}</div>
              <CheckCircle class="ml-auto h-8 w-8 text-green-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Active Vendors</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-yellow-600">{{ stats.logistics }}</div>
              <Truck class="ml-auto h-8 w-8 text-yellow-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Logistics</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent class="p-6">
            <div class="flex items-center">
              <div class="text-2xl font-bold text-purple-600">{{ stats.transportation }}</div>
              <Ship class="ml-auto h-8 w-8 text-purple-500" />
            </div>
            <p class="text-xs text-muted-foreground mt-1">Transportation</p>
          </CardContent>
        </Card>
      </div>

      <!-- Vendors Table -->
      <Card>
        <CardHeader>
          <CardTitle>All Vendors</CardTitle>
          <CardDescription>
            A comprehensive list of all vendors in your supply chain
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="isLoading" class="flex items-center justify-center h-32">
            <Loader2 class="h-6 w-6 animate-spin" />
            <span class="ml-2">Loading vendors...</span>
          </div>
          
          <div v-else-if="error" class="text-center py-8">
            <AlertCircle class="h-12 w-12 text-red-500 mx-auto mb-4" />
            <p class="text-gray-600">{{ error }}</p>
            <Button @click="loadVendors" class="mt-4" variant="outline">
              Try Again
            </Button>
          </div>
          
          <div v-else>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Name</TableHead>
                  <TableHead>Service Type</TableHead>
                  <TableHead>Contact Email</TableHead>
                  <TableHead>Contact Phone</TableHead>
                  <TableHead>Address</TableHead>
                  <TableHead class="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-if="vendors.length === 0">
                  <TableCell :colspan="7" class="h-24 text-center">
                    No vendors found.
                  </TableCell>
                </TableRow>
                <TableRow v-for="vendor in vendors" :key="vendor.vendorId">
                  <TableCell class="font-medium">{{ vendor.vendorId }}</TableCell>
                  <TableCell>
                    <div class="font-medium">{{ vendor.name }}</div>
                  </TableCell>
                  <TableCell>
                    <Badge :variant="getServiceTypeBadgeVariant(vendor.serviceType)">
                      {{ vendor.serviceType }}
                    </Badge>
                  </TableCell>
                  <TableCell>{{ vendor.contactEmail }}</TableCell>
                  <TableCell>{{ vendor.contactPhone }}</TableCell>
                  <TableCell>
                    <div class="max-w-xs truncate">{{ vendor.address }}</div>
                  </TableCell>
                  <TableCell class="text-right">
                    <div class="flex items-center justify-end space-x-2">
                      <Button @click="editVendor(vendor)" size="sm" variant="outline">
                        <Edit class="h-4 w-4" />
                      </Button>
                      <Button @click="deleteVendor(vendor)" size="sm" variant="outline" class="text-red-600 hover:text-red-700">
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
  Building2, 
  CheckCircle, 
  Truck, 
  Ship, 
  Loader2, 
  AlertCircle, 
  Edit, 
  Trash2 
} from 'lucide-vue-next'
import { useApi } from '@/composables/useApi'
import type { Vendor } from '@/types'

const router = useRouter()
const { getVendors, deleteVendor: apiDeleteVendor } = useApi()

const vendors = ref<Vendor[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const stats = computed(() => {
  const total = vendors.value.length
  const active = vendors.value.filter(v => v.serviceType !== 'Inactive').length
  const logistics = vendors.value.filter(v => v.serviceType === 'Logistics').length
  const transportation = vendors.value.filter(v => v.serviceType === 'Transportation').length
  
  return { total, active, logistics, transportation }
})

const loadVendors = async () => {
  isLoading.value = true
  error.value = null
  try {
    vendors.value = await getVendors()
  } catch (err) {
    error.value = 'Failed to load vendors'
    console.error('Error loading vendors:', err)
  } finally {
    isLoading.value = false
  }
}

const getServiceTypeBadgeVariant = (serviceType: string) => {
  switch (serviceType) {
    case 'Logistics':
      return 'default'
    case 'Transportation':
      return 'secondary'
    case 'Warehousing':
      return 'outline'
    case 'Freight':
      return 'destructive'
    default:
      return 'outline'
  }
}

const editVendor = (vendor: Vendor) => {
  router.push(`/vendors/${vendor.vendorId}/edit`)
}

const deleteVendor = async (vendor: Vendor) => {
  if (!vendor.vendorId) {
    alert('Invalid vendor ID')
    return
  }
  
  if (!confirm(`Are you sure you want to delete vendor "${vendor.name}"?`)) {
    return
  }
  
  try {
    await apiDeleteVendor(vendor.vendorId)
    await loadVendors() // Reload vendors after deletion
  } catch (error) {
    console.error('Error deleting vendor:', error)
    alert('Failed to delete vendor. Please try again.')
  }
}

onMounted(() => {
  loadVendors()
  
  // Listen for real-time updates from WebSocket
  window.addEventListener('vendors-updated', loadVendors)
})

onUnmounted(() => {
  window.removeEventListener('vendors-updated', loadVendors)
})
</script>