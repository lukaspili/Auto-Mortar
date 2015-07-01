package automortar.compiler;

/**
 * Pass data between processing steps
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class State {

    private ConfigSpec configSpec;

    public ConfigSpec getConfigSpec() {
        return configSpec;
    }

    public void setConfigSpec(ConfigSpec configSpec) {
        this.configSpec = configSpec;
    }
}
