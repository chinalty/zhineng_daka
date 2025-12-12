# 智能打卡系统 API 文档

## 概述

本文档描述了智能打卡系统的 RESTful API 接口，包括认证、考勤、请假和用户管理等功能。

### 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: Bearer Token（JWT）

### 通用响应格式

#### 成功响应
```json
{
  "ok": true,
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

#### 失败响应
```json
{
  "ok": false,
  "code": 400,
  "message": "错误信息"
}
```

---

## 响应码说明

| 响应码 | 说明    | 含义             |
|-----|-------|----------------|
| 200 | 成功    | 请求处理成功         |
| 400 | 客户端错误 | 请求参数错误或业务逻辑错误  |
| 401 | 未认证   | 需要登录或 token 无效 |
| 403 | 无权限   | 没有访问权限         |
| 404 | 未找到   | 资源不存在          |
| 500 | 服务器错误 | 服务器内部错误        |

---

## 1. 认证模块 (/api/auth)

### 1.1 发送邮箱验证码

**接口地址**: `POST /api/auth/send-captcha`

**请求参数**:
- `email` (query, string): 邮箱地址

**请求示例**:
```
POST /api/auth/send-captcha?email=test@example.com
```

**成功响应**:
```json
{
  "ok": true,
  "message": "验证码已发送"
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "邮箱格式错误"
}
```

---

### 1.2 用户注册

**接口地址**: `POST /api/auth/register`

**请求体**:
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "captcha": "1234"
}
```

**字段说明**:
- `username` (string, 必填): 用户名，3-30字符
- `password` (string, 必填): 密码，至少6字符
- `email` (string, 必填): 邮箱地址
- `captcha` (string, 必填): 4位验证码

**成功响应**:
```json
{
  "ok": true,
  "code": 200,
  "userId": 1
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "用户名已存在"
}
```

**可能的错误信息**:
- "用户名已存在"
- "邮箱已使用"
- "验证码错误"
- "验证码已过期"

---

### 1.3 用户登录

**接口地址**: `POST /api/auth/login`

**请求体**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**字段说明**:
- `username` (string, 必填): 用户名或邮箱
- `password` (string, 必填): 密码

**成功响应**:
```json
{
  "ok": true,
  "code": 200,
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "用户名或密码错误"
}
```

---

## 2. 考勤模块 (/api/attendance)

> **注意**: 考勤模块的所有接口都需要在请求头中携带 JWT Token
> 
> `Authorization: Bearer {token}`

### 2.1 打卡（签到/签退）

**接口地址**: `POST /api/attendance/check`

**请求体**:
```json
{
  "type": 1
}
```

**字段说明**:
- `type` (integer, 必填): 打卡类型
  - 1: 签到
  - 2: 签退

**成功响应（签到）**:
```json
{
  "ok": true,
  "message": "签到成功",
  "data": {
    "id": 1,
    "userId": 1,
    "attendanceDate": "2025-12-12",
    "checkInTime": "2025-12-12T09:30:00",
    "checkInIp": "192.168.1.100",
    "expectedCheckOutTime": "2025-12-12T17:30:00",
    "status": 1,
    "isLate": false,
    "lateMinutes": 0
  }
}
```

**成功响应（签退）**:
```json
{
  "ok": true,
  "message": "签退成功",
  "data": {
    "id": 1,
    "userId": 1,
    "attendanceDate": "2025-12-12",
    "checkInTime": "2025-12-12T09:30:00",
    "checkOutTime": "2025-12-12T17:45:00",
    "checkOutIp": "192.168.1.100",
    "workHours": 8.25,
    "status": 1,
    "isEarlyLeave": false,
    "earlyLeaveMinutes": 0
  }
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "今日已签到"
}
```

**可能的错误信息**:
- "今日已签到"
- "今日已签退"
- "请先签到"
- "无效的打卡类型"

---

### 2.2 获取今日考勤记录

**接口地址**: `GET /api/attendance/today`

**成功响应**:
```json
{
  "ok": true,
  "data": {
    "id": 1,
    "userId": 1,
    "attendanceDate": "2025-12-12",
    "checkInTime": "2025-12-12T09:30:00",
    "checkOutTime": "2025-12-12T17:45:00",
    "workHours": 8.25,
    "status": 1,
    "isLate": false,
    "isEarlyLeave": false,
    "lateMinutes": 0,
    "earlyLeaveMinutes": 0
  }
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "今日无考勤记录"
}
```

---

## 3. 请假模块 (/api/leave)

> **注意**: 请假模块的所有接口都需要在请求头中携带 JWT Token

