package cn.oftenporter.porter.core;


import cn.oftenporter.porter.core.base.ParamSource;
import cn.oftenporter.porter.core.base.UrlDecoder;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.util.WPTool;

import java.util.Enumeration;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/7/24.
 */
class ParamsSourceDefault implements ParamSource
{
    private UrlDecoder.Result result;
    private WRequest request;

    public ParamsSourceDefault(UrlDecoder.Result result, WRequest request)
    {
        this.result = result;
        this.request = request;
    }

    @Override
    public Object getParam(String name)
    {
        Object rs = result.getParam(name);
        if (WPTool.isEmpty(rs))
        {
            rs = request.getParameter(name);
        }
        return rs;
    }

    @Override
    public void putNewParams(Map<String, ?> newParams)
    {
        result.putNewParams(newParams);
    }

    @Override
    public Enumeration<String> paramNames()
    {
        Enumeration<String> enumeration = new Enumeration<String>()
        {
            Enumeration<String> e1 = result.paramNames();
            Enumeration<String> e2 = request.getParameterNames();

            @Override
            public boolean hasMoreElements()
            {
                return e1.hasMoreElements() || e2.hasMoreElements();
            }

            @Override
            public String nextElement()
            {
                return e1.hasMoreElements() ? e1.nextElement() : e2.nextElement();
            }
        };
        return enumeration;
    }
}
