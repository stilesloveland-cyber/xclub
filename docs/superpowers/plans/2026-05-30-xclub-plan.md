# Xclub 安卓便捷工具 App 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一款集成内嵌网页、记账、知识库笔记和效率工具的安卓便捷工具 App

**Architecture:** 单体模块化架构，Kotlin + Jetpack Compose 原生开发。feature 模块间不互相依赖，通过 core-data 层共享数据。Room + SQLCipher 本地加密存储，可选自建 Ktor 服务端云同步。

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room, SQLCipher, Hilt, Compose Navigation, Retrofit, Kotlinx Serialization, Coroutines + Flow, Ktor (服务端), Caddy, Docker

---

## 文件结构总览

```
xclub/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/xclub/app/
│           ├── XclubApp.kt
│           ├── MainActivity.kt
│           ├── navigation/
│           │   ├── XclubNavHost.kt
│           │   └── TopLevelDestination.kt
│           └── ui/
│               └── XclubApp.kt
├── core/
│   ├── core-common/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/xclub/core/common/
│   │       ├── util/
│   │       │   ├── DateTimeUtils.kt
│   │       │   └── CryptoUtils.kt
│   │       └── result/
│   │           └── Result.kt
│   ├── core-ui/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/xclub/core/ui/
│   │       ├── theme/
│   │       │   ├── Theme.kt
│   │       │   ├── Color.kt
│   │       │   └── Type.kt
│   │       └── component/
│   │           ├── XclubScaffold.kt
│   │           └── XclubTopBar.kt
│   ├── core-data/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/xclub/core/data/
│   │       ├── db/
│   │       │   ├── XclubDatabase.kt
│   │       │   ├── converter/
│   │       │   │   └── Converters.kt
│   │       │   ├── dao/
│   │       │   │   ├── WebBookmarkDao.kt
│   │       │   │   ├── TransactionDao.kt
│   │       │   │   ├── CategoryDao.kt
│   │       │   │   ├── SavingPlanDao.kt
│   │       │   │   ├── SavingRecordDao.kt
│   │       │   │   ├── BillingCycleDao.kt
│   │       │   │   ├── NoteDao.kt
│   │       │   │   ├── NoteTagDao.kt
│   │       │   │   ├── NoteLinkDao.kt
│   │       │   │   ├── TodoDao.kt
│   │       │   │   ├── ReminderDao.kt
│   │       │   │   └── UserConfigDao.kt
│   │       │   └── entity/
│   │       │       ├── WebBookmarkEntity.kt
│   │       │       ├── TransactionEntity.kt
│   │       │       ├── CategoryEntity.kt
│   │       │       ├── SavingPlanEntity.kt
│   │       │       ├── SavingRecordEntity.kt
│   │       │       ├── BillingCycleEntity.kt
│   │       │       ├── NoteEntity.kt
│   │       │       ├── NoteTagEntity.kt
│   │       │       ├── NoteLinkEntity.kt
│   │       │       ├── NoteTagRelationEntity.kt
│   │       │       ├── TodoEntity.kt
│   │       │       ├── ReminderEntity.kt
│   │       │       └── UserConfigEntity.kt
│   │       ├── repository/
│   │       │   ├── WebBookmarkRepository.kt
│   │       │   ├── FinanceRepository.kt
│   │       │   ├── NoteRepository.kt
│   │       │   ├── TodoRepository.kt
│   │       │   └── UserConfigRepository.kt
│   │       └── di/
│   │           └── DataModule.kt
│   └── core-sync/
│       ├── build.gradle.kts
│       └── src/main/java/com/xclub/core/sync/
│           ├── SyncEngine.kt
│           ├── SyncApi.kt
│           ├── SyncModels.kt
│           └── di/
│               └── SyncModule.kt
├── feature/
│   ├── feature-web/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/xclub/feature/web/
│   │       ├── navigation/
│   │       │   └── WebNavigation.kt
│   │       ├── ui/
│   │       │   ├── WebBookmarkListScreen.kt
│   │       │   ├── WebBookmarkAddDialog.kt
│   │       │   └── WebViewScreen.kt
│   │       └── viewmodel/
│   │           └── WebBookmarkViewModel.kt
│   ├── feature-finance/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/xclub/feature/finance/
│   │       ├── navigation/
│   │       │   └── FinanceNavigation.kt
│   │       ├── ui/
│   │       │   ├── FinanceOverviewScreen.kt
│   │       │   ├── AddTransactionScreen.kt
│   │       │   ├── SavingPlanScreen.kt
│   │       │   └── FinanceStatsScreen.kt
│   │       ├── viewmodel/
│   │       │   ├── FinanceViewModel.kt
│   │       │   └── SavingPlanViewModel.kt
│   │       └── util/
│   │           └── BillingCycleCalculator.kt
│   ├── feature-notes/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/xclub/feature/notes/
│   │       ├── navigation/
│   │       │   └── NotesNavigation.kt
│   │       ├── ui/
│   │       │   ├── NoteListScreen.kt
│   │       │   ├── NoteEditScreen.kt
│   │       │   └── KnowledgeGraphScreen.kt
│   │       ├── viewmodel/
│   │       │   └── NoteViewModel.kt
│   │       └── util/
│   │           └── NoteLinkParser.kt
│   └── feature-todo/
│       ├── build.gradle.kts
│       └── src/main/java/com/xclub/feature/todo/
│           ├── navigation/
│           │   └── TodoNavigation.kt
│           ├── ui/
│           │   ├── TodoListScreen.kt
│           │   └── AddTodoScreen.kt
│           └── viewmodel/
│               └── TodoViewModel.kt
└── sync-server/
    ├── Dockerfile
    ├── docker-compose.yml
    ├── Caddyfile
    ├── deploy.sh
    └── src/main/kotlin/com/xclub/sync/
        ├── Application.kt
        ├── routes/
        │   ├── AuthRoutes.kt
        │   ├── SyncRoutes.kt
        │   └── UpdateRoutes.kt
        ├── database/
        │   └── DatabaseFactory.kt
        └── model/
            └── SyncModels.kt
```