### 3.1 申请请假

**接口地址**: `POST /api/leave/apply`

**请求体**:
```json
{
  "leaveType": "事假",
  "startDate": "2025-12-15",
  "endDate": "2025-12-15",
  "leaveDays": 1,
  "reason": "个人事务"
}
```

**字段说明**:
- `leaveType` (string, 必填): 请假类型
  - "事假"
  - "病假"
  - "年假"
- `startDate` (date, 必填): 开始日期
- `endDate` (date, 必填): 结束日期
- `leaveDays` (number, 必填): 请假天数
- `reason` (string, 必填): 请假原因

**成功响应**:
```json
{
  "ok": true,
  "message": "请假申请提交成功",
  "leaveId": 1
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "请假时间与已有请假记录重叠"
}
```

**可能的错误信息**:
- "请假时间与已有请假记录重叠"
- "请假类型不能为空"
- "请假原因不能为空"

---

### 3.2 审批请假

**接口地址**: `POST /api/leave/approve`

**请求体**:
```json
{
  "leaveId": 1,
  "status": 1,
  "remark": "同意"
}
```

**字段说明**:
- `leaveId` (integer, 必填): 请假记录ID
- `status` (integer, 必填): 审批状态
  - 1: 批准
  - 2: 拒绝
- `remark` (string, 可选): 审批备注

**成功响应**:
```json
{
  "ok": true,
  "message": "请假已批准"
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "无审批权限"
}
```

**可能的错误信息**:
- "请假记录不存在"
- "该请假已处理"
- "无审批权限"

---

### 3.3 获取我的请假记录

**接口地址**: `GET /api/leave/my-records`

**成功响应**:
```json
{
  "ok": true,
  "data": [
    {
      "id": 1,
      "userId": 1,
      "leaveType": "事假",
      "startDate": "2025-12-15",
      "endDate": "2025-12-15",
      "leaveDays": 1,
      "reason": "个人事务",
      "status": 1,
      "approverId": 2,
      "approvalTime": "2025-12-12T10:00:00",
      "approvalRemark": "同意",
      "createdAt": "2025-12-12T09:00:00"
    }
  ]
}
```

---

### 3.4 获取待审批列表

**接口地址**: `GET /api/leave/pending-approvals`

**成功响应**:
```json
{
  "ok": true,
  "data": [
    {
      "id": 2,
      "userId": 3,
      "leaveType": "病假",
      "startDate": "2025-12-16",
      "endDate": "2025-12-17",
      "leaveDays": 2,
      "reason": "身体不适",
      "status": 0,
      "createdAt": "2025-12-12T11:00:00"
    }
  ]
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "无审批权限"
}
```

---

### 3.5 获取请假详情

**接口地址**: `GET /api/leave/{id}`

**路径参数**:
- `id` (integer): 请假记录ID

**成功响应**:
```json
{
  "ok": true,
  "data": {
    "id": 1,
    "userId": 1,
    "leaveType": "事假",
    "startDate": "2025-12-15",
    "endDate": "2025-12-15",
    "leaveDays": 1,
    "reason": "个人事务",
    "status": 1,
    "approverId": 2,
    "approvalTime": "2025-12-12T10:00:00",
    "approvalRemark": "同意",
    "createdAt": "2025-12-12T09:00:00"
  }
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "请假记录不存在"
}
```

---

## 4. 用户模块 (/api/user)

> **注意**: 用户模块的所有接口都需要在请求头中携带 JWT Token

### 4.1 获取用户信息

**接口地址**: `GET /api/user/info`

**成功响应**:
```json
{
  "ok": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "realName": "张三",
    "departmentId": 1,
    "departmentName": "技术部",
    "roleId": 3,
    "roleName": "普通员工",
    "status": 1,
    "statusText": "启用"
  }
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 401,
  "message": "请先登录"
}
```

---

### 4.2 获取部门员工列表

**接口地址**: `GET /api/user/department-users`

**成功响应**:
```json
{
  "ok": true,
  "data": [
    {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "realName": "张三",
      "departmentId": 1,
      "departmentName": "技术部",
      "roleId": 3,
      "roleName": "普通员工",
      "status": 1,
      "statusText": "启用"
    }
  ]
}
```

**失败响应**:
```json
{
  "ok": false,
  "code": 400,
  "message": "无权限查看部门员工"
}
```

---

### 4.3 获取所有部门

**接口地址**: `GET /api/user/departments`

