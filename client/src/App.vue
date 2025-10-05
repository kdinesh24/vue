<template>
  <!-- Login page without sidebar -->
  <div v-if="isLoginPage">
    <router-view />
  </div>

  <!-- Main app with sidebar -->
  <SidebarProvider v-else :default-open="true">
    <AppSidebar />
    <SidebarInset>
      <!-- Top header bar -->
      <header class="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12">
        <div class="flex items-center gap-2 px-4">
          <SidebarTrigger class="-ml-1" />
          <Separator orientation="vertical" class="mr-2 h-4" />
          <Breadcrumb>
            <BreadcrumbList>
              <BreadcrumbItem class="hidden md:block">
                <BreadcrumbLink href="#">
                  LifeStyle
                </BreadcrumbLink>
              </BreadcrumbItem>
              <BreadcrumbSeparator class="hidden md:block" />
              <BreadcrumbItem>
                <BreadcrumbPage>{{ getPageTitle() }}</BreadcrumbPage>
              </BreadcrumbItem>
            </BreadcrumbList>
          </Breadcrumb>
        </div>
      </header>
      
      <!-- Page content -->
      <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
        <router-view />
      </div>
    </SidebarInset>
    
    <!-- Real-time notifications -->
    <RealtimeNotifications />
  </SidebarProvider>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from '@/components/AppSidebar.vue'
import RealtimeNotifications from '@/components/RealtimeNotifications.vue'
import { SidebarProvider, SidebarInset, SidebarTrigger } from '@/components/ui/sidebar'
import { Separator } from '@/components/ui/separator'
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'

const route = useRoute()

const isLoginPage = computed(() => route.path === '/login' || route.path === '/auth/callback')

const getPageTitle = () => {
  const titles: Record<string, string> = {
    '/': 'Dashboard',
    '/shipments': 'Shipments',
    '/vendors': 'Vendors',
    '/cargo': 'Cargo Management',
    '/routes': 'Routes',
    '/deliveries': 'Deliveries'
  }
  
  const path = route.path
  return titles[path] || titles[Object.keys(titles).find(key => path.startsWith(key)) || '/'] || 'Supply Chain'
}
</script>
