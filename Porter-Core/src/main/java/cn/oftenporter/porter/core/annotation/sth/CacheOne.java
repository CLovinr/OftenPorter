package cn.oftenporter.porter.core.annotation.sth;

import cn.oftenporter.porter.core.init.InnerContextBridge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/27.
 */
public class CacheOne
{

    public static class CacheTool
    {

        public CacheTool()
        {
        }

        private final Map<Class<?>, CacheOne> CACHES = new ConcurrentHashMap<>();

        /**
         * 添加到缓存中。
         *
         * @param clazz
         * @param cacheOne
         */
        void put(Class<?> clazz, CacheOne cacheOne)
        {
            CACHES.put(clazz, cacheOne);
        }

        public CacheOne getCacheOne(Class<?> clazz, InnerContextBridge innerContextBridge) throws Exception
        {
            CacheOne cacheOne = CACHES.get(clazz);
            if (cacheOne == null)// && portInObjConf != null)
            {
                InObj.One one = InObjDeal.bindOne(clazz,innerContextBridge);
                cacheOne = new CacheOne(one);
                put(clazz, cacheOne);
            }
            return cacheOne;
        }
    }

    private InObj.One one;


    public CacheOne(InObj.One one)
    {
        this.one = one;
    }

    public InObj.One getOne()
    {
        return one;
    }


}
