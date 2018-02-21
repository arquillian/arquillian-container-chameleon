package org.arquillian.container.chameleon.deployment.api;

import java.lang.annotation.Annotation;
import org.jboss.arquillian.container.test.api.DeploymentConfiguration;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;

public class DeploymentConfigurationPopulator {

    public static DeploymentConfiguration.DeploymentContentBuilder populate(TestClass testClass, Archive<?> archive) {

        DeploymentParameters deploymentParameters = resolveDeployment(testClass);

        DeploymentConfiguration.DeploymentContentBuilder deploymentContentBuilder =
            new DeploymentConfiguration.DeploymentContentBuilder(archive);
        final DeploymentConfiguration.DeploymentBuilder deploymentBuilder = deploymentContentBuilder.withDeployment()
            .withManaged(deploymentParameters.managed())
            .withOrder(deploymentParameters.order())
            .withTestable(deploymentParameters.testable());

        if (isNotEmptyOrNull(deploymentParameters.deploymentName())) {
            deploymentBuilder.withName(deploymentParameters.deploymentName());
        }
        deploymentContentBuilder = deploymentBuilder.build();

        if (isNotEmptyOrNull(deploymentParameters.overProtocol())) {
            deploymentContentBuilder.withOverProtocol(deploymentParameters.overProtocol());
        }

        if (isNotEmptyOrNull(deploymentParameters.targetsContainer())) {
            deploymentContentBuilder.withTargetsContainer(deploymentParameters.targetsContainer());
        }

        if (deploymentParameters.shouldThrowExcetionClass() != ConstantException.class) {
            deploymentContentBuilder.withShouldThrowException(deploymentParameters.shouldThrowExcetionClass(),
                deploymentParameters.testable());
        }

        return deploymentContentBuilder;
    }

    private static DeploymentParameters resolveDeployment(TestClass testClass) {
        if (testClass.isAnnotationPresent(DeploymentParameters.class)) {
            return testClass.getAnnotation(DeploymentParameters.class);
        } else {
            return new DeploymentParametersAnnotationClass();
        }
    }

    private static boolean isNotEmptyOrNull(String value) {
        // When we update Chameleon to Java 8 this can be changed to isEmpty
        return value != null && value.length() > 0;
    }

    static class DeploymentParametersAnnotationClass implements DeploymentParameters {

        @Override
        public String deploymentName() {
            return "";
        }

        @Override
        public boolean testable() {
            return true;
        }

        @Override
        public boolean managed() {
            return true;
        }

        @Override
        public int order() {
            return -1;
        }

        @Override
        public String overProtocol() {
            return "";
        }

        @Override
        public String targetsContainer() {
            return "";
        }

        @Override
        public Class<? extends Exception> shouldThrowExcetionClass() {
            return ConstantException.class;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return DeploymentParameters.class;
        }
    }
}
