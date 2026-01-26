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

tasks.withType<PublishToMavenRepository>().all {
    dependsOn(tasks.build)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.picbel"
            artifactId = "simple-gmail-sender"
            version = "1.0.0"

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/picbel/SimpleGmailSender")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}