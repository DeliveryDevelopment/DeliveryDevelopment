import { request } from "./client";

export const listOrders = () => request("/orders");

export const getOrder = (orderId) => request(`/orders/${orderId}`);

export const createOrder = (payload) =>
  request("/orders", {
    method: "POST",
    body: payload,
  });

export const confirmOrder = (orderId) =>
  request(`/orders/${orderId}/confirm`, {
    method: "POST",
  });

export default {
  listOrders,
  getOrder,
  createOrder,
  confirmOrder,
};
