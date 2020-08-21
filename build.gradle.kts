buildscript {
    repositories {
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        mavenCentral()
        google()
        jcenter()
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
        jcenter()
    }
}
