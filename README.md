# mm-family 微信小程序后端服务

基于 Spring Boot 2.6.3 + weixin-java-miniapp SDK 构建的微信小程序后端。

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.6.3 | 基础框架 |
| weixin-java-miniapp | 4.8.0 | 微信小程序 SDK |
| Java | 1.8 | 运行环境 |
| Thymeleaf | — | 模板引擎 |
| Lombok | — | 简化样板代码 |
| commons-fileupload | 1.6.0 | 文件上传 |
| Jedis | — | Redis 客户端（可选） |

---

## 功能目录

### 1. 用户认证 `/wx/user/{appid}`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/wx/user/{appid}/login` | GET | 小程序登录，用 `code` 换取 `sessionKey` 和 `openid` |
| `/wx/user/{appid}/info` | GET | 获取并解密用户基本信息（昵称、头像、性别等） |
| `/wx/user/{appid}/phone` | GET | 获取并解密用户绑定手机号 |

### 2. 消息推送 `/wx/portal/{appid}`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/wx/portal/{appid}` | GET | 微信服务器签名验证（配置服务器时使用） |
| `/wx/portal/{appid}` | POST | 接收微信推送消息，支持 JSON/XML、明文/AES 加密 |

消息路由规则：

- 内容含「订阅消息」→ 发送订阅模板消息
- 内容含「文本」→ 回复文本客服消息
- 内容含「图片」→ 上传图片并通过客服消息发送
- 内容含「二维码」→ 生成小程序码并发送
- 其他所有消息 → 记录日志

### 3. 素材管理 `/wx/media/{appid}`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/wx/media/{appid}/upload` | POST | 上传临时素材到微信服务器，返回 `media_id` |
| `/wx/media/{appid}/download/{mediaId}` | GET | 下载临时素材文件 |

---

## 部署配置

### 环境变量

| 变量名 | 必填 | 说明 |
|--------|------|------|
| `WX_APPID` | 是 | 微信小程序 AppID |
| `WX_SECRET` | 是 | 微信小程序 Secret |
| `WX_TOKEN` | 否 | 消息服务器 Token |
| `WX_AES_KEY` | 否 | 消息 AES 加密密钥 |

### Docker 构建

```bash
docker build -t mm-family .
docker run -p 8080:8080 \
  -e WX_APPID=your_appid \
  -e WX_SECRET=your_secret \
  mm-family
```

---

## 项目结构

```
src/main/java/.../
├── WxMaDemoApplication.java       # 启动类
├── config/
│   ├── WxMaConfiguration.java     # SDK 初始化、消息路由配置
│   └── WxMaProperties.java        # 配置属性绑定
├── controller/
│   ├── WxPortalController.java    # 消息入口
│   ├── WxMaUserController.java    # 用户接口
│   └── WxMaMediaController.java   # 素材接口
├── error/
│   ├── ErrorController.java       # 错误页面
│   └── ErrorPageConfiguration.java
└── utils/
    └── JsonUtils.java             # JSON 工具
```
