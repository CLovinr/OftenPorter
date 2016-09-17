package cn.oftenporter.porter.local.porter2;

import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortInObj;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.local.porter.IDemo;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/13.
 */
@PortIn("My")
public class MyPorter
{
    private String words;

    public MyPorter(String words)
    {
        this.words = words;
    }

    @PortIn("hello")
    @PortInObj(IDemo.class)
    public IDemo hello(WObject wObject)
    {
        IDemo iDemo = wObject.finObject(IDemo.class, 0);
        return iDemo;
    }
}
