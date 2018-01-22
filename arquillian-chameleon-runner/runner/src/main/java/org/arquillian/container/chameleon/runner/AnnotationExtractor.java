package org.arquillian.container.chameleon.runner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;
import org.arquillian.container.chameleon.api.ChameleonTarget;

public class AnnotationExtractor {

    public static ChameleonTargetConfiguration extract(Annotation annotation) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();

        ChameleonTargetConfiguration currentConfiguration = null;

        if (annotationType == ChameleonTarget.class) {
            ChameleonTarget chameleonTarget = (ChameleonTarget) annotation;
            return ChameleonTargetConfiguration.from(chameleonTarget);
        }

        final Annotation[] metaAnnotations = findAndSortAnnotations(annotation);

        for (Annotation metaAnnotation : metaAnnotations) {
            if (! metaAnnotation.annotationType().getName().startsWith("java.lang")) {
                final ChameleonTargetConfiguration chameleonTargetConfiguration = extract(metaAnnotation);
                if (currentConfiguration != null) {
                    currentConfiguration = currentConfiguration.importConfiguration(chameleonTargetConfiguration);
                } else {
                    currentConfiguration = chameleonTargetConfiguration;
                }
            }
        }
        return currentConfiguration;
    }

    /**
     * We need to sort annotations so the first one processed is ChameleonTarget so these properties has bigger preference that the inherit ones.
     * @param annotation
     * @return
     */
    static Annotation[] findAndSortAnnotations(Annotation annotation) {
        final Annotation[] metaAnnotations = annotation.annotationType().getAnnotations();
        Arrays.sort(metaAnnotations, new Comparator<Annotation>() {
            @Override
            public int compare(Annotation o1, Annotation o2) {
                if (o1 instanceof ChameleonTarget) {
                    return -1;
                }

                if (o2 instanceof ChameleonTarget) {
                    return 1;
                }

                return 0;

            }
        });
        return metaAnnotations;
    }
}
