plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dyslexia2813.teliktvwebview"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dyslexia2813.teliktvwebview"
        minSdk = 24
        targetSdk = 28
        versionCode = 2
        versionName = "2.0-native-player"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation("androidx.media3:media3-exoplayer:1.5.1")
}
