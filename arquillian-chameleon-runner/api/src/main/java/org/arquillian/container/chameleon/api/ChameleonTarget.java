package org.arquillian.container.chameleon.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ChameleonTarget {

    String value() default "";

    String container() default "";
    String version() default "";
    Mode mode() default Mode.MANAGED;
    Property[] customProperties() default {};
}
