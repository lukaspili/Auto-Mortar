package automortar.compiler.poet;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.TypeName;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import automortar.compiler.model.spec.AutoComponentMemberSpec;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
final class PoetUtils {

    /**
     * Name's sucks
     *
     * @return something like {T.class, T.class, (x count)} or just T.class if only one
     */
    public static String getStringOfClassArrayTypes(int count) {
        Preconditions.checkArgument(count > 0, "getStringOfClassArrayTypes count must be > 0, but is %d", count);

        if (count == 1) {
            return "$T.class";
        }

        StringBuilder builder = new StringBuilder("{");
        for (int i = 0; i < count; ++i) {
            builder.append("$T.class");

            if (i < count - 1) {
                builder.append(", ");
            }
        }

        builder.append("}");
        return builder.toString();
    }

    public static Object[] getTypeNames(List<AutoComponentMemberSpec> specs) {
        List<TypeName> typeNames = new ArrayList<>();
        for (AutoComponentMemberSpec spec : specs) {
            typeNames.add(spec.getTypeName());
        }

        return typeNames.toArray();
    }
}
