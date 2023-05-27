# 推荐系统后端接口文档

请求地址：http://127.0.0.1:8888

## 用户管理

接口列表：
- 登录
- 创建用户
- 更新用户
- 删除用户
- 查找所有用户
- 通过用户名查找用户

### 登录

#### 请求

POST /api/user/login

#### 参数

| 参数名       | 类型     | 描述  |
|-----------|--------|-----|
| username	 | string | 	   |
| password	 | string | 	   |

#### 响应

```
{
    "status": "SUCCESS",
    "data": {
        "登录状态": "登录成功",
        "用户类型": "管理员"
    }
}
```

### 创建用户

#### 请求

POST /api/user/create

#### 参数

| 参数名             | 类型     | 描述  |
|-----------------|--------|-----|
| username	       | string | 	   |
| password	       | string | 	   |
| createUsername	 | string | 	   |
| createNickname	 | string | 	   |
| createPassword	 | string | 	   |
| createType      | string | 	   |

#### 响应

```
{
    "status": "SUCCESS",
    "data": {
        "id": 10,
        "username": "user9",
        "nickname": "lili",
        "password": "123456",
        "type": "普通用户"
    }
}
```

### 更新用户

#### 请求

POST /api/user/update

#### 参数

| 参数名             | 类型     | 描述  |
|-----------------|--------|-----|
| username	       | string | 	   |
| password	       | string | 	   |
| updateUsername	 | string | 	   |
| updateNickname	 | string | 	   |
| updatePassword	 | string | 	   |
| updateType      | string | 	   |

#### 响应

```
{
    "status": "SUCCESS",
    "data": {
        "id": 4,
        "username": "user3",
        "nickname": "lilie",
        "password": "123456",
        "type": "普通用户"
    }
}
```

### 删除用户

#### 请求

POST /api/user/delete

#### 参数

| 参数名       | 类型      | 描述  |
|-----------|---------|-----|
| username	 | string  | 	   |
| password	 | string  | 	   |
| deleteId	 | integer | 	   |

#### 响应

```
{
    "status": "SUCCESS",
    "data": "删除成功"
}
```

### 查找所有用户

#### 请求

POST /api/user/getAll

#### 参数

无

#### 响应

```
{
    "status": "SUCCESS",
    "data": [
        {
            "id": 1,
            "username": "admin",
            "nickname": "admin",
            "password": "123456",
            "type": "管理员"
        },
        {
            "id": 2,
            "username": "user1",
            "nickname": "lele",
            "password": "123456",
            "type": "普通用户"
        }
    ]
}
```

### 通过用户名查找用户

#### 请求

POST /api/user/getUser

#### 参数

| 参数名       | 类型     | 描述  |
|-----------|--------|-----|
| username	 | string | 	   |

#### 响应

```
{
    "status": "SUCCESS",
    "data": {
        "id": 1,
        "username": "admin",
        "nickname": "admin",
        "password": "123456",
        "type": "管理员"
    }
}
```

## 电影评分

接口列表：
- 搜索电影（关键字）
- 搜索电影（全称）
- 查找所有电影
- 获取所有用户对某一部电影的评分
- 获取某一用户的所有评分记录
- 添加/修改评分
- 删除评分