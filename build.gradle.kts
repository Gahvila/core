plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.11"
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
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.27") {
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13-SNAPSHOT") {
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "com.google.guava", module = "guava")
    }
    compileOnly("dev.jorel:commandapi-annotations:9.7.0")

    implementation("de.tr7zw:item-nbt-api:2.14.1")

    implementation("com.github.stefvanschie.inventoryframework:IF-Paper:0.11.1-SNAPSHOT")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")

    annotationProcessor("dev.jorel:commandapi-annotations:9.7.0")
}

group = "net.gahvila"
version = findProperty("version")!!
description = "GahvilaCore"
java.sourceCompatibility = JavaVersion.VERSION_21


publishing {
    repositories {
        maven {
            name = "gahvila-snapshots"
            url = uri("https://repo.gahvila.net/snapshots/")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.gahvila"
            artifactId = "gahvilacore"
            version = findProperty("version").toString()
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
        relocate("de.tr7zw.changeme.nbtapi", "net.gahvila.gahvilacore.shaded.nbtapi")
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