package cn.oftenporter.porter.core.annotation;

import java.lang.annotation.*;

/**
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
    @Target({ElementType.FIELD})
    @Inherited
    @Documented
    public @interface InNece
    {

    }

    /**
     * 对私有字段也有效。
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @Inherited
    @Documented
    public @interface InUnNece
    {

    }

    /**
     * 对于接口的属性默认是否为必须值。
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
