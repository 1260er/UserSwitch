plugins {
    id("com.android.application")
}

android {
    namespace = "de.pritcloud.userswitch"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.pritcloud.userswitch"
        minSdk = 31
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
