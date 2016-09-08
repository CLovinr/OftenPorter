package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.simple.DefaultTypeParserStore;

import java.util.*;

/**
 * Created by 宇宙之灵 on 2016/8/31.
 */
public class PorterConf
{
    private SeekPackages seekPackages;
    private InitParamSource userInitParam;
    private Set<StateListener> stateListenerSet;
    private List<CheckPassable> globalChecks;
    private Map<String, Object> globalAutoSetMap;
    private ClassLoader classLoader;
    private TypeParserStore typeParserStore;
    private boolean responseWhenException = true;
    private boolean enablePortInTiedNameDefault = true;
    private boolean isInited;

    public PorterConf()
    {
        seekPackages = new SeekPackages();
        stateListenerSet = new HashSet<>();
        globalChecks = new ArrayList<>();
        userInitParam = new InitParamSourceImpl();
        typeParserStore = new DefaultTypeParserStore();
        globalAutoSetMap = new HashMap<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    private void checkInited()
    {
        if (isInited)
        {
            throw new RuntimeException("already init!");
        }
    }

    /**
     * 设置是否允许{@linkplain PortIn#value()}取默认值。
     *
     * @param enablePortInTiedNameDefault
     */
    public void setEnablePortInTiedNameDefault(boolean enablePortInTiedNameDefault)
    {
        checkInited();
        this.enablePortInTiedNameDefault = enablePortInTiedNameDefault;
    }

    /**
     * 是否允许{@linkplain PortIn#value()}取默认值。默认为true。
     *
     * @return
     */
    public boolean isEnablePortInTiedNameDefault()
    {
        checkInited();
        return enablePortInTiedNameDefault;
    }

    /**
     * 用于对象自动设置。另见{@linkplain cn.oftenporter.porter.core.annotation.AutoSet}
     *
     * @param name
     * @param object
     */
    public void addGlobalAutoSetObject(String name, Object object)
    {
        globalAutoSetMap.put(name, object);
    }

    public void addGlobalTypeParser(String typeName, TypeParser typeParser)
    {
        checkInited();
        typeParserStore.put(typeName, typeParser);
    }

    public TypeParserStore getTypeParserStore()
    {
        return typeParserStore;
    }

    public boolean isResponseWhenException()
    {
        return responseWhenException;
    }

    public void setStateListenerSet(Set<StateListener> stateListenerSet)
    {
        checkInited();
        this.stateListenerSet = stateListenerSet;
    }

    public SeekPackages getSeekPackages()
    {
        checkInited();
        return seekPackages;
    }

    public void addStateListener(StateListener stateListener)
    {
        checkInited();
        stateListenerSet.add(stateListener);
    }

    public void addGlobalCheck(CheckPassable checkPassable)
    {
        checkInited();
        globalChecks.add(checkPassable);
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        checkInited();
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader()
    {
        checkInited();
        return classLoader;
    }

    public List<CheckPassable> getGlobalChecks()
    {
        checkInited();
        return globalChecks;
    }

    public Map<String, Object> getGlobalAutoSetMap()
    {
        return globalAutoSetMap;
    }

    public Set<StateListener> getStateListenerSet()
    {
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
        globalChecks = null;
        typeParserStore = null;
        globalAutoSetMap = null;
    }
}
