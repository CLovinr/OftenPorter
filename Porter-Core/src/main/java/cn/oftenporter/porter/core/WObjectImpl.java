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
    Object[] inObjs;

    public WObjectImpl(WRequest request, WResponse response)
    {
        this.request = request;
        this.response = response;
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
    public <T> T inObject(Class<T> clazz, int index)
    {
        Object obj = inObjs[index];
        T t = (T) obj;
        return t;
    }
}
