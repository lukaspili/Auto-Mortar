package mvp;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MVP {

    Class<?> parentComponent();

    Class<?> view() default void.class;

    Class<?> baseViewLayout() default void.class;

    Class<? extends Annotation>[] screenAnnotations() default {};
}
