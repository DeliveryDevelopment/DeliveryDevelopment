import React from "react";
import { Card, Empty } from "antd";

// TODO: Implementation owned by teammate C
const MapVisualization = ({ order }) => (
  <Card size="small" style={{ marginTop: 16 }}>
    <Empty description={null} />
  </Card>
);

MapVisualization.defaultProps = {
  order: null,
};

export default MapVisualization;
