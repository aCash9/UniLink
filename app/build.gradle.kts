plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.unilink"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.unilink"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.swiperefreshlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    implementation(libs.timeago)
    implementation(libs.ucrop)
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.ui)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.glide)
    implementation(libs.imageslideshow)
    implementation(libs.lottie)
    implementation(libs.picasso)
    implementation(libs.annotations)
    androidTestImplementation(libs.espresso.core)
}