buildscript {
    repositories {
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "1.4.0"))
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
}
