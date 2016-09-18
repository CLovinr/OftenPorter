package cn.oftenporter.porter.core.base;

/**
 * 用于检测。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface CheckPassable
{
    public enum DuringType
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
     * @param wObject 不同的检测时期，内部初始化情况不同，见{@linkplain DuringType}
     * @param type    检测的时期
     * @return 返回null表示通过，不为null（错误信息）表示无法通过。
     */
    Object willPass(WObject wObject, DuringType type);
}
