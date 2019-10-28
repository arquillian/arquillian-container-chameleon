package org.arquillian.container.chameleon;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.arquillian.container.chameleon.api.ChameleonTarget;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ChameleonTarget("tomee:7.0.5:managed")
public @interface Tomee {
}
