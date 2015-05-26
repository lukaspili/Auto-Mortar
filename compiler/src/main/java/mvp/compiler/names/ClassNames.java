package mvp.compiler.names;

import com.google.auto.common.MoreElements;
import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

/**
 * Names and names
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ClassNames {

    public static final String SCREEN_PREFIX = "MVP_";
    public static final String SCREEN_NAME = "Screen";
    public static final String PRESENTER_NAME = "Presenter";
    public static final String MODULE_NAME = "Module";
    public static final String CONFIG_NAME = "MVP_Config";

    public static ClassName context() {
        return ClassName.get("android.content", "Context");
    }

    public static final ClassName mvpConfig() {
        return ClassName.get("generatedmvp", CONFIG_NAME);
    }

    public static ClassName daggerComponent(ClassName component) {
        return ClassName.get(component.packageName(), String.format("Dagger%s", component.simpleName()));
    }

    private final Element element;
    private final String elementPackage;
    private final ClassName screenClassName;
    private final ClassName componentClassName;
    private final ClassName moduleClassName;
    private final ClassName presenterClassName;


    public ClassNames(Element element) {
        Preconditions.checkNotNull(element);

        this.element = element;

        elementPackage = MoreElements.getPackage(element).getQualifiedName().toString();
        screenClassName = ClassName.get(elementPackage, buildScreenName());
        componentClassName = ClassName.get(elementPackage, String.format("%s_Component", screenClassName.simpleName()));
        moduleClassName = buildScreenInnerClassName(MODULE_NAME);
        presenterClassName = ClassName.get(elementPackage, element.getSimpleName().toString());
    }

    private String buildScreenName() {
        Preconditions.checkNotNull(element);

        String name = element.getSimpleName().toString();

        // try to remove the "Presenter" at the end if any
        int res = name.lastIndexOf(PRESENTER_NAME);
        if (res != -1 && res + PRESENTER_NAME.length() == name.length()) {
            name = name.substring(0, res);
        }
        return String.format("%s%s%s", SCREEN_PREFIX, name, SCREEN_NAME);
    }

    private ClassName buildScreenInnerClassName(String name) {
        return ClassName.get(elementPackage, screenClassName.simpleName(), name);
    }

    public ClassName getScreenClassName() {
        return screenClassName;
    }

    public ClassName getComponentClassName() {
        return componentClassName;
    }

    public ClassName getModuleClassName() {
        return moduleClassName;
    }

    public ClassName getPresenterClassName() {
        return presenterClassName;
    }
}
