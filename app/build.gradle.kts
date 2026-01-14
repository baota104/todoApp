plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.todoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.todoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Hỗ trợ Vector Drawable cho các bản Android thấp (quan trọng khi dùng icon vector)
        vectorDrawables {
            useSupportLibrary = true
        }
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

    // --- QUAN TRỌNG: Cấu hình cho XML ---
    buildFeatures {
        compose = false // Đảm bảo tắt Compose
        viewBinding = true // Bật ViewBinding để ánh xạ XML vào code Kotlin nhanh chóng
    }

    // Đã xóa khối composeOptions {...} vì không dùng

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- KHẮC PHỤC LỖI VERSION (QUAN TRỌNG) ---
    // Ép dùng các phiên bản ổn định cho SDK 34, ngăn chặn tự động tải bản mới gây lỗi
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Thêm dòng này để ép Activity về bản cũ (Fix lỗi activity:1.12.2 đòi SDK 36)
    implementation("androidx.activity:activity-ktx:1.9.0")

    // Thêm dòng này để ép Fragment về bản ổn định
    implementation("androidx.fragment:fragment-ktx:1.8.1")

    // --- GIAO DIỆN XML ---
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- NAVIGATION ---
    // Giữ bản 2.7.7 là bản rất ổn định cho XML
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // --- LIFECYCLE ---
    val lifecycleVersion = "2.8.3"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // --- VIEWPAGER2 ---
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // --- ROOM DATABASE ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // --- COROUTINES ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- TESTING ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // Đã hạ version để khớp SDK 34
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

// Thêm khối này xuống cuối file dependencies để đảm bảo chắc chắn không bị override
configurations.all {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.13.1")
        force("androidx.activity:activity-ktx:1.9.0")
        force("androidx.emoji2:emoji2-views-helper:1.0.0")
        force("androidx.emoji2:emoji2:1.0.0")
    }
}