package mvp.compiler.names;

import com.google.auto.common.MoreElements;
import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;

/**
 * All the logic about class names for a specific element in one place
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ClassNames {

    public static final String SCREEN_PREFIX = "MVP_";
    public static final String SCREEN_NAME = "Screen";
    public static final String PRESENTER_NAME = "Presenter";
    public static final String COMPONENT_NAME = "Component";
    public static final String MODULE_NAME = "Module";
    public static final String CONFIG_NAME = "MVP_Config";

    public static ClassName butterknife() {
        return ClassName.get("butterknife", "ButterKnife");
    }

    public static ClassName context() {
        return ClassName.get("android.content", "Context");
    }

    public static ClassName attributeSet() {
        return ClassName.get("android.util", "AttributeSet");
    }

    public static final ClassName mvpConfig() {
        return ClassName.get("generatedmvp", CONFIG_NAME);
    }

    private final Element element;
    private final String elementPackage;
    private final ClassName screenClassName;
    private final ClassName componentClassName;
    private final ClassName moduleClassName;
    private final ClassName daggerComponentClassName;
    private final ClassName presenterClassName;


    public ClassNames(Element element) {
        Preconditions.checkNotNull(element);

        this.element = element;

        elementPackage = MoreElements.getPackage(element).getQualifiedName().toString();
        screenClassName = ClassName.get(elementPackage, buildScreenName());
        componentClassName = buildScreenInnerClassName(COMPONENT_NAME);
        moduleClassName = buildScreenInnerClassName(MODULE_NAME);
        daggerComponentClassName = ClassName.get(elementPackage, String.format("Dagger%s_%s", screenClassName.simpleName(), componentClassName.simpleName()));
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

    public ClassName getDaggerComponentClassName() {
        return daggerComponentClassName;
    }

    public ClassName getPresenterClassName() {
        return presenterClassName;
    }
}
