package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.util.EnumerationImpl;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
class LocalRequest implements WRequest
{
    private LRequest request;

    public LocalRequest(LRequest request)
    {
        this.request = request;
    }

    @Override
    public Object getParameter(String name)
    {
        return request.getParam(name);
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        Enumeration<String> enumeration = new EnumerationImpl<String>(request.getParamsMap().keySet());
        return enumeration;
    }

    @Override
    public String getPath()
    {
        return request.getRequestPath();
    }
}
