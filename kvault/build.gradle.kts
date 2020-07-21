import java.util.Date
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
}

val artifactName = "KVault"
val artifactGroup = "com.liftric"
val artifactVersion = "1.0"

val repoUrl = "https://github.com/Liftric/kvault"
val issueUrl = "https://github.com/Liftric/kvault/issues"
val desc = "Secure key-value store for Kotlin Multiplatform projects"

val githubRepo = "https://github.com/Liftric/kvault"
val githubReadme = "https://github.com/Liftric/kvault/blob/master/README.md"

val licenseName = "MIT"
val licenseUrl = "https://github.com/Liftric/kvault/blob/LICENSE"
val licenseDist = "repo"

val developerId = "liftric"

group = artifactName
version = artifactVersion

kotlin {
    val buildForDevice = project.findProperty("device") as? Boolean ?: false
    val iosTarget = if (buildForDevice) iosArm64("ios") else iosX64("ios")
    iosTarget.binaries {
        framework {
            if (!buildForDevice) {
                embedBitcode("disable")
            }
        }
    }

    android {
        publishLibraryVariants("release")
    }

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


bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_PASSWORD")
    publish = true
    override = true

    pkg.apply {
        repo = "liftric"
        name = artifactName
        userOrg = developerId
        vcsUrl = repoUrl
        description = desc
        setLabels("kotlin-multiplatform", "liftric", "kotlin-native", "keychain", "sharedpreferences", "key-value-store")
        setLicenses(licenseName)
        desc = description
        websiteUrl = repoUrl
        issueTrackerUrl = issueUrl

        version.apply {
            name = artifactVersion
            vcsTag = artifactVersion
            released = Date().toString()
        }
    }
}

tasks.withType<BintrayUploadTask> {
    doFirst {
        val pubs = project.publishing.publications.map { it.name }.filter { it != "kotlinMultiplatform" }
        setPublications(*pubs.toTypedArray())
    }
}

tasks.withType<BintrayUploadTask> {
    dependsOn("publishToMavenLocal")
}