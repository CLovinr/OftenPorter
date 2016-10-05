package cn.oftenporter.porter.core.pbridge;

import cn.oftenporter.porter.core.base.PortUtil;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
public class PName
{
    private String name;

    public PName(String name)
    {
        if (name == null)
        {
            throw new NullPointerException();
        }
        PortUtil.checkName(name);

        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof PName))
        {
            return false;
        }
        PName pName = (PName) obj;
        return this.name.equals(pName.name);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
