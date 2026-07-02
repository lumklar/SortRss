# 使用 JDK 25 运行环境（请确保镜像可用，若不可用可替换为 eclipse-temurin:21-jre 等）
FROM eclipse-temurin:25-jre

ARG VERSION
ARG DIST_ROOT

# 设置工作目录
WORKDIR /app

# 复制后端 JAR 包
COPY server/build/libs/server-${VERSION}.jar app.jar

# 复制前端静态资源（Wasm 产物）到 ./static 目录
COPY app/webApp/build/dist/${DIST_ROOT} ./static

# 设置默认环境变量（用户启动时可不传，自动使用 ./data/rss.db）
ENV SQLITE_DB_PATH=/data/rss.db

# 暴露 Spring Boot 端口
EXPOSE 80

# 启动 JAR，并指定外部静态资源目录
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=80", "--spring.web.resources.static-locations=file:./static/"]
