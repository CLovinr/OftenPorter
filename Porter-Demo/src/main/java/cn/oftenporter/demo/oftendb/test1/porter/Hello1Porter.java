package cn.oftenporter.demo.oftendb.test1.porter;

import java.util.Date;

import cn.oftenporter.demo.oftendb.base.SqlDBSource;
import cn.oftenporter.oftendb.data.Common2;
import cn.oftenporter.oftendb.data.DataDynamic;
import cn.oftenporter.oftendb.data.SourceAndPGetter;
import cn.oftenporter.oftendb.data.impl.SourceAndPGetterImpl;
import cn.oftenporter.oftendb.db.Condition;
import cn.oftenporter.oftendb.db.NameValues;
import cn.oftenporter.oftendb.db.Unit;
import cn.oftenporter.porter.core.annotation.AutoSet;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.util.KeyUtil;

@PortIn
public class Hello1Porter
{
    private static class Source extends SourceAndPGetterImpl
    {

	public Source()
	{
	    super(new DataDynamic("test1"), new SqlDBSource());
	}
    }

    @AutoSet(classValue = Source.class)
    private SourceAndPGetter source;

    @PortIn(nece = { "name", "age", "sex" }, inner = { "time", "_id" },
	    method = PortMethod.POST)
    public Object add(WObject wObject)
    {
	wObject.finner[0] = new Date();
	wObject.finner[1] = KeyUtil.randomUUID();
	return Common2.C.addData(source, false, wObject);
    }

    @PortIn(nece = { "name" })
    public Object del(WObject wObject)
    {
	Condition condition = source.newCondition();
	condition.put(Condition.EQ, new Unit("name", wObject.fn[0]));
	return Common2.C.deleteData(source, condition, wObject);
    }

    @PortIn(nece = { "name" })
    public Object update(WObject wObject)
    {
	Condition condition = source.newCondition();
	condition.put(Condition.EQ, new Unit("name", wObject.fn[0]));
	NameValues nameValues = new NameValues();
	nameValues.put("time", new Date());
	return Common2.C.updateData(source, condition, nameValues, wObject);
    }

    @PortIn(nece = { "name" })
    public Object count(WObject wObject)
    {
	return Common2.C.exists(source, "name", wObject.fn[0], wObject);
    }

    @PortIn
    public Object list(WObject wObject)
    {
	return Common2.C.queryData(source, null, null, null, wObject);
    }

}
