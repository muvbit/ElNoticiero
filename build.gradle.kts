// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.1" apply false // Para que funcione NAVIGATION
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false //Para que funcione ROOM


}