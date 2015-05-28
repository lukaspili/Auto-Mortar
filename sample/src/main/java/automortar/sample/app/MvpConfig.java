package automortar.sample.app;

import flownavigation.path.Path;
import automortar.config.AutoMortarConfig;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoMortarConfig(
        screenSuperclass = Path.class
)
interface MvpConfig {
}