package cn.oftenporter.porter.core.annotation;

import java.lang.annotation.*;

/**
 * Created by 宇宙之灵 on 2016/8/31.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface PortStart
{
}
