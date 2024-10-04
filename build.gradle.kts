plugins {
    java
    `maven-publish`
    id ("com.gradleup.shadow") version "8.3.2"
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.27")
    compileOnly ("com.github.koca2000:NoteBlockAPI:1.6.2")
    compileOnly ("com.sk89q.worldguard:worldguard-bukkit:7.0.12")
    implementation ("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")

    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.5.3")
    compileOnly("dev.jorel:commandapi-annotations:9.5.3")
    annotationProcessor("dev.jorel:commandapi-annotations:9.5.3")
}


group = "net.gahvila"
version = findProperty("version")!!
description = "GahvilaCore"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    assemble {
        dependsOn(shadowJar)
        dependsOn(publishToMavenLocal)
    }
    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        dependencies {
            include(dependency("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.5.3"))
            include(dependency("com.github.stefvanschie.inventoryframework:IF:0.10.17"))
            include(dependency("com.github.simplix-softworks:simplixstorage:3.2.7"))
        }
        relocate("dev.jorel.commandapi", "net.gahvila.gahvilacore.shaded.commandapi")
        relocate ("com.github.stefvanschie.inventoryframework", "net.gahvila.gahvilacore.shaded.inventoryframework")
        relocate("de.leonhard.storage", "net.gahvila.aula.shaded.storage")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}