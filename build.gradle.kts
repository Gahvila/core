import java.util.Properties

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

val propsFile = file("gradle.properties")
val props = Properties().apply {
    load(propsFile.inputStream())
}

val versionPrefix = props["version"].toString()
val currentBuildNumber = props["buildNumber"].toString().toInt()
val newBuildNumber = currentBuildNumber + 1

val generatedVersion = "$versionPrefix+b$newBuildNumber"

version = generatedVersion

group = "net.gahvila"
version = generatedVersion
description = "GahvilaCore"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.gahvila.net/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.32") {
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.14") {
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation("de.tr7zw:item-nbt-api:2.15.0")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")
    implementation("com.github.koca2000:NBS4j:a15f8d8a19")

    compileOnly("dev.jorel:commandapi-annotations:10.1.1")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:10.1.1")
    annotationProcessor("dev.jorel:commandapi-annotations:10.1.1")

    implementation("com.zaxxer:HikariCP:6.3.0")
}

publishing {
    repositories {
        maven {
            name = "gahvila"
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
            version = generatedVersion
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
        relocate("net.gahvila.inventoryframework", "net.gahvila.gahvilacore.shaded.inventoryframework")
        relocate("de.leonhard.storage", "net.gahvila.gahvilacore.shaded.storage")
        relocate("de.tr7zw.changeme.nbtapi", "net.gahvila.gahvilacore.shaded.nbtapi")
    }

    assemble {
        dependsOn("updateBuildNumber")
        dependsOn(shadowJar)
    }

    processResources {
        expand(project.properties)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    register("updateBuildNumber") {
        doLast {
            props["buildNumber"] = newBuildNumber.toString()
            props.store(propsFile.outputStream(), null)
            println("Updated build number to $newBuildNumber")
        }
    }
}
