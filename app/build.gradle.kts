import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Load keystore properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.halfminute.itmadness"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.halfminute.itmadness"
        minSdk = 33
        targetSdk = 36
        versionCode = 34
        versionName = "4.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Only include English, Dutch, French, German
    @Suppress("UnstableApiUsage")
    androidResources {
        localeFilters.addAll(listOf("en", "nl", "fr", "de"))
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Additional size optimizations
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = false // Disabled - minSdk 33 doesn't need it
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-Xjvm-default=all" // Better interop
            )
        }
        jvmToolchain(21)
    }

    buildFeatures {
        compose = true
        viewBinding = false // Disabled - not needed, slows down build
        buildConfig = true
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/{INDEX.LIST,DEPENDENCIES}"
            excludes += "**/*.kotlin_module"
            excludes += "**/*.version"
            excludes += "**/kotlin/**"
            excludes += "**/*.txt"
            excludes += "**/*.xml"
            excludes += "**/*.properties"
        }
        jniLibs {
            useLegacyPackaging = false
        }
        dex {
            useLegacyPackaging = false
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = true
        warningsAsErrors = false
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)

    // Kotlin
    implementation(libs.kotlin.stdlib)

    // Coroutines (lightweight - needed for Compose)
    implementation(libs.bundles.coroutines)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    // Remove unused animation dependency - using built-in animateFloatAsState
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Material Components (minimal - only for themes)
    implementation(libs.material.components)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}
