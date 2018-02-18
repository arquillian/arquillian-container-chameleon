package org.arquillian.container.chameleon.deployment.maven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MavenBuildDeployment {

    String[] mavenGoals() default "package";
    String mavenVersion() default "3.5.0";
    String[] mavenProfiles() default {};
    String pom() default "pom.xml";

    String deploymentName() default "";
    boolean testable() default true;
    boolean managed() default true;
    int order() default -1;

    String overProtocol() default "";

    String targetsContainer() default "";

    Class<? extends Exception> shouldThrowExcetionClass() default ConstantException.class;

}
