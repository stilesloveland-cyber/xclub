plugins {
    // 【核心修改】改用别名引用，让它继承根目录和 toml 中定义的统一 Kotlin 版本
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

application {
    mainClass.set("com.xclub.sync.ApplicationKt")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:3.0.3")
    implementation("io.ktor:ktor-server-netty-jvm:3.0.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.3")
    implementation("io.ktor:ktor-server-auth-jvm:3.0.3")
    implementation("io.ktor:ktor-server-cors-jvm:3.0.3")
    implementation("io.ktor:ktor-server-status-pages-jvm:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
}

tasks.jar {
    manifest { 
        attributes["Main-Class"] = "com.xclub.sync.ApplicationKt" 
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // 兼容新版 Gradle 的胖胖包（Fat JAR）打包逻辑
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
