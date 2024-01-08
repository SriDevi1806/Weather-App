@file:Suppress("UNUSED_EXPRESSION")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 34

    buildFeatures{
        viewBinding =true
        dataBinding =true
    }


    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.google.android.datatransport:transport-runtime:3.2.0")
    //  implementation("androidx.databinding:compilerCommon:3.2.0-alpha11")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //lottie animations
            implementation ("com.airbnb.android:lottie:6.1.0")

    // GSON converter
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
// retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

//
//    // Dagger - Hilt
//    implementation ("com.google.dagger:hilt-android:2.28.3-alpha")
//
//    kapt ("com.google.dagger:hilt-android-compiler:2.28.3-alpha")
//    implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02")
//    kapt ("androidx.hilt:hilt-compiler:1.0.0-alpha02")
//
//  //  viewModel
//   implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
//   implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
//     implementation ("androidx.room:room-ktx:2.4.0")

}