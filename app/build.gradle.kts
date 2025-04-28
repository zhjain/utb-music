plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)  // 替换 kotlin("kapt")
}

android {
    namespace = "com.startend.youtubeaudioplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.startend.youtubeaudioplayer"
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
        encoding = "UTF-8"
    }

    kotlin {
        jvmToolchain(11)
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/INDEX.LIST"
        }
    }
    sourceSets {
        getByName("main") {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.youtube.player)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.google.api.client) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
    implementation(libs.androidx.google.api.services) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
    implementation(libs.androidx.google.http.client) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
    implementation(libs.androidx.guava)
    implementation(libs.androidx.coil.compose)
    
    // Room 依赖
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)  // 将 kapt 改为 ksp
    
    implementation(libs.androidx.navigation.compose)

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:30.1.1-android")
    }
}