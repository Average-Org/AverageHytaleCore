import com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "github.renderbr.hytale"
version = "1.0.1"

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
    compileOnly("com.hypixel.hytale:Server:2026.01.27-734d39026")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("io.roastedroot:sqlite4j:3.51.2.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    mergeServiceFiles()
    isZip64 = true
    transform(AppendingTransformer::class.java){
        resource = "META-INF/services/java.sql.Driver"
    }
}