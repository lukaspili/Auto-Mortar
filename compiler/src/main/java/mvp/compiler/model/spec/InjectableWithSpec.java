package mvp.compiler.model.spec;

import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class InjectableWithSpec {

    private final String name;
    private final TypeName typeName;

    public InjectableWithSpec(String name, TypeName typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public TypeName getTypeName() {
        return typeName;
    }
}
