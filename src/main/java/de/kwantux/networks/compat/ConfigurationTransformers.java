package de.kwantux.networks.compat;

import de.kwantux.config.Configuration;
import de.kwantux.config.util.Transformation;
import org.apache.maven.artifact.versioning.ComparableVersion;

public final class ConfigurationTransformers {

    public static void generalConfigTransformers(Configuration config) {
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("2.1.9"),
                        "containerWhitelist",
                        "component.input",
                        false,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("2.1.9"),
                        "containerWhitelist",
                        "component.sorting",
                        false,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("2.1.9"),
                        "containerWhitelist",
                        "component.misc",
                        true,
                        null
                )
        );
    }

    public static void recipesTransformers(Configuration config) {
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("2.1.9"),
                        "upgrade.range",
                        null,
                        true,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("3.0.3"),
                        "component.input.block",
                        "component.input",
                        true,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("3.0.3"),
                        "component.input.upgrade",
                        null,
                        true,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("3.0.3"),
                        "component.sorting.block",
                        "component.sorting",
                        true,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("3.0.3"),
                        "component.sorting.upgrade",
                        null,
                        true,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("3.0.3"),
                        "component.misc.block",
                        "component.misc",
                        true,
                        null
                )
        );
        config.transformation(
                new Transformation(
                        new ComparableVersion("2.0.0"),
                        new ComparableVersion("3.0.3"),
                        "component.misc.upgrade",
                        null,
                        true,
                        null
                )
        );

    }
}
