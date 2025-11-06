# DeliveryDevelopment 团队权限与工作流程规范  
**Team Permission & Workflow Guidelines**

---

## 1️⃣ 团队目标 / Team Objective
本仓库用于 DeliveryDevelopment 团队的联合开发与版本管理。  
我们采用标准的 Git 分支模型（`main` / `develop` / `feature/*`），  
通过 GitHub 协作完成前端与后端的并行开发。

> This repository serves as the collaborative workspace for the DeliveryDevelopment team,  
> following a standard Git branching model to ensure efficient development and version control.

---

## 2️⃣ 仓库分支结构 / Branch Structure

| 分支 | 功能说明 | 访问级别 | 合并要求 |
|------|-----------|-----------|-----------|
| `main` | 主分支，用于发布正式版本 | 管理员(Admin)专用 | 仅管理员审核并合并 |
| `develop` | 开发整合分支，用于测试合并 | 管理员/Maintainer 审核 | 需至少 1 次代码审查 |
| `feature/*` | 功能开发分支，每人独立使用 | 所有开发者可创建 | 由 PR 合并至 develop |

> **Rule:**  
> - No one pushes directly to `main` or `develop`.  
> - All updates go through Pull Requests (PRs).  
> - Code is reviewed before merging.

---

## 3️⃣ 成员角色与权限 / Team Roles & Permissions

| 成员角色 | GitHub 权限 | 职责描述 | Responsibilities |
|-----------|--------------|-----------|-------------------|
| 🧠 **项目负责人（你）** | **Admin** | 管理仓库设置、分支保护、合并 main | Manage repo settings, enforce rules, and approve final merges |
| 🧩 **技术负责人 / 审查者** | **Maintain** | 负责审查 PR、合并 develop | Review pull requests and merge tested code into develop |
| 👩‍💻 **普通开发者** | **Write** | 在 feature 分支上开发并发起 PR | Work on feature branches and submit PRs for review |
| 👀 **设计 / 测试 / 产品成员** | **Read** | 查看代码、提交 Issue、评论 | View code, report issues, and discuss in PR comments |

---

## 4️⃣ Git 工作流程 / Git Workflow

### 🧱 1. 从 develop 拉取最新代码  
```bash
git checkout develop
git pull origin develop
```

### 🌳 2. 创建自己的功能分支  
```bash
git checkout -b feature/your-feature-name
```

### ✍️ 3. 提交更改  
```bash
git add .
git commit -m "Implement: feature description"
git push origin feature/your-feature-name
```

### 🔄 4. 在 GitHub 上发起 Pull Request (PR)  
- 从 `feature/xxx` → 合并到 `develop`
- 指定 1 名 Reviewer（推荐 Maintainer）
- 通过代码审查后再合并

### 🚀 5. 测试完成后  
- 管理员将 `develop` 合并入 `main`  
- 同步发布新版本

---

## 5️⃣ 审核与合并规则 / Review & Merge Rules

| 项目 | 要求 | 说明 |
|------|------|------|
| Require pull request before merging | ✅ | 所有改动必须通过 PR |
| Require 1 approval | ✅ | 至少一名 Reviewer 审查 |
| Dismiss stale pull request approvals | ✅ | 新提交会重置旧的审核状态 |
| Require linear history | ✅ | 禁止 merge commit，保持历史整洁 |
| Allow force pushes / deletions | ❌ | 禁止强推和删除 main/develop |
| Restrict who can push to main | ✅ | 仅管理员可推送 |

---

## 6️⃣ PR 命名与提交规范 / PR Naming & Commit Rules

| 类型 | 提交信息格式 | 示例 |
|------|---------------|------|
| 新功能 | `feat: add user login page` | ✅ |
| 修复 | `fix: resolve API timeout bug` | ✅ |
| 重构 | `refactor: improve backend router` | ✅ |
| 文档 | `docs: update API reference` | ✅ |

> 💡 每个 commit 信息都应清晰描述更改内容。

---

## 7️⃣ 日常协作建议 / Collaboration Tips
- 每天上班前 `git pull origin develop` 更新本地版本。  
- 不要直接修改 `main`。  
- 每次提交前确保代码通过本地测试。  
- 合并冲突时先在本地解决再重新发起 PR。  
- 大的改动请先在团队讨论区或微信组确认方案。

---

## 8️⃣ 权限变更 / Access Management
| 角色变更 | 操作人 | 备注 |
|-----------|---------|------|
| 提升至 Maintainer | 项目负责人 | 经团队一致认可 |
| 降级为 Write | 项目负责人 | 若违反合并规范或误操作 |
| 移除成员 | 项目负责人 | 长期不参与开发者 |

---

## 9️⃣ 联系方式 / Communication
📍 **主要沟通渠道**：GitHub Issues + 微信组  
📧 **代码审查负责人**：你（Admin）  
💬 **提交格式讨论区**：`/docs/COMMITS_GUIDE.md`（可另建）

---

## 🔚 结语 / Final Note
> 团队合作的基础是 **纪律与透明**。  
> 每次提交、每个 PR、每次合并，  
> 都是团队信任的体现。  
> 保持分支清晰、提交规范、沟通充分，  
> 我们的项目就会稳定且高效地前进 🚀

