plugins {
    kotlin("multiplatform") version "1.3.72"
}

apply(plugin = "maven-publish")

buildscript {
    repositories {
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
    }
}

repositories {
    mavenCentral()
}

group = "com.liftric"
version = "0.0.1"

kotlin {
    val buildForDevice = project.findProperty("device") as? Boolean ?: false
    val iosTarget = if(buildForDevice) iosArm64("ios") else iosX64("ios")
    iosTarget.binaries {
        framework {
            if (!buildForDevice) {
                embedBitcode("disable")
            }
        }
    }

    sourceSets {
        val iosMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val iosTest by getting {
            dependencies {
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}