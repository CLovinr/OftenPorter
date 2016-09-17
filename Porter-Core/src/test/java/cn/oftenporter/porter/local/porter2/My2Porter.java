package cn.oftenporter.porter.local.porter2;

import cn.oftenporter.porter.core.annotation.PortIn;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/13.
 */
@PortIn("My2")
public class My2Porter
{
    @PortIn("hello")
    public String hello()
    {
        return getClass().getSimpleName();
    }
}
