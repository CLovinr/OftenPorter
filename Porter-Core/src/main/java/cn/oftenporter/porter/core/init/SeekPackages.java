package cn.oftenporter.porter.core.init;


import com.alibaba.fastjson.JSONArray;

import java.util.HashSet;
import java.util.Set;

/**
 * 存放扫描包的对象。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class SeekPackages
{
    private JSONArray jsonArray = new JSONArray();
    private Set<Class<?>> classesForSeek;
    private Set<Object> objectsForSeek;

    public SeekPackages()
    {
        classesForSeek = new HashSet<>();
        objectsForSeek = new HashSet<>();
    }

    /**
     * 添加扫描的接口类。
     *
     * @param clazz 待扫描的类
     * @return
     * @throws NullPointerException clazz为null。
     */
    public SeekPackages addClassPorter(Class<?> clazz) throws NullPointerException
    {
        if (clazz == null)
        {
            throw new NullPointerException();
        }
        classesForSeek.add(clazz);
        return this;
    }

    /**
     * 添加扫描的接口对象。
     *
     * @param object 待扫描的类
     * @return
     * @throws NullPointerException object为null。
     */
    public SeekPackages addObjectPorter(Object object) throws NullPointerException
    {
        if (object == null)
        {
            throw new NullPointerException();
        }
        objectsForSeek.add(object);
        return this;
    }

    public Set<Object> getObjectsForSeek()
    {
        return objectsForSeek;
    }

    public Set<Class<?>> getClassesForSeek()
    {
        return classesForSeek;
    }

    /**
     * 添加待扫描的包。
     *
     * @param packages 包名称，可变参数
     * @return
     */
    public SeekPackages addPorters(String... packages)
    {

        for (int i = 0; i < packages.length; i++)
        {
            jsonArray.add(packages[i]);
        }

        return this;
    }

    public JSONArray getPackages()
    {
        return jsonArray;
    }
}
