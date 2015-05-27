package mvp.compiler.model.spec;

import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class AutoComponentMemberSpec {

    private final TypeName typeName;

    /**
     * TypeName that references the real name of the generated class
     * It's a mess, but working workaround
     */
    private final TypeName realTypeName;

    private final String name;

    public AutoComponentMemberSpec(TypeName typeName, TypeName realTypeName, String name) {
        this.typeName = typeName;
        this.realTypeName = realTypeName;
        this.name = name;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public TypeName getRealTypeName() {
        return realTypeName;
    }

    public String getName() {
        return name;
    }
}
