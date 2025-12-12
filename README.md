# 智能打卡系统 (SailTrack)
<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![Status](https://img.shields.io/badge/Status-开发中-red.svg)

**一个基于 Spring Boot 4.0 和 Vue.js 3.x 的现代化企业考勤管理平台**

[功能特性](#功能特性) • [技术架构](#技术架构) • [快速开始](#快速开始) • [API 文档](#api-文档) • [项目文档](#项目文档)

</div>
---

## 📋 项目简介

智能打卡系统（SailTrack）是一个功能完善的企业考勤管理解决方案，采用前后端分离架构，提供高效、准确、便捷的考勤管理服务。系统支持多种打卡方式、智能异常检测、数据统计分析等功能。

### 🎯 项目目标

- ✅ 提供便捷的员工考勤打卡功能
- ✅ 实现智能化的考勤数据分析
- ✅ 支持灵活的请假审批流程
- ✅ 提供完善的考勤报表统计

### 📊 当前状态

- ✅ **用户认证模块**: 已完成（注册、登录、邮箱验证）
- ✅ **数据库设计**: 已完成（13 个表的完整设计）
- ✅ **基础配置**: 已完成（CORS、JWT、BCrypt、邮件服务）
- 🚧 **考勤功能**: 开发中
- 📋 **前端界面**: 待开发

---

## ✨ 功能特性

### 已实现功能

#### 🔐 用户认证模块
- **用户注册**: 支持邮箱验证码验证
- **用户登录**: JWT Token 身份认证
- **密码安全**: BCrypt 加密存储
- **邮箱验证**: 4 位随机验证码

### 规划功能

#### 👥 用户管理
- 员工信息管理
- 角色权限管理（RBAC）
- 部门组织架构
- 账户安全设置

#### ⏰ 智能打卡
- 多种打卡方式（人脸识别、指纹、二维码、GPS）
- 打卡记录实时同步
- 异常打卡预警
- GPS 定位验证

#### 📈 考勤统计
- 考勤数据自动统计
- 出勤率分析
- 迟到早退记录
- 加班时间计算

#### 📝 请假管理
- 请假申请流程
- 多种请假类型（年假、病假、事假等）
- 审批流程配置
- 请假记录查询

#### 📊 报表分析
- 考勤月报生成
- 数据可视化图表
- 导出 Excel 报表
- 考勤趋势分析

#### ⚙️ 系统设置
- 工作日历配置
- 打卡规则设置
- 系统参数管理
- 操作日志监控

---

## 🛠️ 技术架构

### 后端技术栈

| 技术              | 版本     | 说明     |
|-----------------|--------|--------|
| Spring Boot     | 4.0.0  | 核心框架   |
| Java            | 17     | 开发语言   |
| MySQL           | 8.0+   | 关系型数据库 |
| Spring Data JPA | 4.0.0  | ORM 框架 |
| JWT             | 0.11.5 | 身份认证   |
| Lombok          | Latest | 代码简化   |
| Spring Mail     | 4.0.0  | 邮件服务   |
| BCrypt          | Latest | 密码加密   |
| Maven           | 3.6+   | 构建工具   |

### 前端技术栈（规划）

| 技术           | 版本     | 说明       |
|--------------|--------|----------|
| Vue.js       | 3.x    | 前端框架     |
| Element Plus | Latest | UI 组件库   |
| Vuex         | 4.x    | 状态管理     |
| Vue Router   | 4.x    | 路由管理     |
| Axios        | Latest | HTTP 客户端 |
| Vite         | Latest | 构建工具     |

### 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                      前端层 (Vue.js)                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐│
│  │ 用户管理 │  │ 考勤打卡 │  │ 请假审批 │  │ 报表统计 ││
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘│
└─────────────────────────────────────────────────────────┘
                            ↕ HTTP/HTTPS
┌─────────────────────────────────────────────────────────┐
│                   后端层 (Spring Boot)                   │
│  ┌──────────────────────────────────────────────────┐  │
│  │              Controller 层（RESTful API）         │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │              Service 层（业务逻辑）               │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │         Repository 层（数据访问 JPA）             │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↕ JDBC
┌─────────────────────────────────────────────────────────┐
│                    数据层 (MySQL 8.0)                    │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐      │
│  │  用户表  │ │ 考勤表  │ │ 请假表  │ │ 统计表  │ ...  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘      │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6 或更高版本
- **MySQL**: 8.0 或更高版本
- **IDE**: IntelliJ IDEA / Eclipse（推荐 IntelliJ IDEA）

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://gitee.com/miku-0410/zhineng_daka.git
cd zhineng_daka
```

#### 2. 创建数据库

```sql
CREATE DATABASE sailtrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 配置数据库

编辑 `backend/src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/sailtrack?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=your_password

# 邮件配置（163 邮箱）
spring.mail.host=smtp.163.com
spring.mail.port=465
spring.mail.username=your_email@163.com
spring.mail.password=your_auth_code
```

#### 4. 构建项目

```bash
cd backend
mvn clean install
```

#### 5. 运行项目

```bash
# 方式 1: 使用 Maven（开发环境推荐）
mvn spring-boot:run

# 方式 2: 运行 JAR 包
java -jar target/SailTrack-0.0.1-SNAPSHOT.jar
```

#### 6. 访问应用

- **后端 API**: http://localhost:8080
- **API 基础路径**: `/api/auth`

---

## 📡 API 文档

### 认证接口

#### 发送验证码

```http
POST /api/auth/send-captcha?email=test@example.com
```

**响应示例**:
```json
{
  "ok": true,
  "message": "验证码已发送"
}
```

#### 用户注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "captcha": "1234"
}
```

**响应示例**:
```json
{
  "ok": true,
  "code": 400,
  "userId": 1
}
```

#### 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "ok": true,
  "code": 200,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 📁 项目结构

```
zhineng_daka/
├── backend/                           # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sailtrack/backend/
│   │   │   │   ├── BackendApplication.java      # 主启动类
│   │   │   │   ├── WebConfig.java               # CORS 配置
│   │   │   │   ├── controller/                  # 控制器层
│   │   │   │   │   ├── AuthController.java      # 认证控制器
│   │   │   │   │   └── TestController.java      # 测试控制器
│   │   │   │   ├── service/                     # 服务层
│   │   │   │   │   ├── AuthService.java         # 认证服务
│   │   │   │   │   └── MailService.java         # 邮件服务
│   │   │   │   ├── repository/                  # 数据访问层
│   │   │   │   │   └── UserRepository.java      # 用户仓库
│   │   │   │   ├── entity/                      # 实体类
│   │   │   │   │   └── User.java                # 用户实体
│   │   │   │   ├── dto/                         # 数据传输对象
│   │   │   │   │   ├── LoginRequest.java        # 登录请求
│   │   │   │   │   └── RegisterRequest.java     # 注册请求
│   │   │   │   ├── util/                        # 工具类
│   │   │   │   │   └── JwtUtil.java             # JWT 工具
│   │   │   │   ├── cache/                       # 缓存
│   │   │   │   │   └── CaptchaCache.java        # 验证码缓存
│   │   │   │   └── exception/                   # 异常处理
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   └── resources/
│   │   │       └── application.properties       # 配置文件
│   │   └── test/                                # 测试代码
│   └── pom.xml                                  # Maven 配置
├── word/                                        # 项目文档
│   ├── 智能打卡系统项目概述.md                   # 项目概述
│   ├── 数据库设计文档.md                        # 数据库设计
│   └── 项目结构图.md                            # 项目结构图
├── IFLOW.md                                     # iFlow 上下文文档
└── README.md                                    # 项目说明文档
```

---

## 📚 项目文档

- **[项目概述](word/智能打卡系统项目概述.md)**: 详细的功能规划和架构设计
- **[数据库设计](word/数据库设计文档.md)**: 完整的数据库表结构、字段说明、索引设计
- **[项目结构图](word/项目结构图.md)**: 项目架构和模块关系图
- **[iFlow 上下文](IFLOW.md)**: iFlow AI 开发助手的项目上下文文档

---

## 🗄️ 数据库设计

系统采用 **MVP 设计理念**，共 **5 个核心数据表**：

| 表名 | 说明 | 状态 | 记录数预估 |
|------|------|------|-----------|
| users | 用户表（精简） | ✅ 已实现 | 100-1000 |
| roles | 角色表（3个角色） | ✅ 已设计 | 3（固定） |
| departments | 部门表（扁平化） | ✅ 已设计 | 10-50 |
| attendance_records | 考勤记录表（支持弹性） | ✅ 已设计 | 10万-100万+ |
| leave_records | 请假记录表（简化） | ✅ 已设计 | 1000-1万+ |

### 设计特点

- ✅ **简洁高效**: 从 13 个表精简到 5 个核心表
- ✅ **易于实现**: 降低开发复杂度，快速上线
- ✅ **满足需求**: 覆盖核心考勤功能
- ✅ **可扩展性**: 预留扩展接口

### 主要简化

- 打卡方式：仅点击打卡（移除人脸识别、GPS等）
- 角色管理：移除 HR，保留 ADMIN/MANAGER/EMPLOYEE
- 请假类型：简化为 3 种（事假、病假、年假）
- 部门结构：扁平化，无层级关系
- 延后功能：加班、统计、通知、日志等

详细设计请查看 [数据库设计文档](word/数据库设计文档.md)。

---

## 🔧 开发规范

### 代码结构约定

- **Controller 层**: 处理 HTTP 请求，使用 `@RestController` 和 `@RequestMapping`
- **Service 层**: 业务逻辑实现，使用 `@Service` 和 `@Transactional`
- **Repository 层**: 数据访问，继承 `JpaRepository`
- **Entity 层**: 数据库实体映射，使用 `@Entity` 和 `@Table`
- **DTO 层**: 数据传输对象，使用 Jakarta Validation 注解验证

### 命名规范

- **类名**: 大驼峰命名法 (PascalCase)
  - Controller: `XxxController`
  - Service: `XxxService`
  - Repository: `XxxRepository`
  - Entity: 业务名称（如 `User`）
  - DTO: `XxxRequest`, `XxxResponse`

- **方法名**: 小驼峰命名法 (camelCase)
  - 查询: `findXxx`, `getXxx`
  - 保存: `save`, `create`
  - 更新: `update`
  - 删除: `delete`

### API 设计规范

- **RESTful API 风格**
- **统一响应格式**:
  ```json
  {
    "ok": true,
    "code": 200,
    "data": {},
    "message": "成功"
  }
  ```

---

## 📈 开发计划

### 第一阶段 - 基础功能 ✅ 进行中

- [x] 数据库表结构设计
- [x] 完整数据库设计文档（13 个表）
- [x] 用户实体和认证功能
- [x] 邮箱验证码功能
- [x] JWT 认证实现
- [x] 全局异常处理
- [x] CORS 跨域配置
- [ ] 用户表扩展字段实现
- [ ] 考勤记录实体实现
- [ ] 基础打卡功能实现

### 第二阶段 - 高级功能

- [ ] Redis 缓存集成
- [ ] 人脸识别集成
- [ ] 请假审批流程
- [ ] 报表统计分析
- [ ] Swagger API 文档

### 第三阶段 - 智能化

- [ ] AI 异常检测
- [ ] 数据预测分析
- [ ] 移动端适配
- [ ] 性能优化
- [ ] 负载均衡

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📝 常见问题

### 1. 如何修改数据库配置？
编辑 `backend/src/main/resources/application.properties` 文件中的数据库连接信息。

### 2. 如何配置邮箱服务？
使用 163 邮箱需要开启 SMTP 服务并获取授权码，然后在 `application.properties` 中配置。

### 3. JWT Token 如何使用？
登录成功后，将返回的 token 放在请求头中：
```
Authorization: Bearer <token>
```

### 4. 如何配置跨域访问？
在 `WebConfig.java` 中的 `setAllowedOrigins` 方法中添加允许的源地址。

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👥 联系方式

- **项目地址**: https://gitee.com/miku-0410/zhineng_daka
- **问题反馈**: [Issues](https://gitee.com/miku-0410/zhineng_daka/issues)

---

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JWT](https://jwt.io/)
- [Lombok](https://projectlombok.org/)
- [MySQL](https://www.mysql.com/)

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给它一个 Star！**

Made with ❤️ by SailTrack Team

</div>
