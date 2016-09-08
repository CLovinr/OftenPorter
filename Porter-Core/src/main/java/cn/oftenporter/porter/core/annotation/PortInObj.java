package cn.oftenporter.porter.core.annotation;

import java.lang.annotation.*;

/**
 * <pre>
 * 用于自动生成类或接口对象。
 * 1.对于类，必须是非抽象类且含有无参构造函数。
 * 2.对于接口是以下形式:
 * 应该以get或is开头。
 * &#64;AutoParser
 * interface IDemo{
 *     String getName();
 *     boolean isOk();
 * }
 * </pre>
 * Created by https://github.com/CLovinr on 2016/9/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface PortInObj
{
    /**
     * 对私有字段也有效。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Inherited
    @Documented
    public @interface InNece
    {
        /**
         * 是否自动绑定类型转换。默认为true。支持自动的绑定的类型见包{@linkplain cn.oftenporter.porter.simple.parsers}。
         */
        boolean autoParse() default true;
    }

    /**
     * 对私有字段也有效。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Inherited
    @Documented
    public @interface InUnNece
    {
        /**
         * 是否自动绑定类型转换。默认为true。支持自动的绑定的类型见包{@linkplain cn.oftenporter.porter.simple.parsers}。
         */
        boolean autoParse() default true;
    }

    /**
     * 对于接口的属性默认是否为必须值。
     * <br>
     * 可以使用{@linkplain InNece}或{@linkplain InUnNece}标记接口函数。
     *
     * @return 默认返回true。
     */
    boolean defaultNecessary() default true;

    /**
     * 类或接口。
     *
     * @return
     */
    Class<?>[] types() default {};
}
