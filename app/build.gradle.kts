plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "ru.commandos.diner.delivery"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        create("imitator") {
            initWith(getByName("debug"))
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")
    implementation("io.reactivex.rxjava3:rxjava:3.0.6")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("com.jakewharton.rxbinding4:rxbinding-material:4.0.0")
    implementation("com.uber.autodispose2:autodispose:2.0.0")
    implementation("com.uber.autodispose2:autodispose-lifecycle:2.0.0")
    implementation("com.uber.autodispose2:autodispose-android:2.0.0")
    implementation("com.uber.autodispose2:autodispose-androidx-lifecycle:2.0.0")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.jakewharton.timber:timber:4.7.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.1")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
