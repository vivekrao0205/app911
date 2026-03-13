plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.nrikesari.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nrikesari.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /* ---------------- CORE ---------------- */

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    /* ---------------- COMPOSE ---------------- */

    implementation(platform("androidx.compose:compose-bom:2024.02.01"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.6")
    implementation("com.google.android.gms:play-services-fido:20.0.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    /* ---------------- NAVIGATION ---------------- */

    implementation("androidx.navigation:navigation-compose:2.7.7")

    /* ---------------- VIEWMODEL ---------------- */

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    /* ---------------- ICONS ---------------- */

    implementation("androidx.compose.material:material-icons-extended")

    /* ---------------- IMAGE LOADING ---------------- */

    implementation("io.coil-kt:coil-compose:2.5.0")

    /* ---------------- ROOM DATABASE ---------------- */

    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    /* ---------------- DATASTORE ---------------- */

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    /* ---------------- FIREBASE ---------------- */

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    /* ---------------- GOOGLE SIGN IN ---------------- */

    implementation("com.google.android.gms:play-services-auth:21.0.0")

    /* ---------------- COROUTINES ---------------- */

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    /* ---------------- TESTING ---------------- */

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}