package mvp.compiler;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import mvp.compiler.model.spec.AutoComponentMemberSpec;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class SpecUtils {

    public static Object[] getTypeNames(List<AutoComponentMemberSpec> specs) {
        List<TypeName> typeNames = new ArrayList<>();
        for (AutoComponentMemberSpec spec : specs) {
            typeNames.add(spec.getTypeName());
        }

        return typeNames.toArray();
    }
}
