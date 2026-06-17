plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(":"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("com.materialkolor:material-kolor-jvm:4.1.1")
}

compose.desktop {
    application {
        mainClass = "deskit.demo.MainKt"
    }
}

kotlin {
    jvmToolchain(17)
}
