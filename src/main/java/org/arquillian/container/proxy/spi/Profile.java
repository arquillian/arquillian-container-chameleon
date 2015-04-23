package org.arquillian.container.proxy.spi;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.arquillian.container.proxy.spi.Target.Server;
import org.arquillian.container.proxy.spi.Target.Type;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;

public class Profile {

    private Target target;
    private String[] dependencies;
    private String[] exclusions;

    public Profile(Target target, String[] dependencies, String[] exclusions) {
        this.target = target;
        this.dependencies = dependencies;
        this.exclusions = exclusions;
    }

    public Target getTarget() {
        return target;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public String[] getExclusions() {
        return exclusions;
    }

    public String getDeloyableContainerClass() {
        // return
        // "org.jboss.arquillian.container.glassfish.embedded_3_1.GlassFishContainer";
        return "org.jboss.as.arquillian.container."
                + target.getType().name().toLowerCase() + "."
                + target.getType().name() + "DeployableContainer";
    }

    public static Profile from(Target target) {
        String[] exclusions = new String[] { "org.jboss.arquillian.test:*",
                "org.jboss.arquillian.testenricher:*",
                "org.jboss.arquillian.container:*",
                "org.jboss.arquillian.core:*", "org.jboss.arquillian.config:*",
                "org.jboss.arquillian.protocol:*",
                "org.jboss.shrinkwrap.api:*", "org.jboss.shrinkwrap:*",
                "org.jboss.shrinkwrap.descriptors:*",
                "org.jboss.shrinkwrap.resolver:*",

                "*:wildfly-arquillian-testenricher-msc",
                "*:wildfly-arquillian-protocol-jmx",

                "*:jboss-as-arquillian-testenricher-msc",
                "*:jboss-as-arquillian-protocol-jmx" };
        return new Profile(target, getDependencies(target), exclusions);
    }

    public Map<String, String> getDefaultConfigurationPropertyVariablesValue(
            String installationDirectory) {
        Map<String, String> propertiesSet = new HashMap<String, String>();
        switch (target.getServer()) {
        case WILDFLY: {
            propertiesSet
                    .putAll(getWildflyNotSetSystemProperties(installationDirectory));
        }
            break;
        case JBOSS_AS:
            propertiesSet
                    .putAll(getWildflyNotSetSystemProperties(installationDirectory));
            break;
        default:
            break;
        }
        return propertiesSet;
    }

    private Map<String, String> getWildflyNotSetSystemProperties(
            String installationDirectory) {
        Map<String, String> propertiesSet = new HashMap<String, String>();
        // propertiesSet.put("java.util.logging.manager",
        // "org.jboss.logmanager.LogManager");
        propertiesSet.put("jbossHome", installationDirectory);
        propertiesSet.put("modulePath",
                Paths.get(installationDirectory, "modules").toString());
        return propertiesSet;
    }

    public MavenCoordinate getDistributableCoordinates() {
        switch (target.getServer()) {
        case WILDFLY: {
            String dep = getWildflyDistributableCoordinates(target.getVersion());
            MavenCoordinate mavenCoordinates = toMavenCoordinate(dep);
            return mavenCoordinates;
        }
        case JBOSS_AS: {
            String dep = getJBossASDistributableCoordinates(target.getVersion());
            MavenCoordinate mavenCoordinates = toMavenCoordinate(dep);
            return mavenCoordinates;
        }
        default:
            break;
        }
        throw new ConfigurationException("Unknown target: " + target);
    }

    private MavenCoordinate toMavenCoordinate(String dep) {
        return MavenCoordinates.createCoordinate(dep);
    }

    private static String getWildflyDistributableCoordinates(String version) {
        return "org.wildfly:wildfly-dist:zip:" + version;
    }

    private static String getJBossASDistributableCoordinates(String version) {
        return "org.jboss.as:jboss-as-dist:zip:" + version;
    }

    public static String[] getDependencies(Target target) {
        if (target.getServer() == Server.WILDFLY
                && target.getVersion().startsWith("8.")) {
            return getDependenciesWildFly8(target.getVersion(),
                    target.getType());
        }
        if (target.getServer() == Server.WILDFLY
                && target.getVersion().startsWith("9.")) {
            return getDependenciesWildFly9("1.0.0.Alpha2", target.getType());
        }
        if (target.getServer() == Server.JBOSS_AS) {
            return getDependenciesJBossAS(target.getVersion(), target.getType());
        }
        if (target.getServer() == Server.JBOSS_EAP
                && target.getVersion().startsWith("6.0")) {
            return getDependenciesJBossAS("7.1.2.Final", target.getType());
        }
        if (target.getServer() == Server.JBOSS_EAP
                && target.getVersion().startsWith("6.")) {
            return getDependenciesJBossAS("7.2.0.Final", target.getType());
        }
        // return new String[] {
        // "org.jboss.arquillian.container:arquillian-glassfish-embedded-3.1:1.0.0.CR4",
        // "org.glassfish.main.extras:glassfish-embedded-all:4.1"};

        throw new ConfigurationException("Unknown target: " + target);
    }

    private static String[] getDependenciesJBossAS(String version, Type type) {
        return new String[] { "org.jboss.as:jboss-as-arquillian-container-"
                + type.name().toLowerCase() + ":" + version };
    }

    private static String[] getDependenciesWildFly8(String version, Type type) {
        return new String[] { "org.wildfly:wildfly-arquillian-container-"
                + type.name().toLowerCase() + ":" + version };
    }

    private static String[] getDependenciesWildFly9(String version, Type type) {
        return new String[] { "org.wildfly.arquillian:wildfly-arquillian-container-"
                + type.name().toLowerCase() + ":" + version };
    }
}
