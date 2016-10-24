package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.UrlDecoder;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.base.WResponse;
import cn.oftenporter.porter.core.pbridge.Delivery;


/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
class WObjectImpl extends WObject
{
    private WRequest request;
    private WResponse response;
    private UrlDecoder.Result result;
    Object[] finObjs, cinObjs;

    private Context context;

    WObjectImpl(UrlDecoder.Result result, WRequest request, WResponse response, Context context)
    {
        this.result = result;
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
    public <T> T finObject( int index)
    {
        Object obj = finObjs[index];
        T t = (T) obj;
        return t;
    }


    @Override
    public <T> T cinObject(int index)
    {
        Object obj = cinObjs[index];
        T t = (T) obj;
        return t;
    }


    @Override
    public <T> T savedObject(String key)
    {
        T t = (T) context.innerContextBridge.contextAutoSet.get(key);
        return t;
    }

    @Override
    public <T> T gsavedObject(String key)
    {
        T t = (T) context.innerContextBridge.innerBridge.globalAutoSet.get(key);
        return t;
    }

    @Override
    public Delivery delivery()
    {
        return context.delivery;
    }

    @Override
    public UrlDecoder.Result url()
    {
        return result;
    }
}
