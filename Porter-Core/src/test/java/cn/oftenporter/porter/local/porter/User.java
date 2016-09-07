package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.PortInObj;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
public class User
{
    @PortInObj.InNece
    public String name;
    @PortInObj.InNece
    public int age;

    @Override
    public String toString()
    {
        return "name="+name+",age="+age;
    }
}
