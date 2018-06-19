package org.arquillian.container.chameleon.deployment.gradle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GradleBuild {

    /**
     * Gradle Project directory to run the build from
     * @return radle Project directory relative to current module. By default current directory is used.
     */
    String path() default ".";
    
    /**
     * Tasks to execute for building archive.
     * @return List of goals
     */
    String[] tasks() default "build";

    /**
     * In case of disabling quiet mode, Gradle output is printed to console.
     * The output is generated with "--info --stacktrace".
     * @return 
     */
    boolean quiet() default true;
}
