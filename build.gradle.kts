// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.1" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}
