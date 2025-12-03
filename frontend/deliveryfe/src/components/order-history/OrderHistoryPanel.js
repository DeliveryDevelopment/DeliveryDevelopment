// src/components/order-history/OrderHistoryPanel.js
import React, { useMemo, useState } from "react";
import { List, Card, Tag, Typography, Space, Divider } from "antd";
import MapVisualization from "../map/MapVisualization";
import { MockOrders } from "./MockOrders";

const { Text, Title } = Typography;

/**
 * OrderHistoryPanel
 *
 * Layout:
 *  - Left: list of orders
 *  - Right: detail panel + route map (MapVisualization)
 *
 * Props:
 *  - orders?: Order[]
 *      If not provided, we fall back to local mockOrders for development.
 *
 * Order shape (minimal fields MapVisualization needs):
 *  - id: string
 *  - startAddress: string
 *  - endAddress: string
 *  - startTime: ISO string
 *  - endTime: ISO string
 *  - courierType: "drone" | "robot"
 *  - status: string
 */
const OrderHistoryPanel = ({ orders = MockOrders }) => {
  //
  orders = [...orders, ...MockOrders];
  const [selectedOrderId, setSelectedOrderId] = useState(
    orders.length > 0 ? orders[0].id : null
  );

  const selectedOrder = useMemo(
    () => orders.find((o) => o.id === selectedOrderId) || null,
    [orders, selectedOrderId]
  );

  const renderStatusTag = (status) => {
    switch (status) {
      case "IN_PROGRESS":
        return <Tag color="processing">In Progress</Tag>;
      case "COMPLETED":
        return <Tag color="success">Completed</Tag>;
      case "CANCELLED":
        return <Tag color="error">Cancelled</Tag>;
      case "SCHEDULED":
        return <Tag color="warning">Scheduled</Tag>;
      default:
        return <Tag>{status}</Tag>;
    }
  };

  return (
    <div
      style={{
        display: "flex",
        gap: 24,
        alignItems: "flex-start",
        minHeight: 360,
      }}
    >
      {/* ----- Left: Order list ----- */}
      <div style={{ flex: 1, minWidth: 320 }}>
        <Title level={4} style={{ marginBottom: 16 }}>
          Orders
        </Title>
        <List
          bordered
          dataSource={orders}
          locale={{
            emptyText: "No orders yet. You can still preview the map panel.",
          }}
          renderItem={(order) => {
            const isActive = order.id === selectedOrderId;
            return (
              <List.Item
                onClick={() => setSelectedOrderId(order.id)}
                style={{
                  cursor: "pointer",
                  backgroundColor: isActive ? "#e6f7ff" : "transparent",
                }}
              >
                <List.Item.Meta
                  title={
                    <Space>
                      <Text strong>#{order.id}</Text>
                      {renderStatusTag(order.status)}
                    </Space>
                  }
                  description={
                    <>
                      <Text>
                        From:{" "}
                        <Text strong ellipsis>
                          {order.startAddress}
                        </Text>
                      </Text>
                      <br />
                      <Text>
                        To:{" "}
                        <Text strong ellipsis>
                          {order.endAddress}
                        </Text>
                      </Text>
                    </>
                  }
                />
              </List.Item>
            );
          }}
        />
      </div>

      {/* ----- Right: Detail + Map ----- */}
      <div style={{ flex: 1.4, minWidth: 360 }}>
        <Title level={4} style={{ marginBottom: 16 }}>
          Order Details & Route
        </Title>

        {!selectedOrder ? (
          <Card>
            <Text type="secondary">
              Select an order from the list to view details and route.
            </Text>
          </Card>
        ) : (
          <>
            <Card style={{ marginBottom: 16 }}>
              <Space direction="vertical" size={4}>
                <Text>
                  <strong>Order ID:</strong> {selectedOrder.id}
                </Text>
                <Text>
                  <strong>Customer:</strong> {selectedOrder.customerName}
                </Text>
                <Text>
                  <strong>Courier Type:</strong>{" "}
                  {selectedOrder.courierType === "drone" ? "Drone" : "Robot"}
                </Text>
                <Text>
                  <strong>Status:</strong>{" "}
                  {renderStatusTag(selectedOrder.status)}
                </Text>
                <Divider style={{ margin: "8px 0" }} />
                <Text>
                  <strong>Start:</strong> {selectedOrder.startAddress}
                </Text>
                <Text>
                  <strong>End:</strong> {selectedOrder.endAddress}
                </Text>
                <Text>
                  <strong>Start Time:</strong>{" "}
                  {new Date(selectedOrder.startTime).toLocaleString()}
                </Text>
                <Text>
                  <strong>End Time:</strong>{" "}
                  {new Date(selectedOrder.endTime).toLocaleString()}
                </Text>
              </Space>
            </Card>

            {/* MapVisualization expects an "order" prop with:
               { startAddress, endAddress, startTime, endTime, courierType } */}
            <MapVisualization order={selectedOrder} />
          </>
        )}
      </div>
    </div>
  );
};

export default OrderHistoryPanel;
