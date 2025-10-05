import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/auth/callback',
      name: 'AuthCallback',
      component: () => import('../views/AuthCallback.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('../views/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/shipments',
      name: 'Shipments',
      component: () => import('../views/Shipments.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/shipments/create',
      name: 'CreateShipment',
      component: () => import('../views/CreateShipment.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/shipments/:id/edit',
      name: 'EditShipment',
      component: () => import('../views/EditShipment.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/vendors',
      name: 'Vendors',
      component: () => import('../views/Vendors.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/vendors/create',
      name: 'CreateVendor',
      component: () => import('../views/CreateVendor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/vendors/:id/edit',
      name: 'EditVendor',
      component: () => import('../views/EditVendor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/cargo',
      name: 'Cargo',
      component: () => import('../views/Cargo.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/cargo/create',
      name: 'CreateCargo',
      component: () => import('../views/CreateCargo.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/cargo/:id/edit',
      name: 'EditCargo',
      component: () => import('../views/EditCargo.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/routes',
      name: 'Routes',
      component: () => import('../views/Routes.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/routes/create',
      name: 'CreateRoute',
      component: () => import('../views/CreateRoute.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/routes/:id/edit',
      name: 'EditRoute',
      component: () => import('../views/EditRoute.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/routes/:id',
      name: 'RouteDetail',
      component: () => import('../views/RouteDetail.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/deliveries',
      name: 'Deliveries',
      component: () => import('../views/Deliveries.vue'),
      meta: { requiresAuth: true }
    },
  ],
})

// Navigation guard
router.beforeEach(async (to, _from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const user = localStorage.getItem('user')

  if (requiresAuth && !user) {
    // Redirect to login if route requires auth and user is not logged in
    next('/login')
  } else if (to.path === '/login' && user) {
    // Redirect to dashboard if user is already logged in
    next('/dashboard')
  } else {
    next()
  }
})

export default router