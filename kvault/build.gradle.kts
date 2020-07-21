import java.util.Date
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4"
}

val artifactName = "KVault"
val artifactGroup = project.group.toString()
val artifactVersion = project.version.toString()

val pomUrl = "https://github.com/Liftric/kvault"
val pomScmUrl = "https://github.com/Liftric/kvault"
val pomIssueUrl = "https://github.com/Liftric/kvault/issues"
val pomDesc = "Secure key-value store for Kotlin Multiplatform projects"

val githubRepo = "https://github.com/Liftric/kvault"
val githubReadme = "https://github.com/Liftric/kvault/blob/master/README.md"

val pomLicenseName = "MIT"
val pomLicenseUrl = "https://github.com/Liftric/kvault/blob/LICENSE"
val pomLicenseDist = "repo"

val pomDeveloperId = "liftric"
val pomDeveloperName = "Liftric"

group = "com.liftric"
version = "1.0"

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

publishing {
    publications {
        create<MavenPublication>("liftric") {
            groupId = artifactGroup
            artifactId = artifactName.toLowerCase()
            version = artifactVersion

            pom {
                name.set(rootProject.name)
                description.set(pomDesc)
                url.set(pomUrl)

                licenses {
                    license {
                        name.set(pomLicenseName)
                        url.set(pomLicenseUrl)
                    }
                }
                developers {
                    developer {
                        name.set(pomDeveloperName)
                        id.set(pomDeveloperId)
                    }
                }
                scm {
                    connection.set(pomScmUrl)
                    developerConnection.set(pomScmUrl)
                    url.set(pomUrl)
                }
            }
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
    publish = false
    override = true

    pkg.apply {
        repo = "liftric"
        name = artifactName
        userOrg = pomDeveloperId
        vcsUrl = pomScmUrl
        description = pomDesc
        setLabels("kotlin-multiplatform", "liftric", "kotlin-native", "keychain", "sharedpreferences", "key-value-store")
        setLicenses(pomLicenseName)
        desc = description
        websiteUrl = pomUrl
        issueTrackerUrl = pomIssueUrl

        version.apply {
            name = artifactVersion
            desc = description
            released = Date().toString()
            vcsTag = artifactVersion
        }
    }
}

tasks.withType<BintrayUploadTask> {
    dependsOn("publishToMavenLocal")
}