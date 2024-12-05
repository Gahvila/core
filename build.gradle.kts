plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.27")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12")
    compileOnly("dev.jorel:commandapi-annotations:9.7.0")
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0-SNAPSHOT")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.19-SNAPSHOT")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")

    annotationProcessor("dev.jorel:commandapi-annotations:9.7.0")
}

group = "net.gahvila"
version = findProperty("version")!!
description = "GahvilaCore"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        relocate("dev.jorel.commandapi", "net.gahvila.gahvilacore.shaded.commandapi")
        relocate("com.github.stefvanschie.inventoryframework", "net.gahvila.gahvilacore.shaded.inventoryframework")
        relocate("de.leonhard.storage", "net.gahvila.gahvilacore.shaded.storage")
    }

    assemble {
        dependsOn(shadowJar)
        dependsOn(publishToMavenLocal)
    }

    processResources {
        expand(project.properties)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}