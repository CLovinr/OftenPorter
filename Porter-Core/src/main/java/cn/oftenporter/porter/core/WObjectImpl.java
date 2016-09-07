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
}