---

## Phase 1: 项目基础与 App 壳

### Task 1: 初始化 Gradle 多模块项目

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (root)
- Create: `gradle.properties`
- Create: `gradle/libs.versions.toml`

- [ ] **Step 1: 创建版本目录**

```toml
# gradle/libs.versions.toml
[versions]
agp = "8.7.3"
kotlin = "2.1.0"
ksp = "2.1.0-1.0.29"
compose-bom = "2024.12.01"
hilt = "2.53.1"
hilt-navigation-compose = "1.2.0"
room = "2.6.1"
sqlcipher = "4.6.1"
navigation-compose = "2.8.5"
retrofit = "2.11.0"
okhttp = "4.12.0"
kotlinx-serialization = "1.7.3"
coroutines = "1.9.0"
datastore = "1.1.1"
material3 = "1.3.1"
core-ktx = "1.15.0"
activity-compose = "1.9.3"
lifecycle = "2.8.7"
ktor = "3.0.3"
logback = "1.4.14"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3" }
compose-material-icons = { group = "androidx.compose.material", name = "material-icons-extended" }
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activity-compose" }
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
sqlcipher = { group = "net.zetetic", name = "android-database-sqlcipher", version.ref = "sqlcipher" }
sqlite-framework = { group = "androidx.sqlite", name = "sqlite-framework", version = "2.4.0" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt-navigation-compose" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

- [ ] **Step 2: 创建根 build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
}
```

- [ ] **Step 3: 创建 settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "xclub"

include(":app")
include(":core:core-common")
include(":core:core-ui")
include(":core:core-data")
include(":core:core-sync")
include(":feature:feature-web")
include(":feature:feature-finance")
include(":feature:feature-notes")
include(":feature:feature-todo")
```

- [ ] **Step 4: 创建 gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 5: 提交**

```bash
git add settings.gradle.kts build.gradle.kts gradle.properties gradle/libs.versions.toml
git commit -m "chore: initialize Gradle multi-module project with version catalog"
```

---

### Task 2: 创建 core-common 模块

**Files:**
- Create: `core/core-common/build.gradle.kts`
- Create: `core/core-common/src/main/AndroidManifest.xml`
- Create: `core/core-common/src/main/java/com/xclub/core/common/result/Result.kt`
- Create: `core/core-common/src/main/java/com/xclub/core/common/util/DateTimeUtils.kt`
- Create: `core/core-common/src/main/java/com/xclub/core/common/util/CryptoUtils.kt`

- [ ] **Step 1: 创建 core-common build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xclub.core.common"
    compileSdk = 36
    defaultConfig { minSdk = 33 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
}
```

