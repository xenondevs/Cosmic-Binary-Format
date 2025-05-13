import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "xyz.xenondevs.cbf"
version = "0.18"

val libs = the<LibrariesForLibs>()

repositories {
    mavenLocal { content { includeGroupAndSubgroups("xyz.xenondevs") } }
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    api(libs.kotlin.stdlib)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platformLauncher)
    testImplementation(libs.kotlin.test.junit)
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

publishing {
    repositories {
        maven {
            credentials {
                name = "xenondevs"
                url = uri { "https://repo.xenondevs.xyz/releases/" }
                credentials(PasswordCredentials::class)
            }
        }
    }
    
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
