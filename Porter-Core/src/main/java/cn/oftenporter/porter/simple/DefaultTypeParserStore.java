package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.ITypeParser;
import cn.oftenporter.porter.core.base.TypeParserStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
public class DefaultTypeParserStore implements TypeParserStore
{
    private Map<String, ITypeParser> map = new ConcurrentHashMap<>();

    @Override
    public ITypeParser byId(String id)
    {
        if (id == null)
        {
            return null;
        }
        return map.get(id);
    }

    @Override
    public void put(String id, ITypeParser typeParser)
    {
        map.put(id, typeParser);
    }

    @Override
    public boolean contains(String id)
    {
        return map.containsKey(id);
    }
}
