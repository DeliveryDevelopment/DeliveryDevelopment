// /src/components/order-create/UploadOrderPanel.js
import React, { useState } from "react";
import {
  Card,
  Form,
  Input,
  Select,
  DatePicker,
  Button,
  message,
  Typography,
} from "antd";
// 实际接上后端时可以这样用：
// import { createOrder } from "../../api/orders";

const { RangePicker } = DatePicker;
const { Option } = Select;
const { Text } = Typography;

/**
 * UploadOrderPanel
 *
 * 用来“上传 / 创建”一条订单，目前是纯前端 mock：
 * - 提交表单后构造一个订单对象
 * - 返回给父组件(onUploaded)，父组件更新本地 orders 列表
 *
 * props:
 * - onUploaded(newOrder)
 */

const UploadOrderPanel = ({ onUploaded }) => {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  const handleFinish = async (values) => {
    const { customerName, startAddress, endAddress, courierType, timeRange } =
      values;

    const [startMoment, endMoment] = timeRange || [];
    if (!startMoment || !endMoment) {
      message.error("Please select start/end time");
      return;
    }

    // 构造要发送到后端的 payload
    const payload = {
      customerName,
      startAddress,
      endAddress,
      courierType,
      startTime: startMoment.toISOString(),
      endTime: endMoment.toISOString(),
    };

    setSubmitting(true);
    try {
      // -------------------------------
      // TODO: 真实接后端时，改成调用 createOrder：
      //
      // const savedOrder = await createOrder(payload);
      //
      // 现在先用纯前端 mock 一条记录：
      // -------------------------------
      const mockSavedOrder = {
        id: `MOCK-${Date.now()}`,
        routeId: `route-${Math.random().toString(36).slice(2, 8)}`,
        status: "SCHEDULED",
        ...payload,
      };

      if (onUploaded) {
        onUploaded(mockSavedOrder);
      }

      message.success("Mock order uploaded successfully");
      form.resetFields();
    } catch (err) {
      message.error(err.message || "Failed to upload order");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card>
      <Text type="secondary" style={{ display: "block", marginBottom: 16 }}>
        This form currently uses mock data only. Once the backend is ready,
        replace the mocked part with a real <code>createOrder</code> API call.
      </Text>
      <Form
        layout="vertical"
        form={form}
        onFinish={handleFinish}
        requiredMark={false}
      >
        <Form.Item
          name="customerName"
          label="Customer / Project Name"
          rules={[{ required: true, message: "Please enter customer name" }]}
        >
          <Input placeholder="e.g. USC Robotics Lab" />
        </Form.Item>

        <Form.Item
          name="startAddress"
          label="Start Address"
          rules={[{ required: true, message: "Please enter start address" }]}
        >
          <Input placeholder="e.g. 3201 S Hoover St, Los Angeles, CA 90089" />
        </Form.Item>

        <Form.Item
          name="endAddress"
          label="End Address"
          rules={[{ required: true, message: "Please enter end address" }]}
        >
          <Input placeholder="e.g. 1 World Way, Los Angeles, CA 90045" />
        </Form.Item>

        <Form.Item
          name="courierType"
          label="Courier Type"
          rules={[{ required: true, message: "Please select courier type" }]}
        >
          <Select placeholder="Select courier type">
            <Option value="drone">Drone</Option>
            <Option value="robot">Robot</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="timeRange"
          label="Planned Time Window"
          rules={[{ required: true, message: "Please pick a time window" }]}
        >
          <RangePicker
            showTime
            style={{ width: "100%" }}
            placeholder={["Start time", "End time"]}
          />
        </Form.Item>

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={submitting}
            shape="round"
          >
            Upload Order (Mock)
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default UploadOrderPanel;
