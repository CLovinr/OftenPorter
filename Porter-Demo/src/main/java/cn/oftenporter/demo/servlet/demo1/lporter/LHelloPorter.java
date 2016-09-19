package cn.oftenporter.demo.servlet.demo1.lporter;

import cn.oftenporter.porter.core.annotation.PortIn;

@PortIn
public class LHelloPorter
{

    @PortIn
    public Object say()
    {
	return "Local Hello World!";
    }
}
