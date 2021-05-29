buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(Libs.AndroidGradleTools)
        classpath(kotlin(Libs.GradlePlugin, version = Versions.Kotlin))
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
