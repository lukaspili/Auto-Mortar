package mvp.sample;

import flownavigation.path.Path;
import mvp.config.MvpConfiguration;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@MvpConfiguration(
        screenSuperclass = Path.class
)
interface MvpConfig {
}