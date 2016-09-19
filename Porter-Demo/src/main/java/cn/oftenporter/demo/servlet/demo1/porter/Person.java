package cn.oftenporter.demo.servlet.demo1.porter;

import cn.oftenporter.porter.core.annotation.PortInObj.UnNece;
import cn.oftenporter.porter.core.apt.AutoGen;

@AutoGen()
public interface Person
{
    int getAge();

    String getName();

    @UnNece("Sex")
    int sex();

}
