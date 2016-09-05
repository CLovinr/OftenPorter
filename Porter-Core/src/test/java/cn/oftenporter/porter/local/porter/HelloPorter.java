package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.Parse;
import cn.oftenporter.porter.core.annotation.PortDestroy;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortStart;
import cn.oftenporter.porter.core.base.TiedType;
import cn.oftenporter.porter.core.base.WPObject;
import cn.oftenporter.porter.core.util.LogUtil;
import cn.oftenporter.porter.simple.parsers.IntParser;
import cn.oftenporter.porter.simple.parsers.StringParser;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
@Parse(names = {"age"}, parsers = IntParser.class)
@PortIn(value = "Hello",tiedType = TiedType.REST)
public class HelloPorter
{
    @PortIn(value = "say",nece = {"name", "age"})
    public Object say(WPObject wpObject)
    {
        int age = (int) wpObject.fn[1];
        return "Hello World!" + wpObject.fn[0] + ",age=" + age;
    }

    @PortIn(tiedType = TiedType.REST)
    public Object sayHello(WPObject wpObject)
    {
        return "Hello World-REST!" + wpObject.restValue;
    }

    @PortStart
    public void onStart()
    {
        LogUtil.printPosLn();
    }

    @PortDestroy
    public void onDestroy()
    {
        LogUtil.printPosLn();
    }

}
