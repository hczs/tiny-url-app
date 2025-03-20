# 短网址生成器（Tiny Url App）
![cheng-tiny-url-web-app](https://github.com/user-attachments/assets/d26d18ca-5608-4dd2-a021-9d52617583c7)
## 主要特性
- 提供两种网址缩短方案，可根据需要自行选择：
  - 分布式唯一ID + Base62：短编码最长到 9 位，常规长度在 8 位，优势生成简单快速，劣势就是短编码 8 位，会稍微长一点
  - MurMurHash3 32 位 + Base62：短编码长度稳定在 6 位，优势是短编码足够短，但是需要额外操作，使用布隆过滤器处理哈希碰撞问题
- 提供了一套简单易用的 UI 界面
- 部署简单：后端部署可以直接 Docker 一键部署启动，前端可以直接 Vercel 部署，如果有自己域名可以配置，还免去了申请 SSL 证书、网站备案等繁琐操作
- 性能强劲：使用 GraaLVM Native Image 打包方式，项目启动时间仅 0.29 秒；使用 Spring WebFlux 框架，应用有更优秀的并发性能
## 体验站点
- Vercel 站点：https://tiny-url-app.vercel.app/
- 国内站点：http://101.126.68.192:31006/
## 程序部署
### 后端部署
环境说明：
- Docker: Docker version 25.0.5
- MySQL: MySQL 8.4.3 并自行创建 tiny_url 库，执行初始化脚本 tiny-url-app-backend/src/main/resources/sql/t_url_mapping.sql
- Redis: v7.4.1

在服务器创建 tiny-url-app 文件夹，建立 config 目录，在 config 目录中创建 application.yml 和 application-prod.yml 文件，写入以下内容，并根据实际情况配置 MySQL 和 Redis 的地址及端口

application.yml
```yml
spring:
  # app name
  application:
    name: tiny-url-app-backend

  profiles:
    active: dev
```
application-prod.yml
```yml
spring:
  # database info
  r2dbc:
    url: r2dbc:mysql://127.0.0.1:3306/tiny_url?useSSL=false
    username: root
    password: xxx
    pool:
      initial-size: 5
      max-size: 20
      max-idle-time: 30m

  # redis
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: xxx

tiny-url-app:
  # reference fun.powercheng.url.tiny.enums.ShortenerTypeEnum
  shortener-type: MURMUR32_WITH_BASE62
  # 如果用 UNIQUE_ID_WITH_BASE62 必须配置 worker id
  worker-id: 1
  # global cache config
  cache-config:
    cache-duration: 3
    duration-unit: hours
```
在 config 同级目录下运行下面的命令，启动后端服务
```shell
docker run -d -p 8080:8080  -v ./config:/config -v ./log:/app/log hsunnyc/tiny-url-app-backend
```
### 前端部署
首先 fork 本项目

访问 Vercel 平台：https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme

登录自己的 GitHub 账号后，可选择自己的仓库，点击 import
![image](https://github.com/user-attachments/assets/c4197f47-f4ff-4467-923b-69d17219f847)
选择 Next.js 框架，选择项目的 frontend 目录，点击 Deploy 即可开始部署
![image](https://github.com/user-attachments/assets/38b65488-c4eb-4fab-83ec-2d104fd9e9be)
部署完成后，在 Vercel 的项目主界面中，点击 Settings 菜单，设置后端 API 的地址：
- key 是 NEXT_PUBLIC_API_PROXY_URL
- Value 是你的后端地址，例如 http://x.x.x.x:8080
![image](https://github.com/user-attachments/assets/f09df23d-9ac6-41ec-b962-1082f0a46c88)
至此，项目部署完成



