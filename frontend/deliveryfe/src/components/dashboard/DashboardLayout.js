import React, { useState } from "react";
import { Tabs, Badge } from "antd";
import OrderCreationPanel from "../order-create/OrderCreationPanel";
import OrderHistoryPanel from "../order-history/OrderHistoryPanel";

const { TabPane } = Tabs;

const DashboardLayout = ({ pendingCount, onPendingCountChange }) => {
  const [orders, setOrders] = useState([]);
  const [selectedOrderId, setSelectedOrderId] = useState(null);

  const handleOrderCreated = (newOrder) => {
    setOrders((prev) => [newOrder, ...prev]);
  };

  const handleOrdersChange = (nextOrders) => {
    setOrders(nextOrders);
  };

  const historyTabLabel = (
    <span>
      Order History
      {pendingCount > 0 && (
        <Badge
          count={pendingCount}
          overflowCount={99}
          offset={[8, -2]}
          style={{ backgroundColor: "#ff4d4f" }}
        />
      )}
    </span>
  );

  return (
    <Tabs defaultActiveKey="create" destroyInactiveTabPane>
      <TabPane tab="Place Order" key="create">
        <OrderCreationPanel onOrderCreated={handleOrderCreated} />
      </TabPane>
      <TabPane tab={historyTabLabel} key="history">
        <OrderHistoryPanel
          orders={orders}
          onOrdersChange={handleOrdersChange}
          selectedOrderId={selectedOrderId}
          onSelectOrder={setSelectedOrderId}
          onPendingCountChange={onPendingCountChange}
        />
      </TabPane>
    </Tabs>
  );
};

DashboardLayout.defaultProps = {
  pendingCount: 0,
  onPendingCountChange: () => {},
};

export default DashboardLayout;
