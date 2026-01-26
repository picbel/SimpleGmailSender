plugins {
    kotlin("jvm") version "2.2.0"
    `maven-publish`
}

group = "com.picbel"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.mail:jakarta.mail-api:2.1.3")
    implementation("org.eclipse.angus:angus-mail:2.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "SimpleGmailSender"
            from(components["java"])
        }
    }
}
