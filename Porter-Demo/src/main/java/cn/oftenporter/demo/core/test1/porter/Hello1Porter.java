package cn.oftenporter.demo.core.test1.porter;

import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.WObject;

/**
 * <pre>
 * 1.PortIn放在类上：value为""，则类绑定名见{@linkplain PortIn#value()}
 * </pre>
 * 
 * @author https://github.com/CLovinr <br>
 *         2016年9月16日 下午4:15:04
 *
 */
@PortIn(nece={"sth"})
public class Hello1Porter
{
    /**
     * <pre>
     * 1.nece表示必需参数，如果没有提供则会返回错误信息。
     * 2.unnece表示非必需参数
     * 3.value为""的情况下，表示函数绑定名为函数名，即say
     * </pre>
     * 
     * @param wObject
     * @return
     */
    @PortIn(nece = { "name" }, unnece = { "msg" })
    public Object say(WObject wObject)
    {
	/**
	 * 1.通过WObject.cn获取类接口的必需参数值.
	 * 2.通过WObject.cu获取类接口的非必需参数值.
	 * 3.通过WObject.fn获取接口函数的必需参数值.
	 * 4.通过WObject.fu获取接口函数的非必需参数值.
	 * 
	 * 各个参数值的索引是与声明顺序一致的。
	 */
	Object sth = wObject.cn[0];
	String name = (String) wObject.fn[0];
	Object msg = wObject.fu[0];

	return sth+":"+name+":"+msg;
    }
}
