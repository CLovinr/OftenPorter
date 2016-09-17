package cn.oftenporter.oftendb.data.impl;

import cn.oftenporter.oftendb.data.DBHandleSource;
import cn.oftenporter.oftendb.db.Condition;
import cn.oftenporter.oftendb.db.DBHandle;
import cn.oftenporter.oftendb.db.QuerySettings;
import cn.oftenporter.oftendb.db.mongodb.MongoCondition;
import cn.oftenporter.oftendb.db.mongodb.MongoQuerySettings;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/16.
 */
public abstract class MongoSource implements DBHandleSource
{
    @Override
    public Condition newCondition()
    {
        return new MongoCondition();
    }

    @Override
    public QuerySettings newQuerySettings()
    {
        return new MongoQuerySettings();
    }

    @Override
    public void afterClose(DBHandle dbHandle)
    {

    }
}
