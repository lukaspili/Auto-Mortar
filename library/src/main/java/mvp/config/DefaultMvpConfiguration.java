package mvp.config;

import java.lang.annotation.Annotation;

import mvp.DaggerService;

/**
 * Default configuration applied in none is provided
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class DefaultMvpConfiguration implements MvpConfiguration {

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
        return DaggerService.SERVICE_NAME;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return DefaultMvpConfiguration.class;
    }
}
