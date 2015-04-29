package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ScreenAnnotationSpec extends AbstractSpec {

    private Map<String, Object> members;

    public ScreenAnnotationSpec(ClassName className) {
        super(className);
        members = new HashMap<>();
    }

    public Map<String, Object> getMembers() {
        return members;
    }
}
