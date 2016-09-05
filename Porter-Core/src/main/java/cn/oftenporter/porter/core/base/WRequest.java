package cn.oftenporter.porter.core.base;

import java.util.Enumeration;

/**
 * Created by https://github.com/CLovinr on 2016/7/24.
 */
public interface WRequest
{
    /**
     * 得到参数，例如地址参数和表单参数等。
     *
     * @param name 参数名称
     */
    Object getParameter(String name);

    /**
     * 得到所有参数的名称。
     */
    Enumeration<String> getParameterNames();

    String getPath();

}
