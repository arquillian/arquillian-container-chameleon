package org.arquillian.container.chameleon.deployment.maven;

import org.jboss.shrinkwrap.api.Archive;

public interface MavenRunner {
    Archive<?> run(MavenBuildDeployment conf);
}
