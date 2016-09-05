package cn.oftenporter.porter.core.base;

/**
 * 用于检测。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface CheckPassable
{
    public enum Type
    {
        GLOBAL,
        CLASS,
        METHOD
    }

    /**
     * 返回null表示通过。
     *
     * @param wpObject
     * @param type
     * @return
     */
    Object willPass(WPObject wpObject, Type type);
}
