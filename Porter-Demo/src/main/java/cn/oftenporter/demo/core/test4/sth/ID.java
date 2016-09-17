package cn.oftenporter.demo.core.test4.sth;

import cn.oftenporter.porter.core.annotation.PortInObj.Nece;

public class ID
{
    @Nece
    String num;
    @Nece
    String addr;

    @Override
    public String toString()
    {
	return "ID:" + num + "," + addr;
    }
}
