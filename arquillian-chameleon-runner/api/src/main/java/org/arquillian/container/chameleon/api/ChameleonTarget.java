package org.arquillian.container.chameleon.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * {@code ChameleonTarget} is used to configure Chameleon container, but using an annotation instead of arquillian.xml.
 *
 * The annotation can be used as meta-annotation of other annotations recursively. For example:
 *
 * <pre>
 * {@code
 * @Target({ ElementType.TYPE})
 * @Retention(RetentionPolicy.RUNTIME)
 * @Documented
 * @Inherited
 * @Tomcat
 * @ChameleonTarget(version = "8.0.0")
 * public @interface Tomcat8 {
 * }
 * </pre>
 *
 * might inherit properties set from {@code Tomcat} annotation and override the version field with the one specified.
 *
 * And {@code Tomcat} annotation looks like:
 *
 * <pre>
 * {@code
 * @Target({ ElementType.TYPE})
 * @Retention(RetentionPolicy.RUNTIME)
 * @Documented
 * @Inherited
 * @ChameleonTarget("tomcat:7.0.0:managed")
 * public @interface Tomcat {
 * }
 * </pre>
 *
 * All fields accept expressions like ${property:defaultValue} where {@code property} is first resolved as environment variable,
 * if not set as system property and if not the default value is used.
 *
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ChameleonTarget {

    /**
     * Sets Chameleon Target with form container:version:mode. For example: wildfly:9.0.0:manged.
     * @return Chameleon Target
     */
    String value() default "";

    /**
     * If {@code value} is not use, this property sets the container field.
     * @return Container.
     */
    String container() default "";

    /**
     * If {@code value} is not use, this property sets the version field.
     * @return Version.
     */
    String version() default "";

    /**
     * If {@code value} is not use, this property sets the mode field.
     * @return Mode.
     */
    String mode() default "managed";

    /**
     * Sets custom properties for container definition.
     * @return Custom Properties.
     */
    Property[] customProperties() default {};
}
