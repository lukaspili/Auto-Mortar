package automortar;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public interface ScreenComponentFactory<T_Dependency> {

    Object createComponent(T_Dependency dependency);
}
