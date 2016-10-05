package cn.oftenporter.demo.core.test1.check;

import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.DuringType;
import cn.oftenporter.porter.core.base.WObject;

class CheckPassableImpl implements CheckPassable
{

    @Override
    public Object willPass(WObject wObject, DuringType type)
    {
	System.out.println(getClass().getName() + " is invoked:"+type);
	return null;
    }

}
