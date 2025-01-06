# 阶段一 构建 naive image 可执行文件
FROM ghcr.io/graalvm/native-image-community:23 AS builder

# 安装 Maven
RUN curl -fsSL https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz | tar -xz -C /opt/ && \
    ln -s /opt/apache-maven-3.9.9 /opt/maven
# 设置maven环境变量
ENV M2_HOME=/opt/maven
ENV PATH=$PATH:$M2_HOME/bin

# 打包 native image
# 复制代码
WORKDIR /app
COPY ../pom.xml .
COPY ../src ./src
# 打包
RUN mvn clean package -DskipTests
# aot
RUN mvn spring-boot:process-aot
# native image
RUN mvn native:compile

# 阶段二 构建运行镜像
# https://github.com/quarkusio/quarkus/issues/32045
FROM seepine/alpine-glibc

# 把打包好的可执行文件复制过来
WORKDIR /app
COPY --from=builder /app/target/tiny-url-app-backend .

# 暴露应用 8080 端口
EXPOSE 8080

# 启动命令，必须指定外部配置文件
CMD ["sh", "-c", "./tiny-url-app-backend --spring.config.location=/config/"]