- [ ] **Step 2: 创建 AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: 创建 Result 封装**

```kotlin
package com.xclub.core.common.result

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}

fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Error -> null
}

fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw exception
}
```

- [ ] **Step 4: 创建 DateTimeUtils**

```kotlin
package com.xclub.core.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFormatter = DateTimeFormatter.ofPattern("M月d日")

    fun formatDate(date: LocalDate): String = date.format(displayFormatter)
    fun parseDate(text: String): LocalDate = LocalDate.parse(text, dateFormatter)
    fun now(): Instant = Instant.now()
    fun today(): LocalDate = LocalDate.now()
    fun LocalDate.toTimestamp(): Long = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}
```

- [ ] **Step 5: 创建 CryptoUtils**

```kotlin
package com.xclub.core.common.util

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private const val AES_KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val PBKDF2_ITERATIONS = 10000

    fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, AES_KEY_SIZE)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    fun generateSalt(): ByteArray {
        val salt = ByteArray(32)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun encrypt(plaintext: String, key: SecretKey): String {
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(iv + encrypted)
    }

    fun decrypt(ciphertext: String, key: SecretKey): String {
        val combined = Base64.getDecoder().decode(ciphertext)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }
}
```

- [ ] **Step 6: 提交**

```bash
git add core/core-common/
git commit -m "feat: add core-common module with Result, DateTimeUtils, CryptoUtils"
```

---

### Task 3: 创建 core-ui 模块

**Files:**
- Create: `core/core-ui/build.gradle.kts`
- Create: `core/core-ui/src/main/AndroidManifest.xml`
- Create: `core/core-ui/src/main/java/com/xclub/core/ui/theme/Color.kt`
- Create: `core/core-ui/src/main/java/com/xclub/core/ui/theme/Type.kt`
- Create: `core/core-ui/src/main/java/com/xclub/core/ui/theme/Theme.kt`
- Create: `core/core-ui/src/main/java/com/xclub/core/ui/component/XclubScaffold.kt`

- [ ] **Step 1: 创建 core-ui build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.xclub.core.ui"
    compileSdk = 36
    defaultConfig { minSdk = 33 }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
}
```

- [ ] **Step 2: 创建 AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: 创建 Color.kt**

```kotlin
package com.xclub.core.ui.theme

import androidx.compose.ui.graphics.Color

val XclubPrimary = Color(0xFF1A73E8)
val XclubOnPrimary = Color(0xFFFFFFFF)
val XclubPrimaryContainer = Color(0xFFD3E3FD)
val XclubOnPrimaryContainer = Color(0xFF041E49)
val XclubSecondary = Color(0xFF5F6368)
val XclubOnSecondary = Color(0xFFFFFFFF)
val XclubSecondaryContainer = Color(0xFFE8EAED)
val XclubOnSecondaryContainer = Color(0xFF1C1B1F)
val XclubBackground = Color(0xFFFBFCFE)
val XclubOnBackground = Color(0xFF1C1B1F)
val XclubSurface = Color(0xFFFFFFFF)
val XclubOnSurface = Color(0xFF1C1B1F)
val XclubDarkPrimary = Color(0xFFA8C7FA)
val XclubDarkOnPrimary = Color(0xFF062E6F)
val XclubDarkPrimaryContainer = Color(0xFF0842A0)
val XclubDarkOnPrimaryContainer = Color(0xFFD3E3FD)
val XclubDarkBackground = Color(0xFF1C1B1F)
val XclubDarkOnBackground = Color(0xFFE6E1E5)
val XclubDarkSurface = Color(0xFF2B2930)
val XclubDarkOnSurface = Color(0xFFE6E1E5)
val XclubExpense = Color(0xFFD93025)
val XclubIncome = Color(0xFF1E8E3E)
val XclubSaving = Color(0xFFFA7B17)
```

- [ ] **Step 4: 创建 Type.kt**

```kotlin
package com.xclub.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val XclubTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
)
```

- [ ] **Step 5: 创建 Theme.kt**

```kotlin
package com.xclub.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = XclubPrimary, onPrimary = XclubOnPrimary,
    primaryContainer = XclubPrimaryContainer, onPrimaryContainer = XclubOnPrimaryContainer,
    secondary = XclubSecondary, onSecondary = XclubOnSecondary,
    secondaryContainer = XclubSecondaryContainer, onSecondaryContainer = XclubOnSecondaryContainer,
    background = XclubBackground, onBackground = XclubOnBackground,
    surface = XclubSurface, onSurface = XclubOnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = XclubDarkPrimary, onPrimary = XclubDarkOnPrimary,
    primaryContainer = XclubDarkPrimaryContainer, onPrimaryContainer = XclubDarkOnPrimaryContainer,
    background = XclubDarkBackground, onBackground = XclubDarkOnBackground,
    surface = XclubDarkSurface, onSurface = XclubDarkOnSurface
)

