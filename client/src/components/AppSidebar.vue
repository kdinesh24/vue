<script setup lang="ts">
import { useRoute } from 'vue-router'
import { 
  LayoutDashboard, 
  Package, 
  Users, 
  Route as RouteIcon, 
  Truck, 
  CheckCircle
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

const route = useRoute()

const navigationItems = [
  { title: 'Dashboard', url: '/', icon: LayoutDashboard },
  { title: 'Shipments', url: '/shipments', icon: Package },
  { title: 'Vendors', url: '/vendors', icon: Users },
  { title: 'Cargo', url: '/cargo', icon: Truck },
  { title: 'Routes', url: '/routes', icon: RouteIcon },
  { title: 'Deliveries', url: '/deliveries', icon: CheckCircle },
]

const isActive = (url: string) => (url === '/' ? route.path === '/' : route.path.startsWith(url))
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
      <div class="px-3 py-4 text-xs text-muted-foreground">
        © {{ new Date().getFullYear() }} LifeStyle
      </div>
    </SidebarFooter>
  </Sidebar>
</template>
