rootProject.name = "PrivacySentry"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        mavenCentral()
//        google()
//        maven { url = uri("https://jitpack.io") }
//    }
//}

include (":app")
include (":hook-sentry")
include (":plugin-sentry")
include (":privacy-test")
include (":privacy-annotation")
include (":privacy-proxy")
include (":privacy-replace")