@Composable
fun XclubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = XclubTypography, content = content)
}
```

- [ ] **Step 6: 创建 XclubScaffold**

```kotlin
package com.xclub.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XclubScaffold(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions
            )
        },
        content = content
    )
}
```

- [ ] **Step 7: 提交**

```bash
git add core/core-ui/
git commit -m "feat: add core-ui module with theme, colors, typography, scaffold"
```

---

### Task 4: 创建 core-data 模块

**Files:**
- Create: `core/core-data/build.gradle.kts`
- Create: `core/core-data/src/main/AndroidManifest.xml`
- Create: `core/core-data/src/main/java/com/xclub/core/data/db/entity/` (所有 Entity)
- Create: `core/core-data/src/main/java/com/xclub/core/data/db/converter/Converters.kt`
- Create: `core/core-data/src/main/java/com/xclub/core/data/db/dao/` (所有 DAO)
- Create: `core/core-data/src/main/java/com/xclub/core/data/db/XclubDatabase.kt`
- Create: `core/core-data/src/main/java/com/xclub/core/data/di/DataModule.kt`

- [ ] **Step 1: 创建 core-data build.gradle.kts**

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.xclub.core.data"
    compileSdk = 36
    defaultConfig {
        minSdk = 33
        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.sqlcipher)
    implementation(libs.sqlite.framework)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.datastore.preferences)
}
```

- [ ] **Step 2: 创建 AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

- [ ] **Step 3: 创建所有 Entity 类** — 每个文件一个 Entity，包含 `@Entity` 和 `@PrimaryKey` 注解。具体代码见设计文档中的数据模型，字段名与设计文档一致。金额统一使用 `Long`（分为单位），日期使用 `LocalDate`，时间戳使用 `Instant`，枚举使用 `String` 存储。

- [ ] **Step 4: 创建 Converters** — 提供 `Instant ↔ Long` 和 `LocalDate ↔ Long` 的 TypeConverter。

- [ ] **Step 5: 创建所有 DAO 接口** — 每个 DAO 提供 `getAll(): Flow<List<>>`、`getById(): suspend`、`insert(): suspend`、`update(): suspend`、`delete(): suspend`。TransactionDao 额外提供 `getByDateRange()` 和 `sumByTypeAndDateRange()`。NoteDao 额外提供 `search()` 和 `getByTitle()`。NoteLinkDao 提供 `getOutgoingLinks()` 和 `getIncomingLinks()`。具体代码见设计文档。

- [ ] **Step 6: 创建 XclubDatabase** — 注册所有 Entity 和 DAO，`@TypeConverters(Converters::class)`，`version = 1`。

- [ ] **Step 7: 创建 DataModule** — Hilt `@Module`，提供 `XclubDatabase` 实例（使用 SQLCipher `SupportFactory`），提供所有 DAO 实例。数据库密钥从 SharedPreferences 读取或生成 UUID。

- [ ] **Step 8: 提交**

```bash
git add core/core-data/
git commit -m "feat: add core-data module with Room database, entities, DAOs, SQLCipher"
```

---

### Task 5: 创建 App 壳

