plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    kotlin("plugin.serialization") version "2.1.0"
}

android {

    //Pour utiliser VHAL
    useLibrary("android.car")

    flavorDimensions += "platform"

    productFlavors {
        // Platform dimension
        create("mobile") {
            dimension = "platform"
        }
        create("automotive") {
            dimension = "platform"
        }
    }

    namespace = "com.amonteiro.a26_04_ampere_automotive"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.amonteiro.a26_04_ampere_automotive"
        minSdk = 35
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Client requête
    implementation("io.ktor:ktor-client-okhttp:3.2.2")
    //Intégration avec la bibliothèque de serialisation, gestion des headers
    implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2") //Serialisation JSON
    implementation ("io.ktor:ktor-client-logging-jvm:3.2.2")  //log

    //Coil ImageLoader
    implementation("io.coil-kt.coil3:coil-network-ktor3:3.2.0")
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.+")

    testImplementation("io.mockk:mockk:1.+")

    implementation ("com.google.accompanist:accompanist-permissions:+")

    implementation("androidx.car.app:app:1.7.+")
    implementation("androidx.car.app:app-automotive:1.7.+")

}