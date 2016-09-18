package cn.oftenporter.porter.core;


import cn.oftenporter.porter.core.annotation.AutoSet;
import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.PorterBridge;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.util.PackageUtil;
import cn.oftenporter.porter.core.util.WPTool;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

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
    private boolean enableDefaultValue;

    private static class Temp
    {
        Map<String, Object> contextOneInstanceMap = new HashMap<>();
        Map<String, Object> globalOneInstanceMap;
        PorterBridge bridge;
        PortInObjConf portInObjConf;

        public Temp(Map<String, Object> contextAutoSetMap, Map<String, Object> globalOneInstanceMap,
                PorterBridge bridge, PortInObjConf portInObjConf)
        {
            this.contextOneInstanceMap = contextAutoSetMap;
            this.globalOneInstanceMap = globalOneInstanceMap;
            this.bridge = bridge;
            this.portInObjConf = portInObjConf;
        }
    }

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

    public PortContext initSeek(PorterConf porterConf,TypeParserStore globalParserStore, Map<String, Object> globalOneInstanceMap, PorterBridge bridge)
    {

        if (globalParserStore == null)
        {
            throw new NullPointerException();
        }

        this.enableDefaultValue = porterConf.isEnableTiedNameDefault();

        Temp temp = new Temp(porterConf.getContextAutoSetMap(), globalOneInstanceMap, bridge,
                new PortInObjConf(globalParserStore, classLoader, porterConf.getContextAutoGenImplMap(),
                        porterConf.isEnableTiedNameDefault()));
        seek(porterConf.getSeekPackages().getPackages(), temp);

        Set<Class<?>> forSeek = porterConf.getSeekPackages().getClassesForSeek();
        for (Class<?> clazz : forSeek)
        {
            LOGGER.debug("may add porter:{}", clazz);
            try
            {
                mayAddPorter(clazz, temp);
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }

        Set<Object> objectSet = porterConf.getSeekPackages().getObjectsForSeek();
        for (Object object : objectSet)
        {
            LOGGER.debug("may add porter:{}:{}", object.getClass(), object);
            try
            {
                if (object.getClass().isAnnotationPresent(PortIn.class))
                {
                    addPorter(object, temp);
                }
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return this;
    }

    private void seek(@NotNull JSONArray packages, Temp temp)
    {
        if (classLoader != null)
        {
            Thread.currentThread().setContextClassLoader(classLoader);
        }

        for (int i = 0; i < packages.size(); i++)
        {
            seekPackage(packages.getString(i), temp);
        }
    }

    /**
     * 变量自动设置。
     */
    private void autoSetObject(Object object, Temp temp) throws Exception
    {
        Map<String, Object> contextOneInstanceMap = temp.contextOneInstanceMap;
        Map<String, Object> globalOneInstanceMap = temp.globalOneInstanceMap;
        PorterBridge bridge = temp.bridge;
        Field[] fields = WPTool.getAllFields(object.getClass());
        for (int i = 0; i < fields.length; i++)
        {
            Field f = fields[i];
            if (!f.isAnnotationPresent(AutoSet.class))
            {
                continue;
            }
            f.setAccessible(true);
            if (isDefaultAutoSetObject(f, object, bridge, temp.portInObjConf))
            {
                continue;
            }
            AutoSet autoSet = f.getAnnotation(AutoSet.class);

            String keyName;
            Class<?> mayNew = null;
            Class<?> classClass = autoSet.classValue();
            if (classClass.equals(AutoSet.class))
            {
                keyName = autoSet.value();
            } else
            {
                keyName = classClass.getName();
                mayNew = classClass;
            }
            if ("".equals(keyName))
            {
                keyName = f.getType().getName();
            }
            if (mayNew == null)
            {
                mayNew = f.getType();
            }
            Object value = null;

            try
            {
                switch (autoSet.range())
                {
                    case Global:
                    {
                        value = globalOneInstanceMap.get(keyName);
                        if (value == null)
                        {
                            value = WPTool.newObject(mayNew);
                            globalOneInstanceMap.put(keyName, value);
                        }
                    }
                    break;
                    case Context:
                    {
                        value = contextOneInstanceMap.get(keyName);
                        if (value == null)
                        {
                            value = WPTool.newObject(mayNew);
                            contextOneInstanceMap.put(keyName, value);
                        }
                    }
                    break;
                    case New:
                    {
                        value = WPTool.newObject(mayNew);
                    }
                    break;
                }
            } catch (Exception e)
            {
                LOGGER.error("AutoSet failed for [{}]({}),ex={}", f, autoSet.range(), e.getMessage());
            }
            if (value == null)
            {
                continue;
            }
            f.set(object, value);
        }
    }

    /**
     * 是否是默认工具类。
     */
    private boolean isDefaultAutoSetObject(Field f, Object object, PorterBridge bridge,
            PortInObjConf portInObjConf) throws IllegalAccessException
    {
        if (!f.getType().getName().equals(TypeTo.class.getName()))
        {
            return false;
        }

        TypeTo typeTo = new TypeTo(bridge.paramDealt(), portInObjConf);
        f.set(object, typeTo);
        return true;
    }

    private void seekPackage(String packageStr, Temp temp)
    {
        LOGGER.debug("扫描包：{}", packageStr);
        List<String> classeses = PackageUtil.getClassName(packageStr);
        for (int i = 0; i < classeses.size(); i++)
        {
            try
            {
                Class<?> clazz = PackageUtil.newClass(classeses.get(i), classLoader);
                mayAddPorter(clazz, temp);
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void mayAddPorter(Class<?> clazz, Temp temp) throws Exception
    {
        if ((!Modifier.isAbstract(clazz.getModifiers())) && clazz.isAnnotationPresent(PortIn.class))
        {
            Object porter = WPTool.newObject(clazz);
            addPorter(porter, temp);
        }
    }


    private void addPorter(Object porter, Temp temp) throws Exception
    {
        Class<?> clazz = porter.getClass();
        LOGGER.debug("添加接口：");
        LOGGER.debug("at " + clazz.getName() + ".<init>(" + clazz.getSimpleName() + ".java:1)");
        autoSetObject(porter, temp);

        WPort port = new WPort(classLoader);
        port.initStatic(porter, checks, temp.portInObjConf, enableDefaultValue);
        if (portMap.containsKey(port.getTiedName()))
        {
            LOGGER.warn("the class tiedName '{}' added before.(current:{},last:{})", port.getTiedName(),
                    port.getPortObject(), portMap.get(port.getTiedName()).getPortObject());
        }
        portMap.put(port.getTiedName(), port);
    }

    WPort getClassPort(String classTied)
    {
        return portMap.get(classTied);
    }

//    TypeParserStore getTypeParserStore()
//    {
//        return typeParserStore;
//    }

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
