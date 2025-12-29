plugins {
    kotlin("jvm") version "2.2.21"
}

group = "singh.akaalroop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.slack.api:bolt-socket-mode:1.45.3")
    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.20")
    implementation("org.slf4j:slf4j-simple:1.7.36")
}

kotlin {
    jvmToolchain(20)
}

tasks.test {
    useJUnitPlatform()
}