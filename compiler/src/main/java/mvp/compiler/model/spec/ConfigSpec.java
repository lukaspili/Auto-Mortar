package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ConfigSpec extends AbstractSpec {

    private String daggerServiceName;

    public ConfigSpec(ClassName className) {
        super(className);
    }

    public String getDaggerServiceName() {
        return daggerServiceName;
    }

    public void setDaggerServiceName(String daggerServiceName) {
        this.daggerServiceName = daggerServiceName;
    }
}
