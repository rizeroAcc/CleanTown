plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rizero.core_data"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies{
    implementation(project(":core-database"))
    implementation(project(":core-network"))

    implementation(libs.ktor.serialization)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    api(libs.arrow.core)
    implementation(libs.bundles.koin.annotations)

    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences)

}
