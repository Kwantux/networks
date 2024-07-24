package dev.nanoflux.config.util;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public record Transformation(
        @Nullable ComparableVersion minVersion,
        @Nullable ComparableVersion maxVersion,
        @Nonnull String oldKey,
        @Nonnull String newKey,
        boolean delete,
        @Nullable Function<ConfigurationNode, ConfigurationNode> transform
        ) {
}
