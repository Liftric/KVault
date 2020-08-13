import java.util.Date
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
    id("net.nemerosa.versioning") version "2.14.0"
}

kotlin {
    ios()

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

val artifactName = "KVault"
val artifactGroup = "com.liftric"
val artifactVersion: String = with(versioning.info) {
    if (branch == "HEAD" && dirty.not()) {
        tag
    } else {
        full
    }
}

group = artifactGroup
version = artifactVersion

bintray {
    user = System.getenv("bintrayUser")
    key = System.getenv("bintrayApiKey")
    publish = true
    override = true

    pkg.apply {
        repo = "maven"
        name = artifactName
        userOrg = "liftric"
        vcsUrl = "https://github.com/Liftric/kvault"
        description = "Secure key-value storage for Kotlin Multiplatform projects"
        setLabels("kotlin-multiplatform", "liftric", "kotlin-native", "keychain", "sharedpreferences", "key-value-store")
        setLicenses("MIT")
        desc = description
        websiteUrl = "https://github.com/Liftric/kvault"
        issueTrackerUrl = "https://github.com/Liftric/kvault/issues"

        version.apply {
            name = artifactVersion
            vcsTag = artifactVersion
            released = Date().toString()
        }
    }
}

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = artifactGroup
        if (it.name.contains("metadata")) {
            it.artifactId = "${artifactName.toLowerCase()}-common"
        } else if (it.name.contains("android")) {
            it.artifactId = "${artifactName.toLowerCase()}-android"
        } else {
            it.artifactId = "${artifactName.toLowerCase()}-${it.name}"
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