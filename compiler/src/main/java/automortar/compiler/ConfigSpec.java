package automortar.compiler;

import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ConfigSpec {

    private String daggerServiceName;
    private TypeName screenSuperclassTypeName;

    public String getDaggerServiceName() {
        return daggerServiceName;
    }

    public void setDaggerServiceName(String daggerServiceName) {
        this.daggerServiceName = daggerServiceName;
    }

    public TypeName getScreenSuperclassTypeName() {
        return screenSuperclassTypeName;
    }

    public void setScreenSuperclassTypeName(TypeName screenSuperclassTypeName) {
        this.screenSuperclassTypeName = screenSuperclassTypeName;
    }
}
