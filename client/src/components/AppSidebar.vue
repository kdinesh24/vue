<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  LayoutDashboard, 
  Package, 
  Users, 
  Route as RouteIcon, 
  Truck, 
  CheckCircle,
  LogOut,
  User as UserIcon
} from 'lucide-vue-next'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import { useApi } from '@/composables/useApi'

const route = useRoute()
const router = useRouter()
const { post, get } = useApi()

interface UserInfo {
  id: number
  email: string
  name: string
  picture?: string
  provider: string
  role: string
}

const user = ref<UserInfo | null>(null)
const loading = ref(true)

const navigationItems = [
  { title: 'Dashboard', url: '/dashboard', icon: LayoutDashboard },
  { title: 'Shipments', url: '/shipments', icon: Package },
  { title: 'Vendors', url: '/vendors', icon: Users },
  { title: 'Cargo', url: '/cargo', icon: Truck },
  { title: 'Routes', url: '/routes', icon: RouteIcon },
  { title: 'Deliveries', url: '/deliveries', icon: CheckCircle },
]

const isActive = (url: string) => (url === '/dashboard' ? route.path === '/dashboard' : route.path.startsWith(url))

const fetchUserInfo = async () => {
  try {
    const response = await get<any>('/auth/user')
    if (response.success && response.user) {
      user.value = response.user
      localStorage.setItem('user', JSON.stringify(response.user))
    } else {
      // Try to get from localStorage
      const storedUser = localStorage.getItem('user')
      if (storedUser) {
        user.value = JSON.parse(storedUser)
      }
    }
  } catch (error) {
    console.error('Error fetching user:', error)
    // Try to get from localStorage
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      user.value = JSON.parse(storedUser)
    }
  } finally {
    loading.value = false
  }
}

const handleLogout = async () => {
  try {
    await post('/auth/logout')
    localStorage.removeItem('user')
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
    // Force logout on client side
    localStorage.removeItem('user')
    router.push('/login')
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <Sidebar variant="inset" collapsible="icon">
    <SidebarHeader>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton size="lg" as-child>
            <router-link to="/">
              <div class="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                <Package class="size-4" />
              </div>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-semibold">LifeStyle</span>
                <span class="truncate text-xs">Supply Chain</span>
              </div>
            </router-link>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarHeader>
    
    <SidebarContent>
      <SidebarGroup>
        <SidebarGroupLabel>Platform</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in navigationItems" :key="item.title">
              <SidebarMenuButton 
                :as-child="true" 
                :is-active="isActive(item.url)"
                :tooltip="item.title"
              >
                <router-link :to="item.url">
                  <component :is="item.icon" />
                  <span>{{ item.title }}</span>
                </router-link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>

    <SidebarFooter>
      <!-- User Profile Section -->
      <SidebarMenu>
        <SidebarMenuItem v-if="!loading && user">
          <div class="flex items-center gap-3 p-2">
            <!-- User Avatar -->
            <div v-if="user.picture" class="w-8 h-8 rounded-full overflow-hidden flex-shrink-0">
              <img :src="user.picture" :alt="user.name" class="w-full h-full object-cover" />
            </div>
            <div v-else class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0">
              <UserIcon class="w-5 h-5 text-blue-600" />
            </div>
            
            <!-- User Info - Hidden when collapsed -->
            <div class="flex-1 min-w-0 group-data-[collapsible=icon]:hidden">
              <p class="text-sm font-semibold text-gray-900 truncate">{{ user.name }}</p>
              <p class="text-xs text-gray-500 truncate">{{ user.role }}</p>
            </div>
          </div>
        </SidebarMenuItem>

        <!-- Logout Button -->
        <SidebarMenuItem v-if="!loading && user">
          <SidebarMenuButton 
            @click="handleLogout"
            class="text-red-600 hover:bg-red-50 hover:text-red-700"
            :tooltip="'Logout'"
          >
            <LogOut class="w-4 h-4" />
            <span>Logout</span>
          </SidebarMenuButton>
        </SidebarMenuItem>

        <!-- Loading State -->
        <SidebarMenuItem v-else-if="loading">
          <div class="flex items-center gap-3 p-2">
            <div class="w-8 h-8 rounded-full bg-gray-200 animate-pulse"></div>
            <div class="flex-1 space-y-2 group-data-[collapsible=icon]:hidden">
              <div class="h-4 bg-gray-200 rounded animate-pulse"></div>
              <div class="h-3 bg-gray-200 rounded w-2/3 animate-pulse"></div>
            </div>
          </div>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
  </Sidebar>
</template>
