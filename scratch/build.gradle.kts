plugins {
    id(Plugins.dynamicFeature)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinExtensions)
    id(Plugins.kotlinKapt)
}
android {
    namespace = "com.navgurkul.scratch"
    compileSdk = BuildConfigVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildConfigVersions.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
}

dependencies {
    implementation(project(":commonUI"))
    implementation(project(":app"))
    implementation(project(":core"))

    // Koin for Kotlin
    implementation(KoinDependencies.koinAndroid)
    implementation(KoinDependencies.koinViewModel)

    //to get dynamic feature module
    implementation(GooglePlayDependencies.playCore)

    implementation(KotlinDependencies.kotlin)

    //androidx
    implementation(AndroidxDependencies.coreKtx)
    implementation(AndroidxDependencies.appcompat)
    implementation(MaterialDesignDependencies.materialDesign)
    implementation(AndroidxDependencies.constraintLayout)
    implementation(AndroidxDependencies.legacyV4)

    testImplementation(TestDependencies.jUnit)

    androidTestImplementation(TestDependencies.androidxJUnit)
    androidTestImplementation(TestDependencies.espresso)
    androidTestImplementation(TestDependencies.annotation)

    kapt(MiscellaneousDependencies.AutoService)
}