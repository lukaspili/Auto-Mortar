package mvp.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MvpConfiguration {

    boolean butterknife() default true;

    Class<?> screenSuperclass() default void.class;

    String daggerServiceName() default DefaultMvpConfiguration.DAGGER_SERVICE_NAME;

}
