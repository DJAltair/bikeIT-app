plugins {
    alias(libs.plugins.android.application)
}

val tomtomApiKey: String by project

android {
    namespace = "com.example.bike_it"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bike_it"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }

    buildTypes {
        configureEach {
            buildConfigField("String", "TOMTOM_API_KEY", "\"$tomtomApiKey\"")
        }
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val version = "1.4.0"
    implementation("com.tomtom.sdk.navigation:navigation-online:$version")
    implementation("com.tomtom.sdk.location:provider-android:$version")
    implementation("com.tomtom.sdk.location:provider-map-matched:$version")
    implementation("com.tomtom.sdk.location:provider-simulation:$version")
    implementation("com.tomtom.sdk.maps:map-display:$version")
    implementation("com.tomtom.sdk.datamanagement:navigation-tile-store:$version")
    implementation("com.tomtom.sdk.navigation:ui:$version")
    implementation("com.tomtom.sdk.routing:route-planner-online:$version")
}