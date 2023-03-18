rootProject.name = "kvault"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("android-tools-gradle", "7.3.0")
            version("kotlin", "1.8.10")
            version("crypto", "1.1.0-alpha05")

            library("security-crypto", "androidx.security", "security-crypto").versionRef("crypto")
            library("androidx-test-core", "androidx.test", "core").version("1.5.0")
            library("roboelectric", "org.robolectric", "robolectric").version("4.9.2")

            plugin("versioning", "net.nemerosa.versioning").version("3.0.0")
        }
    }
}
