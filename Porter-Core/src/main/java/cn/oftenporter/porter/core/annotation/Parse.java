package cn.oftenporter.porter.core.annotation;


import cn.oftenporter.porter.core.base.TypeParser;

import java.lang.annotation.*;

/**
 * 用于标记类型转换的绑定关系。
 * <pre>
 *     注解在类上，是全局的，对所有类参数与函数参数有效。注解在函数上的优先级大于类上的。
 * </pre>
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Parse
{
    /**
     * 需要转换的参数的名称。
     */
    String[] names();

    /**
     * 转换的类。优先于{@linkplain #globals()}。当提供一个时，所有的共有这一个；如果是多个，则必须与{@linkplain #names()}一一对应。
     */
    Class<? extends TypeParser>[] parsers() default {};

    /**
     * 全局转换类的绑定的名称。当提供一个时，所有的共有这一个；如果是多个，则必须与{@linkplain #names()}一一对应。
     */
    String[] globals() default {};
}
