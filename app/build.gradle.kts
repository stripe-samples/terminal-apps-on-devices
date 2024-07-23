plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.stripe.aod.sampleapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stripe.aod.sampleapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlin {
        jvmToolchain(JavaLanguageVersion.of(17).asInt())
    }

    kotlinOptions {
        allWarningsAsErrors = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.fragment.ktx)

    // OK HTTP
    implementation(libs.okhttp)

    // Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)

    // Stripe Terminal library
    implementation(libs.stripe.terminal.core)
    implementation(libs.stripe.terminal.handoffclient)

    // navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
}