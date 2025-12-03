// /src/components/user/UserPage.js
import React, { useState } from "react";
import { Tabs, message } from "antd";
import MapVisualization from "./map/MapVisualization";
import OrderHistoryPanel from "./order-history/OrderHistoryPanel";
import UploadOrderPanel from "./order-create/OrderCreationPanel";

const { TabPane } = Tabs;

/**
 * UserPage = 用户主页（登录后进入）
 *
 * 现在包含两个主功能：
 * 1. Order History：查看并选择历史订单 + 底部地图可视化
 * 2. Upload Order：模拟“上传 / 创建”一个新订单
 *
 * 关键点：
 * - 订单列表的数据（含 mock）集中在 UserPage 里维护
 * - UploadOrderPanel 通过回调把新订单传上来，UserPage 更新本地列表
 * - OrderHistoryPanel 只是展示 + 选择订单，不直接管数据来源
 */

// 一些初始 mock 订单，方便演示
const initialMockOrders = [
  {
    id: "ORD-20251203-001",
    routeId: "route-usc-lax",
    customerName: "USC Robotics Lab",
    startAddress: "3201 S Hoover St, Los Angeles, CA 90089",
    endAddress: "1 World Way, Los Angeles, CA 90045",
    courierType: "drone", // or "robot"
    startTime: "2025-12-03T18:00:00Z",
    endTime: "2025-12-03T18:40:00Z",
    status: "IN_PROGRESS",
  },
  {
    id: "ORD-20251203-002",
    routeId: "route-village-lax",
    customerName: "USC Village Delivery",
    startAddress: "3201 S Hoover St, Los Angeles, CA 90089",
    endAddress: "1 World Way, Los Angeles, CA 90045",
    courierType: "robot",
    startTime: "2025-12-03T17:00:00Z",
    endTime: "2025-12-03T17:45:00Z",
    status: "COMPLETED",
  },
];

const UserPage = ({ onOrderCreated, onOrderUpdated }) => {
  // 所有订单（mock + 上传）都放在这里
  const [orders, setOrders] = useState(initialMockOrders);
  const [selectedOrder, setSelectedOrder] = useState(null);

  // 点击列表中的订单
  const handleOrderClick = (order) => {
    setSelectedOrder(order);
  };

  // UploadOrderPanel 成功“上传”一个订单时触发
  const handleOrderUploaded = (newOrder) => {
    setOrders((prev) => [...prev, newOrder]);
    message.success("Order uploaded (mock) successfully.");

    // 通知上层（App / DashboardLayout）有新订单产生
    if (onOrderCreated) {
      onOrderCreated(newOrder);
    }
  };

  // 未来如果在列表中修改了订单状态（比如确认完成）可以走这个回调
  const handleOrderUpdated = (updatedOrder) => {
    setOrders((prev) =>
      prev.map((o) =>
        o.id === updatedOrder.id ? { ...o, ...updatedOrder } : o
      )
    );
    if (onOrderUpdated) {
      onOrderUpdated(updatedOrder);
    }
  };

  return (
    <Tabs
      defaultActiveKey="1"
      destroyInactiveTabPane={true}
      style={{ padding: 20 }}
    >
      {/* -------------------------
          Tab 1: Order History
        -------------------------- */}
      <TabPane tab="Order History" key="1">
        <OrderHistoryPanel
          orders={orders}
          onSelectOrder={handleOrderClick}
          onOrderUpdated={handleOrderUpdated}
        />

        {/* 下面是地图可视化：选中订单后显示 */}
        {selectedOrder && (
          <div style={{ marginTop: 24 }}>
            <MapVisualization order={selectedOrder} />
          </div>
        )}
      </TabPane>

      {/* -------------------------
          Tab 2: Upload Order
        -------------------------- */}
      <TabPane tab="Upload Order" key="2">
        <UploadOrderPanel onUploaded={handleOrderUploaded} />
      </TabPane>
    </Tabs>
  );
};

export default UserPage;
