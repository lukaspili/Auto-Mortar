package automortar.config;

import java.lang.annotation.Annotation;

/**
 * Default configuration applied in none is provided
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class DefaultAutoMortarConfig implements AutoMortarConfig {

    public static final String DAGGER_SERVICE_NAME = "automortar.DaggerService";

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
        return DefaultAutoMortarConfig.class;
    }
}
