package automortar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface ScreenParam {

}
