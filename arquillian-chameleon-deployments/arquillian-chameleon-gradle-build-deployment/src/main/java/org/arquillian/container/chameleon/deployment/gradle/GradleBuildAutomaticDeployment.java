package org.arquillian.container.chameleon.deployment.gradle;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.arquillian.container.chameleon.deployment.api.AbstractAutomaticDeployment;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class GradleBuildAutomaticDeployment extends AbstractAutomaticDeployment {

    @Override
    protected Archive<?> build(TestClass testClass) {
        if (testClass.isAnnotationPresent(GradleBuild.class)) {
            final GradleBuild gradleBuildDeployment = testClass.getAnnotation(GradleBuild.class);
            return runBuild(gradleBuildDeployment);
        }
        return null;
    }

    private Archive<?> runBuild(GradleBuild conf) {
    	
    	ProjectConnection projectConnector = GradleConnector
    		.newConnector()
    		.forProjectDirectory(new File(conf.path()))
    		.connect();
    	
    	BuildLauncher launcher = projectConnector
    		.newBuild()
    		.forTasks(conf.tasks())
    		.withArguments("-x", "test");
    	
    	if (!conf.quiet()) {
	    	launcher.withArguments("--info", "--stacktrace");
	    	launcher.setStandardOutput(System.out);
	        launcher.setStandardError(System.err);
    	}
    	launcher.run();
    	
    	GradleProject projectModel = projectConnector.getModel(GradleProject.class);
		final Path artifactDir = projectModel.getBuildDirectory().toPath().resolve("libs");
		
		Path artifactPath = null;
		try (DirectoryStream<Path> filesInArtifactDir = Files.newDirectoryStream(artifactDir)) {
			for (Path file : filesInArtifactDir) {
                if (file.toString().endsWith(".war")) {
                	artifactPath = file;
                	break;
                }
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
			
		if (artifactPath == null) {
			throw new RuntimeException("No .war-file found in " + artifactDir.toString() + ".");
		}
		
		return ShrinkWrap
				  .create(ZipImporter.class, artifactPath.getFileName().toString())
				  .importFrom(artifactPath.toFile())
				  .as(WebArchive.class);

    }

}
