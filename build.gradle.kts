import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Libs.AndroidLibrary) version Versions.Gradle
    kotlin(Libs.Multiplatform) version Versions.Kotlin
    id(Libs.MavenPublish)
    id(Libs.Versioning) version Versions.Versioning
    id(Libs.Signing)
    id(Libs.Nexus) version Versions.Nexus
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    ios {
        binaries.framework()
    }

    iosSimulatorArm64()

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
        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
        all {
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
        }
    }
}

tasks {
    val iosX64Test by existing(KotlinNativeSimulatorTest::class) {
        filter.excludeTestsMatching("com.liftric.kvault.KVaultTest")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

android {
    compileSdk = Android.CompileSdk

    defaultConfig {
        minSdk = Android.MinSdk
        targetSdk = Android.TargetSdk
        testInstrumentationRunner = Android.TestRunner
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

group = "com.liftric"
version = with(versioning.info) {
    if (branch == "HEAD" && dirty.not()) tag else full
}

afterEvaluate {
    project.publishing.publications.withType(MavenPublication::class.java).forEach {
        it.groupId = project.group.toString()
    }
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(ossrhUsername)
            password.set(ossrhPassword)
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        pom {
            name.set(project.name)
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
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}