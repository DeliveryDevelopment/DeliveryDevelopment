import React, { useEffect } from "react";
import { Card } from "antd";

// TODO: Implementation owned by teammate B (history) + C (map)
const OrderHistoryPanel = ({
  orders,
  onOrdersChange,
  selectedOrderId,
  onSelectOrder,
  onPendingCountChange,
}) => {
  useEffect(() => {
    onPendingCountChange(0);
  }, [onPendingCountChange]);

  return (
    <Card />
  );
};

OrderHistoryPanel.defaultProps = {
  orders: [],
  onOrdersChange: () => {},
  selectedOrderId: null,
  onSelectOrder: () => {},
  onPendingCountChange: () => {},
};

export default OrderHistoryPanel;
