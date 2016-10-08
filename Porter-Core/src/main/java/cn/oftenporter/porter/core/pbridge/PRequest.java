package cn.oftenporter.porter.core.pbridge;

import cn.oftenporter.porter.core.base.AppValues;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.util.EnumerationImpl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
public class PRequest implements WRequest, Cloneable
{
    protected String requestPath;
    protected PortMethod method;
    private HashMap<String, Object> params = new HashMap<String, Object>();

    public PRequest(PortMethod method, String requestPath)
    {
        this.method = method;
        this.requestPath = requestPath;
    }

    public PRequest(WRequest request, String requestPath)
    {
        this(request.getMethod(), requestPath);
        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements())
        {
            String name = e.nextElement();
            addParam(name, request.getParameter(name));
        }
    }

    public PRequest(String requestPath)
    {
        this(PortMethod.GET, requestPath);
    }


    public PRequest withNewPath(String newPath)
    {
        try
        {
            PRequest request = (PRequest) clone();
            request.requestPath = newPath;
            return request;
        } catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Object getParameter(String name)
    {
        return params.get(name);
    }

    public Enumeration<String> getParameterNames()
    {
        Enumeration<String> enumeration = new EnumerationImpl<String>(params.keySet());
        return enumeration;
    }

    public String getPath()
    {
        return requestPath;
    }

    public PortMethod getMethod()
    {
        return method;
    }

    public PRequest addParamAll(AppValues appValues)
    {
        String[] names = appValues.getNames();
        Object[] values = appValues.getValues();
        for (int i = 0; i < names.length; i++)
        {
            params.put(names[i], values[i]);
        }
        return this;
    }

    public PRequest addParamAll(Map<String, Object> paramMap)
    {
        params.putAll(paramMap);
        return this;
    }

    public PRequest addParam(String name, Object value)
    {
        params.put(name, value);
        return this;
    }

    public PRequest setRequestPath(String requestPath)
    {
        this.requestPath = requestPath;
        return this;
    }

    public PRequest setMethod(PortMethod method)
    {
        this.method = method;
        return this;
    }

    public HashMap<String, Object> getParams()
    {
        return params;
    }
}
