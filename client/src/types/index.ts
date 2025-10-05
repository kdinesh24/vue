export interface Shipment {
  shipmentId?: number;
  origin: string;
  destination: string;
  status: string;
  estimatedDelivery: string;
  assignedRoute?: Route;
  assignedVendor?: Vendor;
  createdAt?: string;
  updatedAt?: string;
  cargoItems?: Cargo[];
}

export interface Cargo {
  cargoId?: number;
  shipment?: Shipment;
  type: string;
  description: string;
  value: number;
  weight?: number;
  weightUnit?: string;
  volume?: number;
  createdAt?: string;
}

export interface Vendor {
  vendorId?: number;
  name: string;
  contactInfo: string;
  contactEmail?: string;
  contactPhone?: string;
  address?: string;
  serviceType: string;
  isActive?: boolean;
  createdAt?: string;
}

export interface Route {
  routeId?: number;
  originPort: string;
  destinationPort: string;
  distance?: number;
  duration: number;
  cost?: number;
  transportationMode?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Delivery {
  deliveryId?: number;
  shipment?: Shipment;
  actualDeliveryDate: string;
  recipient: string;
}