plugins {
    id("cbf.common-conventions")
    alias(libs.plugins.jmh)
}

dependencies {
    api(libs.kotlin.reflect)
    api(libs.commons.reflection)
    api(libs.commons.provider)
    testImplementation(libs.commons.collections)
}
