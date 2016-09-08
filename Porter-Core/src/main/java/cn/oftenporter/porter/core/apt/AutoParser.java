package cn.oftenporter.porter.core.apt;

import java.lang.annotation.*;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface AutoParser
{
}
