package cn.oftenporter.porter.core.base;

import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.Delivery;

/**
 * 接口中间对象。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public abstract class WObject
{

    /**
     * 类必须参数值数组。
     */
    public Object[] cn;
    /**
     * 类非必需参数值数组。
     */
    public Object[] cu;
    public Object[] cinner;
    /**
     * 类参数名称对象。
     */
    public InNames cInNames;

    /**
     * 函数必须参数值数组。
     */
    public Object[] fn;
    /**
     * 函数非必需参数值数组。
     */
    public Object[] fu;
    public Object[] finner;
    /**
     * 函数参数名称对象。
     */
    public InNames fInNames;

    /**
     * 如果当前是rest，则其值为""或非空。
     */
    public String restValue;

    public abstract WRequest getRequest();

    public abstract WResponse getResponse();

    /**
     * 获取函数上绑定的对象。
     */
    public abstract <T> T finObject(Class<T> clazz, int index);

    /**
     * 获取类上绑定的对象。
     */
    public abstract <T> T cinObject(Class<T> clazz, int index);

    /**
     * 见{@linkplain #savedObject(String) savedObject(Class.getName())}.
     */
    public <T> T savedObject(Class<T> key)
    {
        Object obj = savedObject(key.getName());
        return (T) obj;
    }

    /**
     * 见{@linkplain #savedObject(String)}
     */
    public <T> T savedObject(Class<T> type, String key)
    {
        Object obj = savedObject(key);
        return (T) obj;
    }

    /**
     * 获取当前context运行期对象实例。
     * 见{@linkplain PorterConf#addContextRuntimeObject(String, Object)}
     */
    public abstract Object savedObject(String key);


    /**
     * 见{@linkplain #gsavedObject(String) gsavedObject(Class.getName())}.
     */
    public <T> T gsavedObject(Class<T> key)
    {
        Object obj = gsavedObject(key.getName());
        return (T) obj;
    }

    /**
     * 见{@linkplain #gsavedObject(String)}
     */
    public <T> T gsavedObject(Class<T> type, String key)
    {
        Object obj = gsavedObject(key);
        return (T) obj;
    }

    /**
     * 获取全局运行期对象.
     * 见{@linkplain CommonMain#addGlobalAutoSet(String, Object)}
     */
    public abstract Object gsavedObject(String key);

    public abstract Delivery delivery();

    public abstract UrlDecoder.Result url();

}
