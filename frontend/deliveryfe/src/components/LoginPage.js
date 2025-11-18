import React, { useState } from "react";
import { Card, Form, Input, Button, Typography, message } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { login as loginApi, register as registerApi } from "../api/auth";
import { useAuth } from "../context/AuthContext";

const { Title } = Typography;

const LoginPage = () => {
  const [form] = Form.useForm();
  const { login } = useAuth();
  const [loading, setLoading] = useState(false);
  const [mode, setMode] = useState("login");

  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      if (mode === "login") {
        const response = await loginApi(values);
        const token = response?.accessToken || response?.token;
        if (!token) {
          throw new Error("登录响应缺少 accessToken");
        }
        login(token);
        message.success("登录成功");
        return;
      }

      const { email, password } = values;
      await registerApi({ email, password });
      message.success("注册成功，请使用新账号登录");
      setMode("login");
      form.resetFields(["password", "confirmPassword"]);
    } catch (error) {
      let msg = error.message || (isLoginMode ? "登录失败" : "注册失败");
      if (isLoginMode && error.status === 401) {
        msg = "邮箱或密码错误，请重试";
      }
      message.error(msg);
      if (isLoginMode) {
        form.resetFields(["password"]);
      }
    } finally {
      setLoading(false);
    }
  };

  const isLoginMode = mode === "login";

  const toggleMode = () => {
    const nextMode = isLoginMode ? "register" : "login";
    setMode(nextMode);
    form.resetFields();
  };

  return (
    <div className="login-container">
      <Card style={{ maxWidth: 420, margin: "0 auto" }}>
        <Title level={3} style={{ textAlign: "center" }}>
          Autonomous Delivery Platform
        </Title>
        <Form
          layout="vertical"
          form={form}
          onFinish={handleSubmit}
          requiredMark={false}
        >
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: "Please enter email" },
              { type: "email", message: "Invalid email format" },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="operator@example.com"
              disabled={loading}
            />
          </Form.Item>
          <Form.Item
            name="password"
            label="Password"
            rules={[
              { required: true, message: "Please enter password" },
              {
                min: 6,
                message: "Password must be at least 6 characters",
              },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Enter password"
              disabled={loading}
            />
          </Form.Item>
          {!isLoginMode && (
            <Form.Item
              name="confirmPassword"
              label="Confirm Password"
              dependencies={["password"]}
              rules={[
                { required: true, message: "Please confirm password" },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (value && value.length < 6) {
                      return Promise.reject(
                        new Error("Password must be at least 6 characters")
                      );
                    }
                    if (!value || getFieldValue("password") === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      new Error("Passwords do not match")
                    );
                  },
                }),
              ]}
            >
              <Input.Password
                prefix={<LockOutlined />}
                placeholder="Re-enter password"
                disabled={loading}
              />
            </Form.Item>
          )}
          <Form.Item>
            <Button
              loading={loading}
              type="primary"
              htmlType="submit"
              block
              shape="round"
            >
              {isLoginMode ? "Sign in" : "Register"}
            </Button>
          </Form.Item>
        </Form>
        <Button type="link" block onClick={toggleMode} disabled={loading}>
          {isLoginMode
            ? "Need an account? Create one"
            : "Already have an account? Sign in"}
        </Button>
      </Card>
    </div>
  );
};

export default LoginPage;
