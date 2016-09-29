package cn.oftenporter.demo.servlet.demo1.porter;

import java.util.ArrayList;

import cn.oftenporter.porter.core.annotation.AutoSet;
import cn.oftenporter.porter.core.annotation.Parser;
import cn.oftenporter.porter.core.annotation.PortDestroy;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortStart;
import cn.oftenporter.porter.core.base.TiedType;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.util.LogUtil;
import cn.oftenporter.porter.simple.parsers.IntParser;
import cn.oftenporter.porter.simple.parsers.StringParser;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
@Parser({ @Parser.parse(varName =  "age" , parser = IntParser.class) })
@Parser.parse(varName =  "sex" , parser = StringParser.class)
@PortIn(value = "", tiedType = TiedType.REST)
public  class HelloPorter
{

    

     //@AutoSet
     //private PersonAP personAP;

    @AutoSet
    private ArrayList<String> list;

    @PortIn(value = "say", nece = { "name", "age", "sex" })
    public Object say(WObject wObject)
    {
	int age = (int) wObject.fn[1];
	return "Hello World!" + wObject.fn[0]
		+ ",age="
		+ age
		+ ",sex="
		+ wObject.fn[2];
    }

    @PortIn(tiedType = TiedType.REST)
    public Object sayHello(WObject wObject)
    {
	return "Hello World-REST!" + wObject.restValue;
    }

    // @PortIn
    // @PortInObj({ Person.class })
    // public Object testPerson(WObject wObject)
    // {
    // PersonAP person = wObject.finObject(PersonAP.class, 0);
    // return person.toString();
    // }

    @PortStart
    public void onStart()
    {
	list.add("tom");

    }

    @PortDestroy
    public void onDestroy()
    {
	LogUtil.printPosLn();
    }

}
