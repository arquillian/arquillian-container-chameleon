package org.arquillian.container.chameleon.deployment.file;

import java.nio.file.Paths;
import org.arquillian.container.chameleon.deployment.api.AbstractAutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

public class FileAutomaticDeployment extends AbstractAutomaticDeployment {

    @Override
    protected Archive<?> build(TestClass testClass) {

        if (testClass.isAnnotationPresent(File.class)) {
            final File file = testClass.getAnnotation(File.class);
            final String location = file.value();

            java.io.File archiveLocation = new java.io.File(location);
            if (!archiveLocation.isAbsolute()) {
                String rootPath = Paths.get(".").toAbsolutePath().toString();
                archiveLocation = new java.io.File(rootPath, location);
            }

            return ShrinkWrap
                .create(ZipImporter.class, getArchiveName(location))
                .importFrom(archiveLocation)
                .as(file.type());
        }

        return null;
    }

    private String getArchiveName(String deploymentFileLocation) {
        return deploymentFileLocation.substring(deploymentFileLocation.lastIndexOf('/') + 1);
    }
}
