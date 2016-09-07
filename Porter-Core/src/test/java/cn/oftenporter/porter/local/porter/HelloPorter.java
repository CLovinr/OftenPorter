package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.Parser;
import cn.oftenporter.porter.core.annotation.PortDestroy;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortStart;
import cn.oftenporter.porter.core.base.TiedType;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.simple.parsers.IntParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
@Parser({
        @Parser.parse(names = {"age"}, parsers = {IntParser.class})
})
@PortIn(value = "Hello", tiedType = TiedType.REST)
public class HelloPorter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloPorter.class);

    @PortIn(value = "say", nece = {"name", "age"})
    public Object say(WObject wObject)
    {
        int age = (int) wObject.fn[1];
        return "Hello World!" + wObject.fn[0] + ",age=" + age;
    }

    @PortIn(tiedType = TiedType.REST)
    public Object sayHello(WObject wObject)
    {
        return "Hello World-REST!" + wObject.restValue;
    }

    @PortStart
    public void onStart()
    {
        LOGGER.debug("");
    }

    @PortDestroy
    public void onDestroy()
    {
        LOGGER.debug("");
    }

}
