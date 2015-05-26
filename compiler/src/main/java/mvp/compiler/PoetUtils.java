package mvp.compiler;

import com.google.common.base.Preconditions;

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
}
