package cn.oftenporter.porter.core.annotation;

import cn.oftenporter.porter.core.init.PorterConf;

import java.lang.annotation.*;

/**
 * 用于自动设置变量
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
@Documented
public @interface AutoSet
{
    /**
     * 为""表示使用当前的变量类型，通过反射来构造对象。否则从全局对象中查找(从{@linkplain PorterConf#addGlobalAutoSetObject(String, Object)}设置的对象)。
     *
     * @return
     */
    String value() default "";

    /**
     * 在为""的情况下，表示是否使用一个实例，默认为true。
     *
     * @return
     */
    boolean oneInstance() default true;
}
