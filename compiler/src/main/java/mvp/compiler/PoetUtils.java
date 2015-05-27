package mvp.compiler;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.TypeName;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class PoetUtils {

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

    /**
     * TypeName looks like com.my.RootActivity.MyModule
     *
     * @return the correct builder method, like myModule
     */
    public static String daggerComponentBuilderMethodNameForModule(TypeName typeName) {
        Preconditions.checkNotNull(typeName, "TypeName for component method builder cannot be null");
        String full = typeName.toString();

        int loc = full.lastIndexOf(".");
        Preconditions.checkArgument(loc > -1, "Cannot find correct name for component method builder");
        String name = full.substring(loc + 1, full.length());

        return StringUtils.uncapitalize(name);
    }
}
