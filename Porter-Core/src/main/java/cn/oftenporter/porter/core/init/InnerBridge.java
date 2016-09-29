package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.annotation.sth.CacheOne;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.TypeParserStore;
import cn.oftenporter.porter.simple.DefaultTypeParserStore;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/28.
 */
public class InnerBridge
{
    public final Map<String, Object> globalAutoSet;
    public final TypeParserStore globalParserStore;
    List<CheckPassable> allGlobalChecksTemp;
    public final CacheOne.CacheTool cacheTool;


    public InnerBridge()
    {
        this.globalAutoSet = new ConcurrentHashMap<>();
        this.globalParserStore = new DefaultTypeParserStore();
        this.allGlobalChecksTemp = new Vector<>();
        this.cacheTool = new CacheOne.CacheTool();
    }
}
