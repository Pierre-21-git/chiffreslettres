plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core-dictionary"))
    testImplementation(libs.junit)
}
