import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version libs.versions.kotlin
    id("com.android.library") version libs.versions.android.tools.gradle
    id("maven-publish")
    id("signing")
    alias(libs.plugins.versioning)
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.security.crypto)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.roboelectric)
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
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
    namespace = "com.liftric.kvault"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner"
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
