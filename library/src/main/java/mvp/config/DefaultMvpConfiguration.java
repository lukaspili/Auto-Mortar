package mvp.config;

import java.lang.annotation.Annotation;

/**
 * Default configuration applied in none is provided
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class DefaultMvpConfiguration implements MvpConfiguration {

    public static final String DAGGER_SERVICE_NAME = "mvp.DaggerService";

    @Override
    public boolean butterknife() {
        return false;
    }

    @Override
    public Class<?> screenSuperclass() {
        return null;
    }

    @Override
    public String daggerServiceName() {
        return DAGGER_SERVICE_NAME;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return DefaultMvpConfiguration.class;
    }
}
