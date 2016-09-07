package cn.oftenporter.porter.core.base;

/**
 * 用于检测。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface CheckPassable
{
    public enum Type
    {
        /**
         * 没有初始化任何参数。
         */
        GLOBAL,
        /**
         * 类参数已经准备完成。
         */
        CLASS,
        /**
         * 函数参数已经准备完成。
         */
        METHOD
    }

    /**
     * 返回null表示通过。
     *
     * @param wObject
     * @param type
     * @return
     */
    Object willPass(WObject wObject, Type type);
}
