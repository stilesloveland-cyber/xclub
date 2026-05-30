pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
include(":sync-server")
