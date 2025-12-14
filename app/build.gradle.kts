import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

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
        versionCode = 110
        versionName = "1.1.0"

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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    dependenciesInfo {
        // <<<< 可重复构建不能在发布包中包含依赖信息：
        // https://f-droid.org/en/docs/Reproducible_Builds/
        includeInApk = false
        includeInBundle = false
        // >>>>>
    }

    applicationVariants.all {
        val variant = this
        val projectName = rootProject.name.replace(Regex("\\s+"), "_")
        val buildType = variant.buildType.name
        val versionName = variant.versionName

        // 统一设置 apk 的打包名称
        if (buildType != "debug") {
            variant.outputs.all {
                val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                output.outputFileName = "${projectName}-${versionName}.apk"
            }
        }
    }
}

dependencies {
    // 用于序列化 @Serializable 标注的对象，且其需要启用插件 org.jetbrains.kotlin.plugin.serialization
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
    implementation(libs.kotlinx.serialization.json)
    //implementation(libs.gson)

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

    //
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    //implementation("com.patrykandpatrick.vico:compose-m3:2.1.3")
    //implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //implementation("com.github.AAChartModel:AAChartCore-Kotlin:7.4.0")
    implementation(project(":echarts"))

//    // CameraX
//    implementation(libs.androidx.camera.core)
//    implementation(libs.androidx.camera.camera2)
//    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.androidx.camera.view)
//    // ML Kit 二维码扫描
//    implementation(libs.barcode.scanning)
//    // Coil 图片加载
//    implementation(libs.coil.compose)
//    // Accompanist 权限
//    implementation(libs.accompanist.permissions)
//    // 系统 UI 控制器
//    implementation(libs.accompanist.systemuicontroller)

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