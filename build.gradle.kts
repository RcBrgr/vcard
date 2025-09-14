plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
    id("maven-publish")
}

group = "com.github.rcbrgr"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlin.test)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            // Set the artifactId, which is your library's name
            artifactId = "vcard"

            // Tell Gradle to publish the components of a Java/Kotlin library
            from(components["kotlin"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}