**Files:**
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/xclub/app/XclubApp.kt`
- Create: `app/src/main/java/com/xclub/app/MainActivity.kt`
- Create: `app/src/main/java/com/xclub/app/navigation/TopLevelDestination.kt`
- Create: `app/src/main/java/com/xclub/app/navigation/XclubNavHost.kt`
- Create: `app/src/main/java/com/xclub/app/ui/XclubApp.kt`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/xml/file_paths.xml`

- [ ] **Step 1: 创建 app build.gradle.kts** — 依赖所有 core 和 feature 模块，配置 `applicationId = "com.xclub.app"`，`targetSdk = 36`，`minSdk = 33`。

- [ ] **Step 2: 创建 AndroidManifest.xml** — 声明 INTERNET、REQUEST_INSTALL_PACKAGES、POST_NOTIFICATIONS、SCHEDULE_EXACT_ALARM 权限；注册 `XclubApp`、`MainActivity`、`FileProvider`。

- [ ] **Step 3: 创建资源文件** — strings.xml（app_name=Xclub）、themes.xml（Material.Light.NoActionBar）、file_paths.xml（external_files + cache）。

- [ ] **Step 4: 创建 XclubApp** — `@HiltAndroidApp Application`。

- [ ] **Step 5: 创建 MainActivity** — `@AndroidEntryPoint ComponentActivity`，`enableEdgeToEdge()`，`setContent { XclubTheme { XclubApp() } }`。

- [ ] **Step 6: 创建 TopLevelDestination** — 枚举 4 个 Tab（WEB/FINANCE/NOTES/TODO），各含 route/icon/label。

- [ ] **Step 7: 创建 XclubNavHost** — `NavHost` 注册 webGraph/financeGraph/notesGraph/todoGraph。

- [ ] **Step 8: 创建 XclubApp** — `Scaffold` + `NavigationBar`（4 个 `NavigationBarItem`）+ `XclubNavHost`。

- [ ] **Step 9: 提交**

```bash
git add app/
git commit -m "feat: add app shell with MainActivity, bottom navigation, NavHost"
```

---

## Phase 2: feature-web 内嵌网页模块

### Task 6: 创建 feature-web 模块

**Files:**
- Create: `feature/feature-web/build.gradle.kts`
- Create: `feature/feature-web/src/main/AndroidManifest.xml`
- Create: `feature/feature-web/src/main/java/com/xclub/feature/web/navigation/WebRoute.kt`
- Create: `feature/feature-web/src/main/java/com/xclub/feature/web/navigation/WebNavigation.kt`
- Create: `feature/feature-web/src/main/java/com/xclub/feature/web/viewmodel/WebBookmarkViewModel.kt`
- Create: `feature/feature-web/src/main/java/com/xclub/feature/web/ui/WebBookmarkListScreen.kt`
- Create: `feature/feature-web/src/main/java/com/xclub/feature/web/ui/WebBookmarkAddDialog.kt`
- Create: `feature/feature-web/src/main/java/com/xclub/feature/web/ui/WebViewScreen.kt`

- [ ] **Step 1: 创建 build.gradle.kts** — 依赖 core-common/core-ui/core-data，启用 compose/hilt/ksp。

- [ ] **Step 2: 创建 WebRoute** — `list` 和 `view/{bookmarkId}` 路由常量。

- [ ] **Step 3: 创建 WebNavigation** — `webGraph()` 扩展函数，注册 `WebBookmarkListScreen` 和 `WebViewScreen` composable。

- [ ] **Step 4: 创建 WebBookmarkViewModel** — `@HiltViewModel`，注入 `WebBookmarkDao`，暴露 `bookmarks: StateFlow`，提供 `addBookmark/deleteBookmark/updateBookmark`。

- [ ] **Step 5: 创建 WebBookmarkListScreen** — 空状态提示 + `LazyColumn` 卡片列表 + 右上角添加按钮 + 长按编辑/删除。使用 `XclubScaffold`。

- [ ] **Step 6: 创建 WebBookmarkAddDialog** — `AlertDialog`，支持新建和编辑模式，输入标题/网址/分组，编辑模式额外显示删除按钮。

- [ ] **Step 7: 创建 WebViewScreen** — `AndroidView({ WebView })` 沉浸式展示，`TopAppBar` 仅显示标题+返回按钮，启用 JavaScript。

