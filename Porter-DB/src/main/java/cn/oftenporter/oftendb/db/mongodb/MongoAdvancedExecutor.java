package cn.oftenporter.oftendb.db.mongodb;

import cn.oftenporter.oftendb.db.AdvancedExecutor;
import cn.oftenporter.oftendb.db.DBException;
import com.mongodb.DBCollection;

public abstract class MongoAdvancedExecutor extends AdvancedExecutor
{

    protected abstract Object execute(DBCollection collection,MongoHandle mongoHandle) throws DBException;

}
