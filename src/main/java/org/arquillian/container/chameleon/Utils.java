package org.arquillian.container.chameleon;

import java.io.File;
import java.net.URL;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;

public final class Utils {

    @SuppressWarnings("deprecation")
    public static URL[] toURLs(File[] archives) throws Exception {
        URL[] urls = new URL[archives.length];
        for (int i = 0; i < archives.length; i++) {
            urls[i] = archives[i].toURL();
        }
        return urls;
    }

    static String join(Object[] strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            if (i != strings.length - 1) {
                sb.append("\n ");
            }
        }
        return sb.toString();
    }

    public static MavenCoordinate toMavenCoordinate(String dep) {
        return MavenCoordinates.createCoordinate(dep);
    }

    public static MavenDependency[] toMavenDependencies(String[] dependencies, String[] exclusions) {
        MavenDependencyExclusion[] mavenExclusions = toMavenExclusions(exclusions);
        MavenDependency[] mavenDependencies = new MavenDependency[dependencies.length];
        for (int i = 0; i < dependencies.length; i++) {
            mavenDependencies[i] = MavenDependencies.createDependency(
                    dependencies[i],
                    ScopeType.COMPILE,
                    false,
                    mavenExclusions);
        }
        return mavenDependencies;
    }

    private static MavenDependencyExclusion[] toMavenExclusions(String[] exclusions) {
        MavenDependencyExclusion[] mavenExclusions = new MavenDependencyExclusion[exclusions.length];
        for (int i = 0; i < exclusions.length; i++) {
            mavenExclusions[i] = MavenDependencies.createExclusion(exclusions[i]);
        }
        return mavenExclusions;
    }

}
