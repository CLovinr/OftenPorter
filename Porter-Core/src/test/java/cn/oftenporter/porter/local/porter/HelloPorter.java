package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.*;
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

    @AutoSet
    private AutoSetObj autoSetObj;

    @AutoSet
    private static AutoSetObj autoSetObj2;

    @AutoSet(oneInstance = false)
    private AutoSetObj autoSetObj3;

    @AutoSet("globalName")
    private String globalSet;

    @PortIn(value = "say", nece = {"name", "age"})
    public Object say(WObject wObject)
    {
        int age = (int) wObject.fn[1];
        return "Hello World!" + wObject.fn[0] + ",age=" + age;
    }

    @PortIn("parseObject")
    @Parser.parse(names = "myAge",parsers = IntParser.class)
    @PortInObj(types = {Article.class, User.class})
    public void parseObject(WObject wObject)
    {
        Article article = wObject.inObject(Article.class, 0);
        User user = wObject.inObject(User.class, 1);
        LOGGER.debug("{}\n{}", article, user);
    }

    @PortIn(tiedType = TiedType.REST)
    public Object sayHello(WObject wObject)
    {
        return "Hello World-REST!" + wObject.restValue;
    }

    @PortStart
    public void onStart()
    {
        LOGGER.debug("{},2:{},3:{},globalName={}", autoSetObj, autoSetObj2, autoSetObj3, globalSet);
    }

    @PortDestroy
    public void onDestroy()
    {
        LOGGER.debug("");
    }

}
