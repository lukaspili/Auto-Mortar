package automortar.sample.app;

import automortar.sample.rest.RestClient;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public interface AppDependencies {

    RestClient restClient();
}
