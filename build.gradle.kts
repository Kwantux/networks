import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    `maven-publish`
    signing
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

runPaper.folia.registerTask()

group = "de.kwantux"
version = "3.1.12"
description = "A performance friendly way to sort your items"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.faststats.dev/releases")
}

dependencies {
    compileOnly("dev.folia", "folia-api", "1.21.4-R0.1-20250613.215508-3")
//    compileOnly("dev.folia", "folia-api", "1.21.11-R0.1-20260222.203011-14")
    paperLibrary("net.kyori", "adventure-text-minimessage", "4.13.1")
    paperLibrary("org.spongepowered", "configurate-hocon", "4.1.2")
    paperLibrary("org.spongepowered", "configurate-yaml", "4.1.2")
    paperLibrary("org.incendo", "cloud-paper", "2.0.0-beta.15")
    paperLibrary("com.google.code.gson", "gson", "2.10.1")
    paperLibrary("dev.faststats.metrics", "bukkit", "0.23.0")
    implementation("com.google.guava:guava:33.2.1-jre")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

paper {
    main = "de.kwantux.networks.Main"
    loader = "de.kwantux.networks.Loader"
    apiVersion = "1.21.4"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    website = "https://github.com/Kwantux/Networks"
    authors = listOf("Kwantux")
    prefix = "Networks"

    permissions {
        register("networks.create") {
            description = "Allows you to create networks"
            default = BukkitPluginDescription.Permission.Default.TRUE // TRUE, FALSE, OP or NOT_OP
        }
        register("networks.listforeign") {
            description = "Allows you to list networks of other players"
            default = BukkitPluginDescription.Permission.Default.TRUE // TRUE, FALSE, OP or NOT_OP
        }
        register("networks.bypass_limit") {
            description = "Allows you to bypass the network owning limit"
            default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
        register("networks.data") {
            description = "Allows you to save and reload config and network data"
            default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
    }

    generateLibrariesJson = true

    foliaSupported = true
}

tasks {
    runServer {
//        minecraftVersion("26.1.2")
        minecraftVersion("1.21.11")
        // Pass development flag to JVM
        jvmArgs("-Dnetworks.development=true")
    }
}