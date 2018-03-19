package org.arquillian.container.chameleon.deployment.maven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MavenBuild {

    String[] goals() default "package";
    String version() default "3.5.0";
    String[] profiles() default {};
    String pom() default "pom.xml";
    String localRepositoryDirectory() default "";
    boolean offline() default false;
    String mvnOpts() default "";
    String[] properties() default {};
    boolean quiet() default true;

}
