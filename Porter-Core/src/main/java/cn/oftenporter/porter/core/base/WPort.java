package cn.oftenporter.porter.core.base;


import cn.oftenporter.porter.core.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 对应于一个接口。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class WPort implements TypeParserNameStore
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

    //类型转换varName:typeName
    private Map<String, String> parsersVarAndType;

    private Map<String, WPort> children;

    private WPortInObj wPortInObj;

    public WPort()
    {
        starts = new ArrayList<>(1);
        destroys = new ArrayList<>(1);
    }


    /**
     * 扫描函数。
     */
    private WPort childPort(Method method, Map<Class<?>, CheckPassable> checkPassableMap,
            TypeParserStore typeParserStore, boolean enableDefaultValue)
    {
        WPort port = null;
        try
        {
            if (method.isAnnotationPresent(PortIn.class))
            {

                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length > 1 || parameters.length == 1 && !WObject.class.equals(parameters[0]))
                {
                    throw new IllegalArgumentException("the parameter list of " + method + " is illegal!");
                }

                method.setAccessible(true);

                PortIn portIn = method.getAnnotation(PortIn.class);
                WPort _port = new WPort();
                _port.argCount = parameters.length;
                _port.tiedType = portIn.tiedType();
                _port.tiedName = PortUtil.tied(portIn, method,
                        TiedType.type(getTiedType(), _port.getTiedType()) == TiedType.REST || enableDefaultValue);
                _port.inNames = new InNames(portIn.nece(), portIn.unnece(), portIn.inner());
                _port.checks = portIn.checks();
                _port.method = PortUtil.method(getMethod(), portIn.method());

                _port.port = method;
                _port.parsersVarAndType = new HashMap<>();
                PortUtil.addCheckPassable(checkPassableMap, portIn.checks());

                //类型转换处理
                if (method.isAnnotationPresent(Parser.class))
                {
                    Parser parser = method.getAnnotation(Parser.class);
                    PortUtil.addTypeParser(_port.parsersVarAndType, parser, typeParserStore);
                }
                if (method.isAnnotationPresent(Parser.parse.class))
                {
                    Parser.parse parse = method.getAnnotation(Parser.parse.class);
                    PortUtil.addTypeParser(_port.parsersVarAndType, parse, typeParserStore);
                }

                _port.wPortInObj = PortUtil.dealPortInObj(method);

                if (method.isAnnotationPresent(PortOut.class))
                {
                    PortOut portOut = method.getAnnotation(PortOut.class);
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
    public void initStatic(Object port, Map<Class<?>, CheckPassable> checkPassableMap, TypeParserStore typeParserStore,
            boolean enableDefaultValue)
    {
        this.port = port;
        Class<?> clazz = port.getClass();
        PortIn in = clazz.getAnnotation(PortIn.class);
        this.tiedName = PortUtil.tied(in, clazz, enableDefaultValue);
        this.inNames = new InNames(in.nece(), in.unnece(), in.inner());
        this.method = in.method();
        this.checks = in.checks();
        this.tiedType = in.tiedType();
        children = new HashMap<>();

        LOGGER.debug("tiedName={},tiedType={},method={}", this.tiedName, this.tiedType, this.method);

        this.parsersVarAndType = new HashMap<>();
        PortUtil.addCheckPassable(checkPassableMap, in.checks());
        if (clazz.isAnnotationPresent(Parser.class))
        {
            Parser parser = clazz.getAnnotation(Parser.class);
            PortUtil.addTypeParser(this.parsersVarAndType, parser, typeParserStore);
        }

        if (clazz.isAnnotationPresent(Parser.parse.class))
        {
            Parser.parse parse = clazz.getAnnotation(Parser.parse.class);
            PortUtil.addTypeParser(this.parsersVarAndType, parse, typeParserStore);
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods)
        {
            if (Modifier.isStatic(method.getModifiers()))
            {
                continue;
            }
            mayAddStartOrDestroy(method);
            WPort child = childPort(method, checkPassableMap, typeParserStore, enableDefaultValue);
            if (child != null)
            {
                TiedType tiedType = TiedType.type(getTiedType(), child.getTiedType());
                child.tiedType = tiedType;
                switch (tiedType)
                {

                    case REST:
                        children.put(child.getMethod().name(), child);
                        LOGGER.debug("add-rest:{} (method={})", child.method, method.getName());
                        break;
                    case Default:
                        children.put(child.getTiedName(), child);
                        LOGGER.debug("add:{},{} (method={})", child.tiedName, child.method, method.getName());
                        break;
                }

            }
        }
        initOk();
    }

    private void mayAddStartOrDestroy(Method method)
    {
        if (method.isAnnotationPresent(PortStart.class))
        {
            starts.add(method);
        }

        if (method.isAnnotationPresent(PortDestroy.class))
        {
            destroys.add(method);
        }
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

        switch (getTiedType())
        {

            case REST:
                wport = children.get(result.funTied());
                if (wport == null)
                {
                    wport = children.get(method.name());
                }
                break;
            case Default:
                wport = children.get(result.funTied());
                break;
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
        if (parsersVarAndType.size() == 0)
        {
            parsersVarAndType = new HashMap<>(0);
        }
    }

    @Override
    public String typeName(String varName)
    {
        return parsersVarAndType.get(varName);
    }
}
