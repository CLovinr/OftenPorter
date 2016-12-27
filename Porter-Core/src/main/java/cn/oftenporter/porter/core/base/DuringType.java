package cn.oftenporter.porter.core.base;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/3.
 */
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
    METHOD,

    /**
     * 调用完后,{@linkplain CheckPassable#willPass(WObject, DuringType, Aspect)}的Aspect不为空。
     */
    INVOKED
}
