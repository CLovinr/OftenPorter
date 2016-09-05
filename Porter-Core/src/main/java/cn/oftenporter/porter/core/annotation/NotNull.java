package cn.oftenporter.porter.core.annotation;

import java.lang.annotation.*;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Inherited
public @interface NotNull
{
}
