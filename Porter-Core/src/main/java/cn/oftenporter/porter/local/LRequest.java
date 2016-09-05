package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.base.PortMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
public class LRequest
{
    private String requestPath;
    private PortMethod method;
    private HashMap<String, Object> params = new HashMap<String, Object>();

    public LRequest(PortMethod method, String requestPath)
    {
        this.method = method;
        this.requestPath = requestPath;
    }

    public LRequest(String requestPath)
    {
        this(PortMethod.GET, requestPath);
    }

    /**
     * 得到参数
     *
     * @return
     */
    public Map<String, Object> getParamsMap()
    {
        return params;
    }

    public Object getParam(String name)
    {
        return params.get(name);
    }

    public LRequest addParam(String name, Object value)
    {
        params.put(name, value);
        return this;
    }

    public LRequest setRequestPath(String requestPath)
    {
        this.requestPath = requestPath;
        return this;
    }

    public String getRequestPath()
    {
        return requestPath;
    }

    public PortMethod getMethod()
    {
        return method;
    }

    public LRequest setMethod(PortMethod method)
    {
        this.method = method;
        return this;
    }
}
