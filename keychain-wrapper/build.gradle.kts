plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
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

    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val iosMain by getting {
            dependencies {
            }
        }
        val iosTest by getting {
            dependencies {
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("androidx.security:security-crypto:1.1.0-alpha01")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.robolectric:robolectric:4.3.1") {
                    exclude("com.google.auto.service", "auto-service")
                }
                implementation("androidx.test:core:1.2.0")
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        testInstrumentationRunner = "org.robolectric.RobolectricTestRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
