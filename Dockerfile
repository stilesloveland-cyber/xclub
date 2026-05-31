# 阶段 1：构建 JAR
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# 复制 Gradle 相关文件（利用缓存）
COPY gradle /build/gradle
COPY gradlew /build/
COPY gradle.properties /build/
COPY settings.gradle.kts /build/
COPY build.gradle.kts /build/
COPY gradle/libs.versions.toml /build/gradle/

# 复制 sync-server 源码
COPY sync-server /build/sync-server

# 构建 JAR
RUN chmod +x gradlew && ./gradlew :sync-server:shadowJar -x test

# 阶段 2：运行镜像
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/sync-server/build/libs/sync-server-*-all.jar app.jar
EXPOSE 5555
ENTRYPOINT ["java", "-jar", "app.jar"]
