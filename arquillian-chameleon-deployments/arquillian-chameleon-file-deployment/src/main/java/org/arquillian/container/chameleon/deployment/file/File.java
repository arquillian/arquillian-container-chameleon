package org.arquillian.container.chameleon.deployment.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface File {

    String value();
    Class<? extends Archive> type() default WebArchive.class;

}
