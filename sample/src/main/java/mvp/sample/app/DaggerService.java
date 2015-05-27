package mvp.sample.app;

import android.content.Context;

import mvp.config.DefaultAutoMortarConfig;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class DaggerService {

    public static final String SERVICE_NAME = DefaultAutoMortarConfig.DAGGER_SERVICE_NAME;

    /**
     * Caller is required to know the type of the component for this context.
     */
    @SuppressWarnings("unchecked") //
    public static <T> T getDaggerComponent(Context context) {
        return (T) context.getSystemService(SERVICE_NAME);
    }
}
