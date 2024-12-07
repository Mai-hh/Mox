plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.maihao"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.maihao.mox.Mox")
}

