plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "fr.pierre.chiffreslettres.data"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    // api (pas implementation) : AppDatabase expose RoomDatabase/Flow<...> dans son API publique,
    // les modules consommateurs (:app) ont besoin de ces types sur leur classpath de compilation.
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    api(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
}
