plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "github.renderbr.hytale"
version = "1.1.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.hytale.com/release")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    val hytaleServer = "com.hypixel.hytale:Server:0.5.1"
    testImplementation(hytaleServer)
    compileOnly(hytaleServer)
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation(files("sqlite4j-compiled.jar"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    mergeServiceFiles()
    isZip64 = true
    isPreserveFileTimestamps = true
    exclude("**/SQLiteModuleMachineFuncGroup_0.class")
}