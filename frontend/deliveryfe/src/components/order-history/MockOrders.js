// src/components/order-history/mockOrders.js

/**
 * Simple in-memory mock orders for development.
 * Later you can replace this with real API calls.
 */

const now = Date.now();

export const MockOrders = [
  {
    id: "ORD-001",
    customerName: "Alice Johnson",
    startAddress: "USC Village, Los Angeles, CA 90007",
    endAddress:
      "Los Angeles International Airport (LAX), Los Angeles, CA 90045",
    startTime: new Date(now - 5 * 60 * 1000).toISOString(), // 5 min ago
    endTime: new Date(now + 20 * 60 * 1000).toISOString(), // 20 min later
    courierType: "drone", // "drone" | "robot"
    status: "IN_PROGRESS", // IN_PROGRESS / COMPLETED / CANCELLED
  },
  {
    id: "ORD-002",
    customerName: "Alice Johnson",
    startAddress: "USC Village, Los Angeles, CA 90007",
    endAddress: "Santa Monica Pier, Santa Monica, CA 90401",
    startTime: new Date(now - 40 * 60 * 1000).toISOString(),
    endTime: new Date(now - 10 * 60 * 1000).toISOString(),
    courierType: "robot",
    status: "COMPLETED",
  },
  {
    id: "ORD-003",
    customerName: "Alice Johnson",
    startAddress: "3201 S Hoover St, Los Angeles, CA 90007",
    endAddress: "Staples Center, 1111 S Figueroa St, Los Angeles, CA 90015",
    startTime: new Date(now + 10 * 60 * 1000).toISOString(), // future order
    endTime: new Date(now + 40 * 60 * 1000).toISOString(),
    courierType: "drone",
    status: "SCHEDULED",
  },
];
