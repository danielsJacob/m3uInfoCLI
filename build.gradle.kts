plugins {
    kotlin("jvm") version "2.2.21"
}

group = "org.jex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
// Get latest Ktor Verison
val ktor_version: String by project
dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}