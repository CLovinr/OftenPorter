package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.annotation.AutoSet;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortInObj;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.simple.DefaultTypeParserStore;

import java.util.*;

/**
 * 框架配置对象。非线程安全。
 */
public class PorterConf
{
    private SeekPackages seekPackages;
    private InitParamSource userInitParam;
    private Set<StateListener> stateListenerSet;
    private List<CheckPassable> contextChecks;
    private Map<String, Object> contextAutoSetMap;
    private Map<String, Object> contextRuntimeMap;
    private Map<String, Class<?>> contextAutoGenImplMap;
    private ClassLoader classLoader;
    private boolean responseWhenException = true;
    private boolean enablePortInTiedNameDefault = true;
    private boolean isInited;
    private String name;
    private String contentEncoding = "utf-8";


    PorterConf()
    {
        seekPackages = new SeekPackages();
        stateListenerSet = new HashSet<>();
        contextChecks = new ArrayList<>();
        userInitParam = new InitParamSourceImpl();
        contextAutoSetMap = new HashMap<>();
        contextAutoGenImplMap = new HashMap<>();
        contextRuntimeMap = new HashMap<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public String getContentEncoding()
    {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding)
    {
        checkInited();
        this.contentEncoding = contentEncoding;
    }

    public void setContextName(String contextName)
    {
        checkInited();
        PortUtil.checkName(contextName);
        this.name = contextName;
    }

    public String getContextName()
    {
        return name;
    }


    private void checkInited()
    {
        if (isInited)
        {
            throw new RuntimeException("already init!");
        }
    }

    /**
     * 见{@linkplain #isEnableTiedNameDefault()}
     *
     * @param enablePortInTiedNameDefault
     */
    public void setEnableTiedNameDefault(boolean enablePortInTiedNameDefault)
    {
        checkInited();
        this.enablePortInTiedNameDefault = enablePortInTiedNameDefault;
    }

    /**
     * 是否允许{@linkplain PortIn#value()}、{@linkplain PortInObj.Nece#value()}和{@linkplain PortInObj.UnNece#value()}取默认值。默认为true。
     *
     * @return
     */
    public boolean isEnableTiedNameDefault()
    {
        checkInited();
        return enablePortInTiedNameDefault;
    }

    /**
     * 用于对象自动设置。另见{@linkplain AutoSet.Range#Context}
     *
     * @param name
     * @param object
     */
    public void addContextAutoSet(String name, Object object)
    {
        contextAutoSetMap.put(name, object);
    }


    /**
     * 用于添加接口实现.
     *
     * @param name      名称
     * @param implClass 实现类。
     */
    public void addContextAutoGenImpl(String name, Class<?> implClass)
    {
        contextAutoGenImplMap.put(name, implClass);
    }

    public Map<String, Class<?>> getContextAutoGenImplMap()
    {
        return contextAutoGenImplMap;
    }

    /**
     * 添加运行期对象。只对当前context有效。
     *
     * @param name   用于查找的名称（唯一）。
     * @param object 添加的对象
     */
    public void addContextRuntimeObject(String name, Object object)
    {
        contextRuntimeMap.put(name, object);
    }


    public Map<String, Object> getContextRuntimeMap()
    {
        return contextRuntimeMap;
    }


    public boolean isResponseWhenException()
    {
        return responseWhenException;
    }


    public SeekPackages getSeekPackages()
    {
        return seekPackages;
    }

    public void addStateListener(StateListener stateListener)
    {
        checkInited();
        stateListenerSet.add(stateListener);
    }

    /**
     * 添加针对当前context有效的全局检测对象。
     *
     * @param checkPassable
     */
    public void addContextCheck(CheckPassable checkPassable)
    {
        contextChecks.add(checkPassable);
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        checkInited();
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public List<CheckPassable> getContextChecks()
    {
        return contextChecks;
    }

    public Map<String, Object> getContextAutoSetMap()
    {
        return contextAutoSetMap;
    }

    public Set<StateListener> getStateListenerSet()
    {
        checkInited();
        return stateListenerSet;
    }

    public InitParamSource getUserInitParam()
    {
        checkInited();
        return userInitParam;
    }

    void initOk()
    {
        isInited = true;
        seekPackages = null;
        userInitParam = null;
        classLoader = null;
        contextChecks = null;
        contextAutoSetMap = null;
        contextAutoGenImplMap = null;
        contextRuntimeMap = null;
        stateListenerSet = null;
    }
}
