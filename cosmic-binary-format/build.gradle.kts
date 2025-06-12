plugins {
    id("cbf.common-conventions")
    id("cbf.dokka-conventions")
    alias(libs.plugins.jmh)
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.commons.provider)
    implementation(libs.commons.reflection)
    testImplementation(libs.commons.collections)
}

kotlin {
    compilerOptions { 
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}