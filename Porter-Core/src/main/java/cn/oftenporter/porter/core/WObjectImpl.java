package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.base.WResponse;


/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
class WObjectImpl extends WObject
{
    private WRequest request;
    private WResponse response;
    Object[] finObjs, cinObjs;

    private PortExecutor.Context context;

    WObjectImpl(WRequest request, WResponse response, PortExecutor.Context context)
    {
        this.request = request;
        this.response = response;
        this.context = context;
    }

    @Override
    public WRequest getRequest()
    {
        return request;
    }

    @Override
    public WResponse getResponse()
    {
        return response;
    }

    @Override
    public <T> T finObject(Class<T> clazz, int index)
    {
        Object obj = finObjs[index];
        T t = (T) obj;
        return t;
    }


    @Override
    public <T> T cinObject(Class<T> clazz, int index)
    {
        Object obj = cinObjs[index];
        T t = (T) obj;
        return t;
    }


    @Override
    public Object savedObject(String key)
    {
        return context.contextRuntimeMap.get(key);
    }

    @Override
    public Object gsavedObject(String key)
    {
        return context.globalAutoSetMap.get(key);
    }
}
