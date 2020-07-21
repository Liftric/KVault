buildscript {
    repositories {
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.4")
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://dl.bintray.com/netguru/maven") }
    }
}
