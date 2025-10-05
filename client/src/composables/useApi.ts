import type { Shipment, Vendor, Cargo, Route, Delivery } from '../types'

const BASE_URL = 'http://localhost:8081/api'

// Helper function for fetch requests
const apiRequest = async <T>(url: string, options: RequestInit = {}): Promise<T> => {
  const response = await fetch(`${BASE_URL}${url}`, {
    credentials: 'include',  // Important for session cookies
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  // Handle empty responses (204 No Content or empty body)
  const contentType = response.headers.get('content-type')
  const contentLength = response.headers.get('content-length')
  
  if (contentLength === '0' || response.status === 204) {
    return {} as T
  }
  
  if (contentType && contentType.includes('application/json')) {
    const text = await response.text()
    return text ? JSON.parse(text) : {} as T
  }
  
  return response.json()
}

export const useApi = () => {
  // Generic API methods
  const get = async <T = any>(url: string): Promise<T> => {
    return apiRequest<T>(url, { method: 'GET' })
  }

  const post = async <T = any>(url: string, data?: any): Promise<T> => {
    return apiRequest<T>(url, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    })
  }

  const put = async <T = any>(url: string, data: any): Promise<T> => {
    return apiRequest<T>(url, {
      method: 'PUT',
      body: JSON.stringify(data),
    })
  }

  const del = async <T = any>(url: string): Promise<T> => {
    return apiRequest<T>(url, { method: 'DELETE' })
  }

  // Shipments API
  const getShipments = async (): Promise<Shipment[]> => {
    try {
      const data = await apiRequest<Shipment[]>('/shipments')
      console.log('Fetched shipments:', data)
      return data || []
    } catch (error) {
      console.error('Error fetching shipments:', error)
      return []
    }
  }
  
  const getShipment = async (id: number): Promise<Shipment | null> => {
    try {
      const data = await apiRequest<Shipment>(`/shipments/${id}`)
      return data
    } catch (error) {
      console.error('Error fetching shipment:', error)
      return null
    }
  }
  
  const createShipment = async (shipment: Omit<Shipment, 'shipmentId'>): Promise<Shipment> => {
    try {
      const data = await apiRequest<Shipment>('/shipments', {
        method: 'POST',
        body: JSON.stringify(shipment)
      })
      return data
    } catch (error) {
      console.error('Error creating shipment:', error)
      throw error
    }
  }
  
  const updateShipment = async (id: number, shipment: Omit<Shipment, 'shipmentId'>): Promise<Shipment> => {
    try {
      const data = await apiRequest<Shipment>(`/shipments/${id}`, {
        method: 'PUT',
        body: JSON.stringify(shipment)
      })
      return data
    } catch (error) {
      console.error('Error updating shipment:', error)
      throw error
    }
  }
  
  const deleteShipment = async (id: number): Promise<void> => {
    try {
      await apiRequest(`/shipments/${id}`, { 
        method: 'DELETE' 
      })
    } catch (error) {
      console.error('Error deleting shipment:', error)
      throw error
    }
  }

  // Vendors API
  const getVendors = async (): Promise<Vendor[]> => {
    try {
      const data = await apiRequest<Vendor[]>('/vendors')
      console.log('Fetched vendors:', data)
      return data || []
    } catch (error) {
      console.error('Error fetching vendors:', error)
      return []
    }
  }
  
  const getVendor = async (id: number): Promise<Vendor | null> => {
    try {
      const data = await apiRequest<Vendor>(`/vendors/${id}`)
      return data
    } catch (error) {
      console.error('Error fetching vendor:', error)
      return null
    }
  }
  
  const createVendor = async (vendor: Omit<Vendor, 'vendorId'>): Promise<Vendor> => {
    try {
      const data = await apiRequest<Vendor>('/vendors', {
        method: 'POST',
        body: JSON.stringify(vendor)
      })
      return data
    } catch (error) {
      console.error('Error creating vendor:', error)
      throw error
    }
  }
  
  const updateVendor = async (id: number, vendor: Omit<Vendor, 'vendorId'>): Promise<Vendor> => {
    try {
      const data = await apiRequest<Vendor>(`/vendors/${id}`, {
        method: 'PUT',
        body: JSON.stringify(vendor)
      })
      return data
    } catch (error) {
      console.error('Error updating vendor:', error)
      throw error
    }
  }
  
  const deleteVendor = async (id: number): Promise<void> => {
    try {
      await apiRequest(`/vendors/${id}`, { 
        method: 'DELETE' 
      })
    } catch (error) {
      console.error('Error deleting vendor:', error)
      throw error
    }
  }

  // Routes API
  const getRoutes = async (): Promise<Route[]> => {
    try {
      const data = await apiRequest<Route[]>('/routes')
      console.log('Fetched routes:', data)
      return data || []
    } catch (error) {
      console.error('Error fetching routes:', error)
      return []
    }
  }
  
  const getRoute = async (id: number): Promise<Route | null> => {
    try {
      const data = await apiRequest<Route>(`/routes/${id}`)
      return data
    } catch (error) {
      console.error('Error fetching route:', error)
      return null
    }
  }
  
  const createRoute = async (route: Omit<Route, 'routeId'>): Promise<Route> => {
    try {
      const data = await apiRequest<Route>('/routes', {
        method: 'POST',
        body: JSON.stringify(route)
      })
      return data
    } catch (error) {
      console.error('Error creating route:', error)
      throw error
    }
  }
  
  const updateRoute = async (id: number, route: Omit<Route, 'routeId'>): Promise<Route> => {
    try {
      const data = await apiRequest<Route>(`/routes/${id}`, {
        method: 'PUT',
        body: JSON.stringify(route)
      })
      return data
    } catch (error) {
      console.error('Error updating route:', error)
      throw error
    }
  }
  
  const deleteRoute = async (id: number): Promise<void> => {
    try {
      await apiRequest(`/routes/${id}`, { 
        method: 'DELETE' 
      })
    } catch (error) {
      console.error('Error deleting route:', error)
      throw error
    }
  }

  // Cargo API
  const getCargo = async (): Promise<Cargo[]> => {
    try {
      const data = await apiRequest<Cargo[]>('/cargo')
      console.log('Fetched cargo:', data)
      return data || []
    } catch (error) {
      console.error('Error fetching cargo:', error)
      return []
    }
  }
  
  const getCargoItem = async (id: number): Promise<Cargo | null> => {
    try {
      const data = await apiRequest<Cargo>(`/cargo/${id}`)
      return data
    } catch (error) {
      console.error('Error fetching cargo item:', error)
      return null
    }
  }
  
  const createCargo = async (cargo: Omit<Cargo, 'cargoId'>): Promise<Cargo> => {
    try {
      const data = await apiRequest<Cargo>('/cargo', {
        method: 'POST',
        body: JSON.stringify(cargo)
      })
      return data
    } catch (error) {
      console.error('Error creating cargo:', error)
      throw error
    }
  }
  
  const updateCargo = async (id: number, cargo: Omit<Cargo, 'cargoId'>): Promise<Cargo> => {
    try {
      const data = await apiRequest<Cargo>(`/cargo/${id}`, {
        method: 'PUT',
        body: JSON.stringify(cargo)
      })
      return data
    } catch (error) {
      console.error('Error updating cargo:', error)
      throw error
    }
  }
  
  const deleteCargo = async (id: number): Promise<void> => {
    try {
      await apiRequest(`/cargo/${id}`, { 
        method: 'DELETE' 
      })
    } catch (error) {
      console.error('Error deleting cargo:', error)
      throw error
    }
  }

  // Deliveries API
  const getDeliveries = async (): Promise<Delivery[]> => {
    try {
      const data = await apiRequest<Delivery[]>('/deliveries')
      console.log('Fetched deliveries:', data)
      return data || []
    } catch (error) {
      console.error('Error fetching deliveries:', error)
      return []
    }
  }
  
  const getDelivery = async (id: number): Promise<Delivery | null> => {
    try {
      const data = await apiRequest<Delivery>(`/deliveries/${id}`)
      return data
    } catch (error) {
      console.error('Error fetching delivery:', error)
      return null
    }
  }
  
  const createDelivery = async (delivery: Omit<Delivery, 'deliveryId'>): Promise<Delivery> => {
    try {
      const data = await apiRequest<Delivery>('/deliveries', {
        method: 'POST',
        body: JSON.stringify(delivery)
      })
      return data
    } catch (error) {
      console.error('Error creating delivery:', error)
      throw error
    }
  }

  return {
    // Generic methods
    get,
    post,
    put,
    del,
    // Shipments
    getShipments,
    getShipment,
    createShipment,
    updateShipment,
    deleteShipment,
    // Vendors
    getVendors,
    getVendor,
    createVendor,
    updateVendor,
    deleteVendor,
    // Routes
    getRoutes,
    getRoute,
    createRoute,
    updateRoute,
    deleteRoute,
    // Cargo
    getCargo,
    getCargoItem,
    createCargo,
    updateCargo,
    deleteCargo,
    // Deliveries
    getDeliveries,
    getDelivery,
    createDelivery,
  }
}