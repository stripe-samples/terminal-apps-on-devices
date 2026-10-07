// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.5" apply false
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}
