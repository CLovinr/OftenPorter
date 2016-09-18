package cn.oftenporter.porter.core.annotation;


import cn.oftenporter.porter.core.apt.AutoGen;
import cn.oftenporter.porter.core.base.ITypeParser;

import java.lang.annotation.*;

/**
 * <pre>
 * 用于自动生成类或接口对象。对于该方式的参数类型绑定是全局的，例如有一个类A，其所有字段的{@linkplain ITypeParser}在一个地方被绑定了，那么在另一个地方则无需进行绑定。
 * 1.对于类，必须是非抽象类且含有无参构造函数。
 * 2.对于接口是以下形式:(默认为必需参数,需要用到Annotation Processor机制，见{@linkplain AutoGen})
 * 以get或is开头的变量名是去掉get或is再把第一个字符变为小写。
 * &#64;AutoGen
 * interface IDemo{
 *     String getName();
 *     boolean isOk();
 * }
 * </pre>
 * <p>
 * <strong>注意：</strong>对于此方式注解的绑定类，如果某些变量没有被绑定{@linkplain ITypeParser}，则会从类或函数的{@linkplain Parser.parse}中获取绑定；
 * 并且绑定之后，在其他地方使用该类时，不需要要绑定了，因为类的绑定是<strong>全局的(针对所有context)</strong>.
 * </p>
 * Created by https://github.com/CLovinr on 2016/9/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
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
    public @interface Nece
    {
        /**
         * 是否自动绑定类型转换。默认为true。支持自动的绑定的类型见包{@linkplain cn.oftenporter.porter.simple.parsers}。
         */
        boolean autoParse() default true;

        /**
         * 为""表示使用变量的名称。
         *
         * @return
         */
        String value() default "";
    }

    /**
     * 对私有字段也有效。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Inherited
    @Documented
    public @interface UnNece
    {
        /**
         * 见{@linkplain Nece#autoParse()}
         *
         * @return
         */
        boolean autoParse() default true;

        /**
         * 为""表示使用变量的名称。
         *
         * @return
         */
        String value() default "";
    }


    /**
     * 类或接口。
     * <br>
     *
     * @return
     */
    Class<?>[] value() default {};
}
