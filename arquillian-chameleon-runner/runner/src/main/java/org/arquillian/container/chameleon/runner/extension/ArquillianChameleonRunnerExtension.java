package org.arquillian.container.chameleon.runner.extension;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ArquillianChameleonRunnerExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, ChameleonRunnerAppender.class);
    }
}
