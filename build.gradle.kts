plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.27")
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation ("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation ("com.github.DaJokni:simplixstorage:-SNAPSHOT")
    //commandapi
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.5.3")
    compileOnly("dev.jorel:commandapi-annotations:9.5.3")
    annotationProcessor("dev.jorel:commandapi-annotations:9.5.3")
}


group = "GahvilaCore"
version = "2.0"
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
    }
    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        dependencies {
            include(dependency("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.5.3"))
            include(dependency("com.github.stefvanschie.inventoryframework:IF:0.10.17"))
            include(dependency("com.github.DaJokni:simplixstorage:-SNAPSHOT"))
        }
        relocate("dev.jorel.commandapi", "net.gahvila.gahvilacore.shaded.commandapi")
        relocate ("com.github.stefvanschie.inventoryframework", "net.gahvila.gahvilacore.shaded.inventoryframework")
        relocate("de.leonhard.storage", "net.gahvila.gahvilacore.shaded.storage")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}