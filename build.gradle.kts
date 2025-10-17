plugins {
    kotlin("jvm") version "1.9.24"
    application
}
repositories { mavenCentral() }
application { mainClass.set("chess.app.MainKt") }
kotlin { jvmToolchain(17) }

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
