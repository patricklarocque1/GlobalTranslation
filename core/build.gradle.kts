plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    // Pure Kotlin, no Android dependencies
    implementation(libs.kotlinx.coroutines.core)
    
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

