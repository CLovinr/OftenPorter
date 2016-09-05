package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.UrlDecoder;
import cn.oftenporter.porter.core.util.EnumerationImpl;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
class DefaultUrlResult implements UrlDecoder.Result
{
    private Map<String, Object> params;
    private String classTied, funTied;

    public DefaultUrlResult(Map<String, Object> params, String classTied, String funTied)
    {
        this.params = params;
        this.classTied = classTied;
        this.funTied = funTied;
    }

    @Override
    public String classTied()
    {
        return classTied;
    }

    @Override
    public String funTied()
    {
        return funTied;
    }

    @Override
    public Object getParam(String name)
    {
        return params.get(name);
    }

    @Override
    public void putNewParams(Map<String, ?> newParams)
    {
        params.putAll(newParams);
    }

    @Override
    public Enumeration<String> paramNames()
    {
        Iterator<String> iterator = params.keySet().iterator();
        return new EnumerationImpl<String>(iterator);
    }
}
