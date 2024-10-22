import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val test_member_code: String =
    gradleLocalProperties(rootDir, providers).getProperty("member_code", "")
val test_password: String = gradleLocalProperties(rootDir, providers).getProperty("password", "")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "flora_app@2024"
            keyPassword = "flora_app@2024"
            keyAlias = "Flora_app"
        }
    }
    namespace = "com.cmd.flora"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cmd.flora"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions.add("type")

    productFlavors {
        create("MOCK") {
            dimension = "type"
        }
        create("API") {
            dimension = "type"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "API_ENDPOINT", "\"https://stg.app.flora-g.co.jp/api/v1/\"")

            buildConfigField("String", "CART_URL", "\"https://shop-flora.stores.jp/\"")
            buildConfigField(
                "String",
                "Flora_URL",
                "\"https://www.flora-g.co.jp/funeral/okyami-cyoubun\""
            )
            buildConfigField("String", "Privacy_URL", "\"https://www.flora-g.co.jp/policy\"")


            buildConfigField("String", "testMemberCode", "\"\"")
            buildConfigField("String", "testPassword", "\"\"")
        }

        debug {
            applicationIdSuffix = ".dev"
            isDebuggable = true

//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "API_ENDPOINT", "\"https://stg.app.flora-g.co.jp/api/v1/\"")

            buildConfigField("String", "CART_URL", "\"https://shop-flora.stores.jp/\"")
            buildConfigField(
                "String",
                "Flora_URL",
                "\"https://www.flora-g.co.jp/funeral/okyami-cyoubun\""
            )
            buildConfigField("String", "Privacy_URL", "\"https://www.flora-g.co.jp/policy\"")
            buildConfigField("String", "testMemberCode", "\"$test_member_code\"")
            buildConfigField("String", "testPassword", "\"$test_password\"")
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
        buildConfig = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")   // Hilt compiler

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
    implementation("com.tbuonomo:dotsindicator:5.0")

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.robertlevonyan.view:MaterialChipView:3.0.8")

    implementation("io.coil-kt:coil:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")

    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("org.jsoup:jsoup:1.17.2")

    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")

}