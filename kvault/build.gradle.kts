import java.util.Date
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask

plugins {
    id(Libs.AndroidLibrary)
    kotlin(Libs.Multiplatform)
    id(Libs.MavenPublish)
    id(Libs.Bintray) version Versions.Bintray
    id(Libs.Versioning) version Versions.Versioning
}

kotlin {
    ios {
        binaries.framework()
    }

    android {
        publishLibraryVariants("debug", "release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(TestLibs.TestCommon))
                implementation(kotlin(TestLibs.TestAnnotations))
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
                implementation(Libs.Crypto)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin(TestLibs.Test))
                implementation(kotlin(TestLibs.TestJunit))
                implementation(TestLibs.RoboElectrics) {
                    exclude(Exclude.GoogleAutoService, Exclude.AutoService)
                }
                implementation(TestLibs.TestCore)
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
        }
    }
}

android {
    compileSdkVersion(Android.CompileSdk)

    defaultConfig {
        minSdkVersion(Android.MinSdk)
        targetSdkVersion(Android.TargetSdk)
        testInstrumentationRunner = Android.TestRunner
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
    }
}

tasks.withType<BintrayUploadTask> {
    doFirst {
        // https://github.com/bintray/gradle-bintray-plugin/issues/229
        project.publishing.publications.withType(MavenPublication::class.java).forEach {
            val moduleFile = buildDir.resolve("publications/${it.name}/module.json")
            if (moduleFile.exists()) {
                it.artifact(object : org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact(moduleFile) {
                    override fun getDefaultExtension() = "module"
                })
            }
        }
        val pubs = project.publishing.publications.map { it.name }
        setPublications(*pubs.toTypedArray())
    }
}

tasks.withType<BintrayUploadTask> {
    dependsOn("publishToMavenLocal")
}
