import java.util.Date

plugins {
    id(Libs.AndroidLibrary)
    kotlin(Libs.Multiplatform)
    id(Libs.MavenPublish)
    id(Libs.Versioning) version Versions.Versioning
    id("signing")
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

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = artifactGroup
    }
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications.withType<MavenPublication> {

        artifact(javadocJar.get())

        pom {
            name.set(artifactName)
            description.set("Secure key-value storage for Kotlin Multiplatform projects")
            url.set("https://github.com/Liftric/kvault")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/Liftric/kvault/blob/master/LICENSE")
                }
            }
            developers {
                developer {
                    id.set("gaebel")
                    name.set("Jan Gaebel")
                    email.set("gaebel@liftric.com")
                }
            }
            scm {
                url.set("https://github.com/Liftric/kvault")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}