plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.maihao"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    // 你的主函数入口类
    mainClass.set("com.maihao.mox.MoxKt")
}

// 配置 Shadow 打包任务
tasks.shadowJar {
    // 去掉默认的 "-all" 后缀，输出更干净
    archiveClassifier.set("")

    // 设置清单文件里的 Main-Class
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}