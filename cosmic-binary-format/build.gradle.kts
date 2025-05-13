plugins {
    id("cbf.common-conventions")
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.commons.reflection)
    api(libs.commons.provider)
    testImplementation(libs.commons.collections)
}
