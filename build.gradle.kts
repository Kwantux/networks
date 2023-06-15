import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    `maven-publish`
    signing
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "net.quantum625"
version = "2.0.1"
description = "A performance friendly way to sort your items"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")
    implementation("net.kyori", "adventure-text-minimessage", "4.13.1")
    bukkitLibrary("org.spongepowered", "configurate-hocon", "4.1.2")
    bukkitLibrary("org.spongepowered", "configurate-yaml", "4.1.2")
    bukkitLibrary("cloud.commandframework", "cloud-paper", "1.8.1")
    bukkitLibrary("com.google.code.gson", "gson", "2.9.0");
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkit {
    main = "net.quantum625.networks.Main"
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Quantum625")

    permissions {
        register("networks.create") {
            description = "Allows you to create networks"
            default = BukkitPluginDescription.Permission.Default.TRUE // TRUE, FALSE, OP or NOT_OP
        }
        register("networks.listforeign") {
            description = "Allows you to list networks of other players"
            default = BukkitPluginDescription.Permission.Default.TRUE // TRUE, FALSE, OP or NOT_OP
        }
        register("networks.itemview") {
            description = "Allows you to run the test command"
            default = BukkitPluginDescription.Permission.Default.TRUE // TRUE, FALSE, OP or NOT_OP
        }
        register("networks.data") {
            description = "Allows you to save and reload config and network data"
            default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.20")
    }
}