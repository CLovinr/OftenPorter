package cn.oftenporter.porter.core.annotation;

import java.lang.annotation.*;

/**
 * 用于标记函数，在销毁时调用。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface PortDestroy
{
}
