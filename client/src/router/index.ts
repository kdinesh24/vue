import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../views/Dashboard.vue'),
    },
    {
      path: '/shipments',
      component: () => import('../views/Shipments.vue'),
    },
    {
      path: '/shipments/create',
      component: () => import('../views/CreateShipment.vue'),
    },
    {
      path: '/shipments/:id/edit',
      component: () => import('../views/EditShipment.vue'),
    },
    {
      path: '/vendors',
      component: () => import('../views/Vendors.vue'),
    },
    {
      path: '/vendors/create',
      component: () => import('../views/CreateVendor.vue'),
    },
    {
      path: '/cargo',
      component: () => import('../views/Cargo.vue'),
    },
    {
      path: '/cargo/create',
      component: () => import('../views/CreateCargo.vue'),
    },
    {
      path: '/routes',
      component: () => import('../views/Routes.vue'),
    },
    {
      path: '/routes/create',
      component: () => import('../views/CreateRoute.vue'),
    },
    {
      path: '/routes/:id',
      component: () => import('../views/RouteDetail.vue'),
    },
    {
      path: '/deliveries',
      component: () => import('../views/Deliveries.vue'),
    },
  ],
})

export default router