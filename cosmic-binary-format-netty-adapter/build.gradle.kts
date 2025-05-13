plugins {
    id("cbf.common-conventions")
}

dependencies {
    api(project(":cosmic-binary-format"))
    api(libs.netty.buffer)
}
