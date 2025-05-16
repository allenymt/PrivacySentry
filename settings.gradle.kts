rootProject.name = "PrivacySentry"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include (":app")
include (":hook-sentry")
include (":plugin-sentry")
include (":privacy-test")
include (":privacy-annotation")
include (":privacy-proxy")
include (":privacy-replace")
