package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.PortInObj;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
public class User
{
    @PortInObj.Nece("name")
    public String name;
    @PortInObj.Nece(value = "myAge",autoParse = false)
    public int myAge;

    @Override
    public String toString()
    {
        return "name="+name+",age="+myAge;
    }
}
