package org.arquillian.container.chameleon.deployment.gradle;

import org.jboss.shrinkwrap.api.Archive;

public interface GradleRunner {
    Archive<?> run(GradleBuild conf);
}
