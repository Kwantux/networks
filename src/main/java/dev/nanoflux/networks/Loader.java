package dev.nanoflux.networks;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class Loader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
//        resolver.addRepository(new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build());
//        resolver.addRepository(new RemoteRepository.Builder("spigot", "default", "https://hub.spigotmc.org/nexus/content/repositories/snapshots/").build());
        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-text-minimessage:4.13.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-hocon:4.1.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-yaml:4.1.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-core:2.0.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-annotations:2.0.0"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-paper:2.0.0-beta.10"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-bukkit:2.0.0-beta.10"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-minecraft-extras:2.0.0-beta.10"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.incendo:cloud-brigadier:2.0.0-beta.10"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.google.code.gson:gson:2.10.1"), null));

        classpathBuilder.addLibrary(resolver);
    }
}