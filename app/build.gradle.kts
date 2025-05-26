plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") // PARA ROOM
   id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) // PARA ROOM

}

android {
    namespace = "com.muvbit.elnoticiero"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.muvbit.elnoticiero"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.7.6"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.coordinatorlayout)

    //PARA GOOGLE MAPS
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    //PARA NAVIGATION
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.paging:paging-runtime-ktx:3.3.0-alpha03")

    //PARA ROOM
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    kapt("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.22")

    //PARA PREFERENCIAS
    implementation ("androidx.preference:preference:1.2.0")

    //PARA CANALES TV
    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")
    implementation ("androidx.media3:media3-exoplayer-hls:1.3.1")

    /*
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    */


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    annotationProcessor(libs.androidx.room.compiler)
}