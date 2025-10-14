pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // Add Maven repository for preview/alpha versions if needed
        maven {
            url = uri("https://androidx.dev/snapshots/builds/12443078/artifacts/repository")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.google.com")
        }
    }
}

rootProject.name = "GlobalTranslation"
include(":app", ":core", ":data")
