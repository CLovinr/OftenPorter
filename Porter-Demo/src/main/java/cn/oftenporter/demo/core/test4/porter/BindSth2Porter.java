package cn.oftenporter.demo.core.test4.porter;

import cn.oftenporter.demo.core.test4.sth.ID;
import cn.oftenporter.demo.core.test4.sth.User;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortInObj;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.WObject;

@PortIn
@PortInObj(ID.class)
public class BindSth2Porter
{
    /**
     * <pre>
     * 1.通过method指定请求方法。
     * 2.通过@PortInObj来绑定对象，对象中的字段通过@InNece来指定是必需值，@InUnNece指定非必需值，默认情况下变量的类型是
     * 自动绑定的。
     * </pre>
     * 
     * @param wObject
     * @return
     */

    @PortIn(method = PortMethod.POST)
    @PortInObj({ User.class })
    public Object send(WObject wObject)
    {
	ID id = wObject.cinObject(ID.class, 0);
	User user = wObject.finObject(User.class, 0);
	return user + "," + id;
    }

}
