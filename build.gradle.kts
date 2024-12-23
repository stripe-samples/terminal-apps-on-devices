// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.5" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}