- [ ] **Step 8: 提交**

```bash
git add feature/feature-web/
git commit -m "feat: add feature-web module with bookmark list, add/edit dialog, WebView screen"
```

---

## Phase 3: feature-finance 记账模块

### Task 7: 创建 feature-finance 模块

**Files:**
- Create: `feature/feature-finance/build.gradle.kts` + AndroidManifest.xml
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/navigation/FinanceRoute.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/navigation/FinanceNavigation.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/util/BillingCycleCalculator.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/viewmodel/FinanceViewModel.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/viewmodel/SavingPlanViewModel.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/ui/FinanceOverviewScreen.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/ui/AddTransactionScreen.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/ui/SavingPlanScreen.kt`
- Create: `feature/feature-finance/src/main/java/com/xclub/feature/finance/ui/FinanceStatsScreen.kt`

- [ ] **Step 1: 创建 build.gradle.kts** — 同 feature-web 模式。

- [ ] **Step 2: 创建 BillingCycleCalculator** — 计算 `CycleRange(start, end)`，支持 `NATURAL_MONTH` 和 `PAYDAY` 模式，支持 startOffset/endOffset 偏移，提供 `previousRange/nextRange` 切换。

- [ ] **Step 3: 创建 FinanceRoute + FinanceNavigation** — overview/addTransaction/savingPlan/stats 四个路由。

- [ ] **Step 4: 创建 FinanceViewModel** — 暴露 `FinanceOverviewState`（cycleRange/totalIncome/totalExpense/transactions/categories/billingCycle），提供 `addTransaction/deleteTransaction/previousCycle/nextCycle`。

- [ ] **Step 5: 创建 FinanceOverviewScreen** — 周期概览卡片（收入/支出/结余，等宽字体）+ 交易列表 + FAB 记一笔 + 左右箭头切换周期。

- [ ] **Step 6: 创建 AddTransactionScreen** — 支出/收入切换 + 金额输入 + 分类选择网格 + 备注 + 保存按钮。

- [ ] **Step 7: 创建 SavingPlanViewModel + SavingPlanScreen** — 存钱计划列表 + 进度条 + 存入按钮 + 新建对话框。

- [ ] **Step 8: 创建 FinanceStatsScreen** — 占位页面，后续迭代添加图表。

- [ ] **Step 9: 提交**

```bash
git add feature/feature-finance/
git commit -m "feat: add feature-finance module with overview, add transaction, saving plan, billing cycle"
```

---

## Phase 4: feature-notes 笔记模块

### Task 8: 创建 feature-notes 模块

**Files:**
- Create: `feature/feature-notes/build.gradle.kts` + AndroidManifest.xml
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/navigation/NotesRoute.kt`
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/navigation/NotesNavigation.kt`
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/util/NoteLinkParser.kt`
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/viewmodel/NoteViewModel.kt`
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/ui/NoteListScreen.kt`
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/ui/NoteEditScreen.kt`
- Create: `feature/feature-notes/src/main/java/com/xclub/feature/notes/ui/KnowledgeGraphScreen.kt`

- [ ] **Step 1: 创建 build.gradle.kts**

- [ ] **Step 2: 创建 NoteLinkParser** — 正则 `Regex("""\[\[(.+?)]]""")` 提取链接名，`replaceLinkNames` 替换链接名。

- [ ] **Step 3: 创建 NotesRoute + NotesNavigation** — list/edit/{noteId}/graph 三个路由。

- [ ] **Step 4: 创建 NoteViewModel** — 暴露 `noteListState: StateFlow<NoteListState>`，搜索用 `flatMapLatest`，提供 `saveNote/deleteNote/getBackLinks`。保存时自动解析 `[[xxx]]` 并维护 `NoteLinkEntity`。

- [ ] **Step 5: 创建 NoteListScreen** — 搜索栏 + 卡片列表 + FAB 新建 + 知识图谱入口。

- [ ] **Step 6: 创建 NoteEditScreen** — 标题输入 + 正文编辑/预览切换 + 备注 + 反向链接列表。返回时自动保存。

- [ ] **Step 7: 创建 KnowledgeGraphScreen** — 占位页面，后续迭代添加 Canvas 绘制。

- [ ] **Step 8: 提交**

```bash
git add feature/feature-notes/
git commit -m "feat: add feature-notes module with list, edit, link parsing, knowledge graph placeholder"
```

---

## Phase 5: feature-todo 效率工具模块

### Task 9: 创建 feature-todo 模块

**Files:**
- Create: `feature/feature-todo/build.gradle.kts` + AndroidManifest.xml
- Create: `feature/feature-todo/src/main/java/com/xclub/feature/todo/navigation/TodoRoute.kt`
- Create: `feature/feature-todo/src/main/java/com/xclub/feature/todo/navigation/TodoNavigation.kt`
- Create: `feature/feature-todo/src/main/java/com/xclub/feature/todo/viewmodel/TodoViewModel.kt`
- Create: `feature/feature-todo/src/main/java/com/xclub/feature/todo/ui/TodoListScreen.kt`
- Create: `feature/feature-todo/src/main/java/com/xclub/feature/todo/ui/AddTodoScreen.kt`

- [ ] **Step 1: 创建 build.gradle.kts**

- [ ] **Step 2: 创建 TodoRoute + TodoNavigation** — list/add 两个路由。

- [ ] **Step 3: 创建 TodoViewModel** — 暴露 `activeTodos/completedTodos`，提供 `addTodo/toggleComplete/deleteTodo`。

- [ ] **Step 4: 创建 TodoListScreen** — 待办/已完成两段列表 + Checkbox 切换 + 优先级色点 + FAB。

- [ ] **Step 5: 创建 AddTodoScreen** — 标题 + 备注 + 优先级 FilterChip（高/中/低）+ 截止日期开关 + 保存。

- [ ] **Step 6: 提交**

```bash
git add feature/feature-todo/
git commit -m "feat: add feature-todo module with list, add todo, toggle complete"
```

---

## Phase 6: 云同步服务端

### Task 10: 创建 sync-server

**Files:**
- Create: `sync-server/build.gradle.kts`
- Create: `sync-server/src/main/kotlin/com/xclub/sync/Application.kt`
- Create: `sync-server/src/main/kotlin/com/xclub/sync/model/SyncModels.kt`
- Create: `sync-server/src/main/kotlin/com/xclub/sync/database/DatabaseFactory.kt`
- Create: `sync-server/src/main/kotlin/com/xclub/sync/routes/AuthRoutes.kt`
- Create: `sync-server/src/main/kotlin/com/xclub/sync/routes/SyncRoutes.kt`
- Create: `sync-server/src/main/kotlin/com/xclub/sync/routes/UpdateRoutes.kt`
- Create: `sync-server/Dockerfile`
- Create: `sync-server/docker-compose.yml`
- Create: `sync-server/Caddyfile`
- Create: `sync-server/deploy.sh`

- [ ] **Step 1: 创建 sync-server build.gradle.kts** — Kotlin JVM 项目，依赖 Ktor Server（netty/content-negotiation/auth/cors/status-pages）、kotlinx-serialization-json、logback、sqlite-jdbc、HikariCP。

- [ ] **Step 2: 创建 SyncModels** — `SyncRequest/SyncRecord/SyncResponse/AuthRequest/AuthResponse/UpdateInfo` 数据类。

- [ ] **Step 3: 创建 DatabaseFactory** — SQLite 连接，创建 `sync_records` 表（module/record_id/action/data/updated_at/sync_version），索引 `(module, sync_version)`。

- [ ] **Step 4: 创建 AuthRoutes** — `/api/auth` POST，支持 register（生成 UUID token）和 verify。

- [ ] **Step 5: 创建 SyncRoutes** — `/api/sync` POST（需认证），接收客户端变更记录，返回服务端新记录。增量同步基于 `syncVersion`。

- [ ] **Step 6: 创建 UpdateRoutes** — `/api/update/check` GET，读取 `./data/updates/` 目录下最新 APK 文件信息返回。

- [ ] **Step 7: 创建 Application.kt** — Ktor 服务器入口，配置 CORS、ContentNegotiation（JSON）、Authentication（Bearer token）、注册路由、默认端口 5555。

- [ ] **Step 8: 创建 Dockerfile**

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/sync-server-*-all.jar app.jar
EXPOSE 5555
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 9: 创建 docker-compose.yml**

```yaml
services:
  sync-server:
    build: .
    ports:
      - "${PORT:-5555}:5555"
    volumes:
      - ./data:/app/data
    environment:
      - PORT=5555
    restart: unless-stopped

  caddy:
    image: caddy:2-alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./Caddyfile:/etc/caddy/Caddyfile
      - caddy_data:/data
      - caddy_config:/config
    restart: unless-stopped
    depends_on:
      - sync-server

  cloudflared:
    image: cloudflare/cloudflared:latest
    command: tunnel run
    environment:
      - TUNNEL_TOKEN=${CLOUDFLARE_TUNNEL_TOKEN:-}
    profiles:
      - cloudflare
    restart: unless-stopped

