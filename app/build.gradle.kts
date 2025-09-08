import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "org.crazydan.studio.app.healthtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.crazydan.studio.app.healthtracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 100
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
        // 显式启用在 buildTypes 中对 buildConfigField 的使用支持
        // https://stackoverflow.com/questions/74634321/fixing-the-build-type-contains-custom-buildconfig-fields-but-the-feature-is-di#answer-74634322
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore/release.properties")
            val keystoreProperties = Properties().apply {
                load(FileInputStream(keystorePropertiesFile))
            }

            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    buildTypes {
        // 开发调试版本
        debug {
            // 应用 id 添加调试信息后缀：需避免与 release 包同名，否则，在同一机器上二者不能共存
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        // 正式发布版本
        release {
            signingConfig = signingConfigs["release"]

            // 可重复构建不能在发布包中包含版本控制信息：
            // https://f-droid.org/en/docs/Reproducible_Builds/#vcs-info
            vcsInfo.include = false

            isShrinkResources = true
            isMinifyEnabled = true

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
    dependenciesInfo {
        // <<<< 可重复构建不能在发布包中包含依赖信息：
        // https://f-droid.org/en/docs/Reproducible_Builds/
        includeInApk = false
        includeInBundle = false
        // >>>>>
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.compose.material3.datetime.pickers)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    //implementation("com.patrykandpatrick.vico:compose-m3:2.1.3")
    //implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //implementation("com.github.AAChartModel:AAChartCore-Kotlin:7.4.0")
    implementation(project(":echarts"))

    implementation(libs.gson)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // @Preview 预览支持
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}