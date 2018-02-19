package org.arquillian.container.chameleon.runner.extension;

import org.arquillian.container.chameleon.runner.ArquillianChameleon;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class ChameleonRunnerAppender implements AuxiliaryArchiveAppender {

    public static final String CHAMELEON_RUNNER_INCONTAINER_FILE = "chameleonrunner.txt";

    @Override
    public Archive<?> createAuxiliaryArchive() {
        return ShrinkWrap.create(JavaArchive.class, "arquillian-chameleon-runner.jar")
            .addPackage(ArquillianChameleon.class.getPackage())
            .addAsResource(new StringAsset("test"), CHAMELEON_RUNNER_INCONTAINER_FILE);
    }
}
