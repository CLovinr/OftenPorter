package cn.oftenporter.porter.core.base;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public enum OutType
{
    /**
     * 无错误情况下，框架不会输出。
     */
    NoResponse,

    /**
     * 输出返回值，返回结果为null或返回类型为void则不会输出内容。
     */
    Object
}