**成功响应**:
```json
{
  "ok": true,
  "data": [
    {
      "id": 1,
      "departmentName": "技术部",
      "managerId": 3,
      "description": "技术研发部门",
      "status": 1,
      "createdAt": "2025-12-12T00:00:00"
    },
    {
      "id": 2,
      "departmentName": "市场部",
      "managerId": 5,
      "description": "市场营销部门",
      "status": 1,
      "createdAt": "2025-12-12T00:00:00"
    }
  ]
}
```

---

### 4.4 获取所有角色

**接口地址**: `GET /api/user/roles`

**成功响应**:
```json
{
  "ok": true,
  "data": [
    {
      "id": 1,
      "roleName": "系统管理员",
      "roleCode": "ADMIN",
      "description": "拥有系统所有权限",
      "status": 1,
      "createdAt": "2025-12-12T00:00:00"
    },
    {
      "id": 2,
      "roleName": "部门经理",
      "roleCode": "MANAGER",
      "description": "管理本部门员工考勤",
      "status": 1,
      "createdAt": "2025-12-12T00:00:00"
    },
    {
      "id": 3,
      "roleName": "普通员工",
      "roleCode": "EMPLOYEE",
      "description": "基本考勤功能",
      "status": 1,
      "createdAt": "2025-12-12T00:00:00"
    }
  ]
}
```

---

## 5. 测试接口 (/api/test)

### 5.1 查看验证码缓存

**接口地址**: `GET /api/test/captcha-cache`

**成功响应**:
```json
{
  "ok": true,
  "data": {
    "test@example.com": "1234"
  },
  "size": 1
}
```

---

### 5.2 测试验证码验证

**接口地址**: `POST /api/test/test-captcha`

**请求参数**:
- `email` (query, string): 邮箱地址
- `code` (query, string): 验证码

**成功响应**:
```json
{
  "ok": true,
  "email": "test@example.com",
  "code": "1234",
  "result": true
}
```

---

## 6. 调试接口 (/api/debug)

> **注意**: 调试接口仅用于开发和测试

### 6.1 手动设置验证码

**接口地址**: `POST /api/debug/set-captcha`

**请求参数**:
- `email` (query, string): 邮箱地址
- `code` (query, string): 验证码

**成功响应**:
```json
{
  "ok": true,
  "message": "验证码已设置",
  "email": "test@example.com",
  "code": "1234"
}
```

---

### 6.2 测试邮件发送

**接口地址**: `POST /api/debug/test-email`

**请求参数**:
- `email` (query, string): 邮箱地址
- `code` (query, string): 验证码

**成功响应**:
```json
{
  "ok": true,
  "message": "邮件发送成功"
}
```

**失败响应**:
```json
{
  "ok": false,
  "message": "邮件发送失败",
  "error": "详细错误信息"
}
```

---

### 6.3 检查验证码缓存

**接口地址**: `GET /api/debug/check-cache`

**请求参数**:
- `email` (query, string): 邮箱地址

**成功响应**:
```json
{
  "ok": true,
  "email": "test@example.com",
  "exists": true,
  "code": "1234"
}
```

---

## 业务规则说明

### 1. 弹性工作制规则

- **签到时间窗口**: 08:00 - 10:00
- **要求工作时长**: 8 小时
- **迟到判定**: 超过 10:00 签到即为迟到
- **早退判定**: 早于预期签退时间即为早退
- **预期签退时间**: 签到时间 + 8 小时

### 2. 请假规则

- **请假类型**: 事假、病假、年假
- **审批权限**: 仅部门经理可审批本部门员工请假
- **时间冲突**: 不允许重叠的请假时间
- **状态流转**: 待审批(0) → 已批准(1) / 已拒绝(2)

### 3. 角色权限

| 角色 | 权限说明 |
|------|---------|
| ADMIN (1) | 系统管理员，拥有所有权限 |
| MANAGER (2) | 部门经理，可管理本部门员工和审批请假 |
| EMPLOYEE (3) | 普通员工，只能查看和操作自己的数据 |

---

## 错误处理

### 常见错误及解决方案

1. **401 未认证**
   - 检查请求头是否携带正确的 Token
   - 检查 Token 是否过期

2. **400 客户端错误**
   - 检查请求参数是否完整
   - 检查参数格式是否正确

3. **403 无权限**
   - 检查用户角色是否有相应权限
   - 确认操作的资源是否属于用户管辖范围

4. **验证码错误**
   - 确认验证码是否在5分钟有效期内
   - 检查邮箱地址是否正确（大小写不敏感）

---

## 更新日志

### v1.0.0 (2025-12-12)
- 初始版本
- 实现认证、考勤、请假、用户管理模块
- 支持弹性工作制
- 完整的错误处理和响应码说明