package mvp.standalone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Standalone @MVP
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MVP {

    //TODO: should be optional
    Class<?> parentComponent();

    Class<?> view() default void.class;

    Class<?> baseViewLayout() default void.class;

    Class<?> screenSuperclass() default void.class;
}
