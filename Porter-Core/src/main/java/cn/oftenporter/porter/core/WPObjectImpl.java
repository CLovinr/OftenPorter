package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.WPObject;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.base.WResponse;

/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
class WPObjectImpl extends WPObject
{
    private WRequest request;
    private WResponse response;

    public WPObjectImpl(WRequest request, WResponse response)
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
}
