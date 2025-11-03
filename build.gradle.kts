// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.kotlin.compose) apply false
//}

//plugins {
//    id("com.android.application") version "8.2.0"
//    kotlin("android") version "2.0.0"
//}

//buildscript {
//    repositories { google(); mavenCentral() }
//    dependencies {
//        classpath("com.android.tools.build:gradle:8.2.0")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
//    }
//}
//
//allprojects {
//    repositories { google(); mavenCentral() }
//}

buildscript {
    // Opcional: solo si alguna dependencia legacy lo requiere. Normalmente no es necesario.
    repositories {
        // vacío intencionalmente: repositorios gestionados desde settings.gradle.kts
    }
    dependencies {
        // No colocar classpath aquí si no es necesario
    }
}


