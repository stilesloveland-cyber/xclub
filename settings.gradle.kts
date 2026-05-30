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
