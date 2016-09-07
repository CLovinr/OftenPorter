package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.TypeParser;
import cn.oftenporter.porter.core.base.TypeParserStore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
public class DefaultTypeParserStore implements TypeParserStore
{
    private Map<String, TypeParser> map = new HashMap<>();

    @Override
    public TypeParser byName(String name)
    {
        return map.get(name);
    }

    @Override
    public void put(String name, TypeParser typeParser)
    {
        map.put(name, typeParser);
    }

    @Override
    public boolean contains(String name)
    {
        return map.containsKey(name);
    }
}
