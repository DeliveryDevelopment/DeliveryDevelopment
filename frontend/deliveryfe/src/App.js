// src/App.js
import React, { useMemo, useState } from "react";
import { Layout, Dropdown, Menu, Button, Tag } from "antd";
import { UserOutlined } from "@ant-design/icons";
import LoginPage from "./components/LoginPage";
import UserPage from "./components/UserPage"; // or "./components/UserPage" based on your structure
import { useAuth } from "./context/AuthContext";
import "./App.css";

const { Header, Content } = Layout;

/**
 * AppShell
 *
 * Top-level application shell:
 *  - Shows LoginPage when user is not authenticated
 *  - Shows UserPage (customer order history + map) when authenticated
 *  - Provides a global dark-blue header with a logout action
 */
const AppShell = () => {
  const { isAuthed, logout } = useAuth();
  const [pendingCount, setPendingCount] = useState(0); // reserved for future use

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
      // Not logged in -> show login page
      return <LoginPage />;
    }

    // Logged in -> show user main page (order history + map, etc.)
    return <UserPage />;
  };

  return (
    <Layout className="app-layout" style={{ minHeight: "100vh" }}>
      {/* Global dark-blue header */}
      <Header
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          background: "#001529", // deep blue
        }}
      >
        <div className="app-header-title" style={{ color: "#fff" }}>
          Autonomous Delivery – Customer Portal
        </div>

        {isAuthed && (
          <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
            {/* If you later need some pending badge, you can reuse this */}
            {pendingCount > 0 && (
              <Tag color="orange">{pendingCount} pending confirmations</Tag>
            )}

            <Dropdown
              trigger={["click"]}
              overlay={userMenu}
              placement="bottomRight"
            >
              <Button icon={<UserOutlined />} shape="circle" />
            </Dropdown>
          </div>
        )}
      </Header>

      <Content className="app-content" style={{ padding: "16px 24px" }}>
        {renderContent()}
      </Content>
    </Layout>
  );
};

export default AppShell;
