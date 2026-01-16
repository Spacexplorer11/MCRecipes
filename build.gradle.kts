plugins {
    kotlin("jvm") version "2.2.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "singh.akaalroop"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.slack.api:bolt-socket-mode:1.45.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.20")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20231013")
}

kotlin {
    jvmToolchain(20)
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("singh.akaalroop.MainKt")
}