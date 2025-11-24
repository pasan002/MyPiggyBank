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
        // Alternative Maven repositories
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Alternative Maven Central mirrors
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MyPiggyBank"
include(":app")
