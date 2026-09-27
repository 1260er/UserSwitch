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
        versionCode = 1
        versionName = "0.1.0"
    }
}
