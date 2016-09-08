package cn.oftenporter.porter.core;


import cn.oftenporter.porter.core.annotation.AutoSet;
import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.util.PackageUtil;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
        seek(porterConf.getSeekPackages(), porterConf.getGlobalAutoSetMap());
        return this;
    }

    private PortContext seek(@NotNull SeekPackages seekPackages, Map<String, Object> globalAutoSetMap)
    {
        return seek(seekPackages.getPackages(), globalAutoSetMap);
    }

    private PortContext seek(@NotNull JSONArray packages, Map<String, Object> globalAutoSetMap)
    {
        if (classLoader != null)
        {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        Map<Class<?>, Object> oneInstanceMap = new HashMap<>();
        for (int i = 0; i < packages.size(); i++)
        {
            seekPackage(packages.getString(i), globalAutoSetMap, oneInstanceMap);
        }
        return this;
    }

    private void autoSetObject(Object object, Map<String, Object> globalAutoSetMap,
            Map<Class<?>, Object> oneInstanceMap) throws Exception
    {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            Field f = fields[i];
            if (!f.isAnnotationPresent(AutoSet.class))
            {
                continue;
            }
            AutoSet autoSet = f.getAnnotation(AutoSet.class);
            f.setAccessible(true);
            String keyName = autoSet.value();
            Object value;
            if ("".equals(keyName))
            {
                Class<?> type = f.getType();

                if (autoSet.oneInstance())
                {
                    if (oneInstanceMap.containsKey(type))
                    {
                        value = oneInstanceMap.get(type);
                    } else
                    {
                        value = PortUtil.newObject(f.getType());
                        oneInstanceMap.put(type, value);
                    }
                } else
                {
                    value = PortUtil.newObject(f.getType());
                }
            } else
            {
                value = globalAutoSetMap.get(keyName);
                if (value == null)
                {
                    throw new RuntimeException("globalAutoSet Object for '" + keyName + " is null!");
                }

            }
            f.set(object, value);
        }
    }

    private void seekPackage(String packageStr, Map<String, Object> globalAutoSetMap,
            Map<Class<?>, Object> oneInstanceMap)
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
                    autoSetObject(porter, globalAutoSetMap, oneInstanceMap);
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
