package cn.oftenporter.porter.core.base;

import java.util.Map;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/14.
 */
public class PortInObjConf
{
    // 全局的
    private TypeParserStore typeParserStore;
    private ClassLoader classLoader;
    private Map<String, Class<?>> autoGenImplMap;
    private boolean enableDefaultValue;

    public PortInObjConf(TypeParserStore typeParserStore, ClassLoader classLoader,
            Map<String, Class<?>> autoGenImplMap, boolean enableDefaultValue)
    {
        this.typeParserStore = typeParserStore;
        this.classLoader = classLoader;
        this.autoGenImplMap = autoGenImplMap;
        this.enableDefaultValue = enableDefaultValue;
    }

    public boolean isEnableDefaultValue()
    {
        return enableDefaultValue;
    }

    public Map<String, Class<?>> getAutoGenImplMap()
    {
        return autoGenImplMap;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public TypeParserStore getTypeParserStore()
    {
        return typeParserStore;
    }
}
