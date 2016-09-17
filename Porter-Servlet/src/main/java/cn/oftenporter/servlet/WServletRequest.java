package cn.oftenporter.servlet;


import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.WRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WServletRequest implements WRequest
{
    private HttpServletRequest request;
    private String path;
    private PortMethod method;

    WServletRequest(HttpServletRequest request, PortMethod method)
    {
        this.request = request;
        this.method = method;
        this.path = request.getRequestURI().substring(request.getContextPath().length());
    }


    @Override
    public String getParameter(String name)
    {
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames()
    {
        return request.getParameterNames();
    }

    @Override
    public String getPath()
    {
        return path;
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
