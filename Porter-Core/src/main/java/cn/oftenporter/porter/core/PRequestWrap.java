package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.pbridge.PRequest;

import java.util.Enumeration;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/28.
 */
class PRequestWrap extends PRequest
{
    private WRequest wRequest;

    public PRequestWrap(WRequest wRequest, String path)
    {
        super(wRequest.getMethod(), path);
        this.wRequest = wRequest;
    }

    @Override
    public Object getParameter(String name)
    {
        return wRequest.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        return wRequest.getParameterNames();
    }


    @Override
    public PortMethod getMethod()
    {
        return wRequest.getMethod();
    }
}
