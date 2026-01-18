plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "github.renderbr.hytale"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly(files("lib/HytaleServer.jar"))
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    isZip64 = true
    entryCompression = ZipEntryCompression.DEFLATED
}