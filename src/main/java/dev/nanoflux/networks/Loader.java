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
        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("net.kyori:adventure-text-minimessage:4.13.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-hocon:4.1.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.spongepowered:configurate-yaml:4.1.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("cloud.commandframework:cloud-paper:1.8.4"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.google.code.gson:gson:2.10.1"), null));

        classpathBuilder.addLibrary(resolver);
    }
}