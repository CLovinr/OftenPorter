package cn.oftenporter.porter.core.base;


import cn.oftenporter.porter.core.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.oftenporter.porter.core.base.BackableSeek.SeekType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 对应于一个接口。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class WPort
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WPort.class);
    private Object port;
    //////////////
    private int argCount = 0;
    private OutType outType;
    /////////////////
    private String tiedName;
    private InNames inNames;
    private PortMethod method;
    private Class<? extends CheckPassable>[] checks;
    private TiedType tiedType;
    private List<Method> starts, destroys;
    private boolean isMultiTiedType;


    private Map<String, WPort> children;

    private WPortInObj wPortInObj;
    private ClassLoader classLoader;


    public WPort(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
        starts = new ArrayList<>(1);
        destroys = new ArrayList<>(1);
    }


    /**
     * 扫描函数。
     */
    private WPort childPort(Method method, Map<Class<?>, CheckPassable> checkPassableMap,
            PortInObjConf portInObjConf, boolean enableDefaultValue, BackableSeek backableSeek)
    {
        WPort port = null;
        try
        {
            method.setAccessible(true);
            PortIn portIn = PortUtil.getAnnotation(method, PortIn.class);
            if (portIn != null && (isMultiTiedType || tiedType != TiedType.REST || portIn.tiedType() == TiedType.REST))
            {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length > 1 || parameters.length == 1 && !WObject.class.equals(parameters[0]))
                {
                    throw new IllegalArgumentException("the parameter list of " + method + " is illegal!");
                }
                WPort _port = new WPort(classLoader);
                _port.argCount = parameters.length;
                _port.tiedType = portIn.tiedType();
                _port.tiedName = PortUtil.tied(portIn, method,
                        TiedType.type(getTiedType(), _port.getTiedType()) == TiedType.REST || enableDefaultValue);
                _port.inNames = InNames.fromStringArray(portIn.nece(), portIn.unnece(), portIn.inner());
                _port.checks = portIn.checks();
                _port.method = PortUtil.method(getMethod(), portIn.method());

                _port.port = method;
                PortUtil.addCheckPassable(checkPassableMap, portIn.checks());

                boolean hasBinded = false;
                //类型转换处理
                Parser parser = PortUtil.getAnnotation(method, Parser.class);
                if (parser != null)
                {//多个

                    //添加；而且对于类型转换绑定为空的，在类上进行查找和绑定。
                    PortUtil.bindTypeParsers(_port.inNames, parser, portInObjConf.getTypeParserStore(), backableSeek,
                            SeekType.Add_Bind);
                    hasBinded = true;
                }
                Parser.parse parse = PortUtil.getAnnotation(method, Parser.parse.class);
                if (parse != null)
                {//单个
                    //添加；而且对于类型转换绑定为空的，在类上进行查找和绑定。
                    PortUtil.bindTypeParser(_port.inNames, parse, portInObjConf.getTypeParserStore(), backableSeek,
                            SeekType.Add_Bind);
                    hasBinded = true;
                }
                if (!hasBinded)
                {
                    //当函数上没有转换注解、而类上有时，加上此句是确保类上的转换对函数有想
                    PortUtil.bindTypeParser(_port.inNames, null, portInObjConf.getTypeParserStore(), backableSeek,
                            SeekType.NotAdd_Bind);
                }

                _port.wPortInObj = PortUtil
                        .dealPortInObj(method, portInObjConf, backableSeek,
                                SeekType.NotAdd_Bind);
                PortOut portOut = PortUtil.getAnnotation(method, PortOut.class);
                if (portOut != null)
                {
                    _port.outType = portOut.value();
                } else
                {
                    _port.outType = OutType.Object;
                }

                _port.initOk();
                port = _port;
            }
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return port;
    }

    /**
     * 初始化静态的接口类。
     *
     * @param port             接口对象。
     * @param checkPassableMap 用于存放检测接口的map。
     */
    public void initStatic(Object port, Map<Class<?>, CheckPassable> checkPassableMap,
            PortInObjConf portInObjConf,
            boolean enableDefaultValue)
    {
        this.port = port;
        Class<?> clazz = port.getClass();
        PortIn in = clazz.getAnnotation(PortIn.class);
        this.tiedName = PortUtil.tied(in, clazz, enableDefaultValue);
        this.inNames = InNames.fromStringArray(in.nece(), in.unnece(), in.inner());
        this.method = in.method();
        this.checks = in.checks();
        this.tiedType = in.tiedType();
        this.isMultiTiedType = in.multiTiedType();
        children = new HashMap<>();

        LOGGER.debug("tiedName={},tiedType={},method={}", this.tiedName, this.tiedType, this.method);

        PortUtil.addCheckPassable(checkPassableMap, in.checks());


        BackableSeek backableSeek = new BackableSeek();
        backableSeek.push();
        if (clazz.isAnnotationPresent(Parser.class))
        {
            Parser parser = clazz.getAnnotation(Parser.class);
            PortUtil.bindTypeParsers(this.inNames, parser, portInObjConf.getTypeParserStore(), backableSeek,
                    SeekType.Add_NotBind);
        }

        if (clazz.isAnnotationPresent(Parser.parse.class))
        {
            Parser.parse parse = clazz.getAnnotation(Parser.parse.class);
            PortUtil.bindTypeParser(this.inNames, parse, portInObjConf.getTypeParserStore(), backableSeek,
                    SeekType.Add_NotBind);
        }


        try
        {
            wPortInObj = PortUtil
                    .dealPortInObj(clazz, portInObjConf, backableSeek,
                            SeekType.NotAdd_Bind);
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            if (Modifier.isStatic(method.getModifiers()))
            {
                continue;
            }
            if (mayAddStartOrDestroy(method))
            {
                continue;
            }
            backableSeek.push();
            WPort child = childPort(method, checkPassableMap, portInObjConf, enableDefaultValue, backableSeek);
            backableSeek.pop();
            if (child != null)
            {
                TiedType tiedType = TiedType.type(getTiedType(), child.getTiedType());
                child.tiedType = tiedType;
                switch (tiedType)
                {

                    case REST:
                        children.put(child.getMethod().name(), child);
                        LOGGER.debug("add-rest:{} (function={})", child.method, method.getName());
                        break;
                    case Default:
                        children.put(child.getTiedName(), child);
                        LOGGER.debug("add:{},{} (function={})", child.tiedName, child.method, method.getName());
                        break;
                }

            }
        }
        initOk();
    }

    private boolean mayAddStartOrDestroy(Method method)
    {
        if (PortUtil.getAnnotation(method, PortStart.class) != null)
        {
            method.setAccessible(true);
            starts.add(method);
            return true;
        }

        if (PortUtil.getAnnotation(method, PortDestroy.class) != null)
        {
            method.setAccessible(true);
            destroys.add(method);
            return true;
        }
        return false;
    }

    /**
     * 用于函数，转换成类。
     *
     * @return
     */
    public WPortInObj getWPortInObj()
    {
        return wPortInObj;
    }

    public String getTiedName()
    {
        return tiedName;
    }

    public InNames getInNames()
    {
        return inNames;
    }

    public PortMethod getMethod()
    {
        return method;
    }

    public Class<? extends CheckPassable>[] getChecks()
    {
        return checks;
    }

    public Object getPortObject()
    {
        return port;
    }

    public TiedType getTiedType()
    {
        return tiedType;
    }


    public Map<String, WPort> getChildren()
    {
        return children;
    }

    /**
     * 对于rest，会优先获取非{@linkplain TiedType#REST}接口。
     *
     * @param result 地址解析结果
     * @param method 请求方法
     * @return 函数接口。
     */
    public WPort getChild(UrlDecoder.Result result, PortMethod method)
    {
        WPort wport = null;

        switch (tiedType)
        {

            case REST:
                if (isMultiTiedType)
                {
                    wport = children.get(result.funTied());
                    if (wport == null)
                    {
                        wport = children.get(method.name());
                    }
                } else
                {
                    wport = children.get(method.name());
                }
                break;
            case Default:
                wport = children.get(result.funTied());
                break;
        }
        if (wport != null && wport.method != method)
        {
            wport = null;
        }
        return wport;
    }

    public int getArgCount()
    {
        return argCount;
    }

    public OutType getOutType()
    {
        return outType;
    }

    public void onStart()
    {
        if (starts != null)
        {
            for (int i = 0; i < starts.size(); i++)
            {
                Method start = starts.get(i);
                try
                {
                    start.invoke(port);
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    public void onDestroy()
    {
        if (destroys != null)
        {
            for (int i = 0; i < destroys.size(); i++)
            {
                Method destroy = destroys.get(i);
                try
                {
                    destroy.invoke(port);
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private void initOk()
    {

    }


}
