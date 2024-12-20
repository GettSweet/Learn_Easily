plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.learneasily.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.learneasily.myapplication"
        minSdk = 24
        targetSdk = 33
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.squareup.retrofit:retrofit:2.0.0-beta2")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("commons-io:commons-io:2.18.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")


    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")

}