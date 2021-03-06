package cn.oftenporter.servlet;


import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.pbridge.PRequest;
import cn.oftenporter.porter.core.util.WPTool;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WServletRequest extends PRequest
{
    private HttpServletRequest request;

    WServletRequest(HttpServletRequest request, String urlPatternPrefix, PortMethod method)
    {
        super(method, request.getRequestURI().substring(request.getContextPath().length() + urlPatternPrefix.length()),
                false);
        this.request = request;
    }

    @Override
    public Map<String, Object> getParameterMap()
    {
        if (super.params == null)
        {
            params = new HashMap<>();
            Enumeration<String> e = request.getParameterNames();

            while (e.hasMoreElements())
            {
                String name = e.nextElement();
                String value = request.getParameter(name);
                if (WPTool.notNullAndEmpty(value))
                {
                    params.put(name, value);
                }
            }
        }
        return super.getParameterMap();
    }

    @Override
    public String getParameter(String name)
    {
        return request.getParameter(name);
    }


    @Override
    public PortMethod getMethod()
    {
        return method;
    }


    public HttpServletRequest getServletRequest()
    {
        return request;
    }


    /**
     * 得到host，包含协议。如http://localhost:8080/hello得到的是http://localhost:8080
     *
     * @param url
     * @return
     */
    public static String getHostFromURL(CharSequence url)
    {
        Pattern pattern = Pattern.compile("^(http|https)://([^/]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find())
        {
            return matcher.group();
        } else
        {
            return "";
        }
    }

    public String getHost()
    {
        return getHostFromURL(request.getRequestURL());
    }

}
