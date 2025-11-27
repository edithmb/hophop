import org.gradle.internal.impldep.org.apache.ivy.util.url.IvyAuthenticator.install

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.chaquo.python")

}

android {
    namespace = "com.example.hophop"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hophop"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }

//        python {
//            // Ruta a Python en tu PC (ajusta según tu instalación)
//            buildPython("python3")  // En Windows puede ser "python" o "C:/Python311/python.exe"
//
//            // Librerías que necesitas instalar
//            pip {
//                install("pandas==2.0.3")
//                install("matplotlib==3.7.2")
//                install("numpy==1.24.3")
//                install("scikit-learn==1.3.0")
//            }
//        }
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}