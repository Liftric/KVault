object Android {
    const val CompileSdk = 30
    const val MinSdk = 21
    const val TargetSdk = 30
    const val TestRunner = "org.robolectric.RobolectricTestRunner"
}

object Versions {
    const val Gradle = "4.2.1"
    const val Kotlin = "1.5.20"
    const val Versioning = "2.14.0"
    const val Crypto = "1.1.0-alpha03"
    const val RoboElectric = "4.5.1"
    const val TestCore = "1.2.0"
}

object Libs {
    const val Signing = "signing"
    const val AndroidGradleTools = "com.android.tools.build:gradle:${Versions.Gradle}"
    const val AndroidLibrary = "com.android.library"
    const val Multiplatform = "multiplatform"
    const val MavenPublish = "maven-publish"
    const val Versioning = "net.nemerosa.versioning"
    const val Crypto = "androidx.security:security-crypto:${Versions.Crypto}"
}

object TestLibs {
    const val TestCommon = "test-common"
    const val TestAnnotations = "test-annotations-common"
    const val Test = "test"
    const val TestJunit = "test-junit"
    const val RoboElectrics = "org.robolectric:robolectric:${Versions.RoboElectric}"
    const val TestCore = "androidx.test:core:${Versions.TestCore}"
}

object Exclude {
    const val GoogleAutoService = "com.google.auto.service"
    const val AutoService = "auto-service"
}

