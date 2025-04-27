plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.emsi.bricole_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emsi.bricole_app"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.drawerlayout:drawerlayout:1.2.0") // ✅ This one is essential for the drawer
    implementation("com.google.android.material:material:1.11.0") // Material components (NavigationView, etc.)
    implementation("androidx.appcompat:appcompat:1.6.1") // AppCompat for Toolbar support
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
