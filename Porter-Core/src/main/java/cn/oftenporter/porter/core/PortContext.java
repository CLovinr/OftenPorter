package cn.oftenporter.porter.core;


import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.util.PackageUtil;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PortContext
{

    private static final Logger LOGGER = LoggerFactory.getLogger(PortContext.class);
    private ClassLoader classLoader;
    //接口
    private Map<String, WPort> portMap;
    //检测
    private Map<Class<?>, CheckPassable> checks;
    private TypeParserStore typeParserStore;
    private boolean enableDefaultValue;

    public PortContext()
    {
        init();
    }

    private void init()
    {
        portMap = new HashMap<>();
        checks = new HashMap<>();
    }


    /**
     * 设置当前的类加载器。
     *
     * @param classLoader
     * @return
     */
    public PortContext setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
        return this;
    }

    public PortContext initSeek(PorterConf porterConf)
    {

        TypeParserStore typeParserStore = porterConf.getTypeParserStore();
        if (typeParserStore == null)
        {
            throw new NullPointerException();
        }
        this.typeParserStore = typeParserStore;
        this.enableDefaultValue = porterConf.isEnablePortInTiedNameDefault();
        seek(porterConf.getSeekPackages());
        return this;
    }

    private PortContext seek(@NotNull SeekPackages seekPackages)
    {
        return seek(seekPackages.getPackages());
    }

    private PortContext seek(@NotNull JSONArray packages)
    {
        if (classLoader != null)
        {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        for (int i = 0; i < packages.size(); i++)
        {
            seekPackage(packages.getString(i));
        }
        return this;
    }

    private void seekPackage(String packageStr)
    {
        LOGGER.debug("扫描包：{}", packageStr);
        List<String> classeses = PackageUtil.getClassName(packageStr);
        for (int i = 0; i < classeses.size(); i++)
        {
            try
            {
                Class<?> clazz = PackageUtil.newClass(classeses.get(i), classLoader);
                if ((!Modifier.isAbstract(clazz.getModifiers())) && clazz.isAnnotationPresent(PortIn.class))
                {
                    LOGGER.debug("添加接口：");
                    LOGGER.debug("at " + clazz.getName() + ".<init>(" + clazz.getSimpleName() + ".java:1)");
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Object porter = constructor.newInstance();
                    addPorter(porter);
                }
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void addPorter(Object porter)
    {
        WPort port = new WPort();
        port.initStatic(porter, checks, typeParserStore, enableDefaultValue);
        portMap.put(port.getTiedName(), port);
    }

    WPort getClassPort(String classTied)
    {
        return portMap.get(classTied);
    }

    TypeParserStore getTypeParserStore()
    {
        return typeParserStore;
    }

    public void start()
    {
        Iterator<WPort> iterator = portMap.values().iterator();
        while (iterator.hasNext())
        {
            iterator.next().onStart();
        }
    }

    public void destroy()
    {
        Iterator<WPort> iterator = portMap.values().iterator();
        while (iterator.hasNext())
        {
            iterator.next().onDestroy();
        }
    }


    CheckPassable getCheckPassable(Class<? extends CheckPassable> clazz)
    {
        return checks.get(clazz);
    }

}