volumes:
  caddy_data:
  caddy_config:
```

- [ ] **Step 10: 创建 Caddyfile**

```
{$DOMAIN:} {
    reverse_proxy sync-server:5555
}

:5555 {
    reverse_proxy sync-server:5555
    tls internal
}
```

- [ ] **Step 11: 创建 deploy.sh** — 交互式部署脚本：检测 Docker → 输入端口/域名/Cloudflare 配置 → 生成 `.env` → `docker compose up -d` → 健康检查。支持 `--repair`（重启容器/检查端口/恢复数据）和 `--uninstall`（停止容器/清理镜像/可选保留数据）。

- [ ] **Step 12: 提交**

```bash
git add sync-server/
git commit -m "feat: add sync-server with Ktor, Docker, Caddy, Cloudflare Tunnel, deploy script"
```

---

## Phase 7: core-sync 客户端同步引擎 + 应用更新

### Task 11: 创建 core-sync 模块

**Files:**
- Create: `core/core-sync/build.gradle.kts`
- Create: `core/core-sync/src/main/AndroidManifest.xml`
- Create: `core/core-sync/src/main/java/com/xclub/core/sync/SyncModels.kt`
- Create: `core/core-sync/src/main/java/com/xclub/core/sync/SyncApi.kt`
- Create: `core/core-sync/src/main/java/com/xclub/core/sync/SyncEngine.kt`
- Create: `core/core-sync/src/main/java/com/xclub/core/sync/di/SyncModule.kt`
- Create: `core/core-sync/src/main/java/com/xclub/core/sync/update/UpdateChecker.kt`

- [ ] **Step 1: 创建 core-sync build.gradle.kts** — 依赖 core-common/core-data，Retrofit + OkHttp + kotlinx-serialization。

- [ ] **Step 2: 创建 SyncModels** — 客户端同步数据类，与服务端模型对应。

- [ ] **Step 3: 创建 SyncApi** — Retrofit 接口，`@POST("/api/sync")` 和 `@GET("/api/update/check")`。

- [ ] **Step 4: 创建 SyncEngine** — 增量同步逻辑：读取本地 `syncVersion` → 收集变更记录 → 上传 → 接收服务端变更 → 写入本地数据库。支持仅 Wi-Fi 检查。

- [ ] **Step 5: 创建 SyncModule** — Hilt Module，提供 Retrofit 实例（服务器地址从 UserConfig 读取）和 SyncEngine。

- [ ] **Step 6: 创建 UpdateChecker** — 检查版本更新，下载 APK，通过 FileProvider + Intent 安装。

- [ ] **Step 7: 提交**

```bash
git add core/core-sync/
git commit -m "feat: add core-sync module with sync engine, update checker"
```

---

## Phase 8: 验证与集成

### Task 12: 编译验证与初始数据填充

- [ ] **Step 1: 执行全项目编译**

```bash
./gradlew assembleDebug
```

修复所有编译错误。

- [ ] **Step 2: 创建默认分类数据** — 在 `DataModule` 中添加 `RoomDatabase.Callback`，在 `onCreate` 时插入默认支出/收入分类（餐饮/交通/购物/工资/兼职等）。

- [ ] **Step 3: 验证各模块导航** — 在模拟器上测试 4 个 Tab 切换、各功能页面跳转。

- [ ] **Step 4: 提交**

```bash
git add .
git commit -m "feat: add default categories, fix compilation, verify navigation"
```
