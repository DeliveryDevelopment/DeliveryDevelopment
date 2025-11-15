import React, { useMemo, useState } from "react";
import { Layout, Dropdown, Menu, Button, Tag } from "antd";
import { UserOutlined } from "@ant-design/icons";
import LoginPage from "./components/LoginPage";
import DashboardLayout from "./components/dashboard/DashboardLayout";
import { useAuth } from "./context/AuthContext";
import "./App.css";

const { Header, Content } = Layout;

const AppShell = () => {
  const { isAuthed, logout } = useAuth();
  const [pendingCount, setPendingCount] = useState(0);

  const userMenu = useMemo(
    () => (
      <Menu>
        <Menu.Item key="logout" onClick={logout}>
          Sign out
        </Menu.Item>
      </Menu>
    ),
    [logout]
  );

  const renderContent = () => {
    if (!isAuthed) {
      return <LoginPage />;
    }

    return (
      <DashboardLayout
        pendingCount={pendingCount}
        onPendingCountChange={setPendingCount}
      />
    );
  };

  return (
    <Layout className="app-layout">
      <Header style={{ display: "flex", justifyContent: "space-between" }}>
        <div className="app-header-title">Autonomous Delivery Control</div>
        {isAuthed && (
          <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
            {pendingCount > 0 && (
              <Tag color="orange">{pendingCount} pending confirmations</Tag>
            )}
            <Dropdown trigger={["click"]} overlay={userMenu} placement="bottomRight">
              <Button icon={<UserOutlined />} shape="circle" />
            </Dropdown>
          </div>
        )}
      </Header>
      <Content className="app-content">{renderContent()}</Content>
    </Layout>
  );
};

export default AppShell;
