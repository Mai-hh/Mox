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
    implementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("com.maihao.mox.Mox")
}

