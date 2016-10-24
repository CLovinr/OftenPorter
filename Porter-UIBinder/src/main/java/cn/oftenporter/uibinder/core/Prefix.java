package cn.oftenporter.uibinder.core;


import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.PortUtil;

/**
 * Created by ZhuiFeng on 2015/6/13.
 */
public class Prefix
{


    /**
     * id名称前缀
     */
    public final String idPrefix;

    /**
     * 接口路径前缀
     */
    public final String pathPrefix;

    public final ErrListener errListener;

    /**
     * 绑定完成后用于调用的接口,且会传人view;只调用一次。
     */
    public final String bindCallbackMethod;


    @Override
    public String toString()
    {
        return idPrefix + "" + pathPrefix + ",callback=" + bindCallbackMethod;
    }

    public static Prefix forDelete(String contextName, Class<?> clazz, boolean enableDefaultValue)
    {
        return new Prefix(null, "/" + contextName + "/" + classTied(clazz, enableDefaultValue) + "/", null, null);
    }

    public static Prefix forDelete(String porterPrefix)
    {
        return new Prefix(null, porterPrefix, null, null);
    }

    /**
     * @param idPrefix           代表id的内容前缀
     * @param pathPrefix         调用的接口路径前缀
     * @param bindCallbackMethod 绑定完成后用于调用的接口。
     * @param errListener        错误监听器
     */
    public Prefix(String idPrefix, String pathPrefix, String bindCallbackMethod, ErrListener errListener)
    {
        this.idPrefix = idPrefix;
        this.pathPrefix = pathPrefix;
        this.bindCallbackMethod = bindCallbackMethod;
        this.errListener = errListener;
    }

    private static String classTied(Class<?> clazz, boolean enableDefaultValue)
    {
        PortIn portIn = clazz.getAnnotation(PortIn.class);
        if (portIn == null)
        {
            throw new RuntimeException(
                    "class [" + clazz.getName() + "] not with annotation of @" + PortIn.class.getName());
        }
        String tied = PortUtil.tied(portIn, clazz, enableDefaultValue);
        return tied;
    }

    /**
     * 规则为:
     * <pre>
     *     绑定名为："TiedName"
     *     1.idPrefix:"tiedName_"
     *     2.porterPrefix:"/TiedName/"
     *     3.callback:{@linkplain BinderDefault#CALLBACK}
     * </pre>
     *
     * @param c                  接口类
     * @param enableDefaultValue 是否允许类的默认绑定名。
     * @return 返回构造的对象
     */
    public static Prefix buildPrefix(String contextName, Class<?> c, boolean enableDefaultValue)
    {
        String tied = classTied(c, enableDefaultValue);
        Prefix prefix = new Prefix(
                tied.substring(0, 1).toLowerCase() + tied.substring(1) + "_",
                "/" + contextName + "/" + tied + "/", BinderDefault.CALLBACK, null);
        return prefix;
    }


}
