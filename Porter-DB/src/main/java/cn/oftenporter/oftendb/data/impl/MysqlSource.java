package cn.oftenporter.oftendb.data.impl;

import cn.oftenporter.oftendb.data.DBHandleSource;
import cn.oftenporter.oftendb.db.Condition;
import cn.oftenporter.oftendb.db.DBHandle;
import cn.oftenporter.oftendb.db.QuerySettings;
import cn.oftenporter.oftendb.db.mysql.SqlCondition;
import cn.oftenporter.oftendb.db.mysql.SqlQuerySettings;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/16.
 */
public abstract class MysqlSource implements DBHandleSource
{
    @Override
    public Condition newCondition()
    {
        return new SqlCondition();
    }

    @Override
    public QuerySettings newQuerySettings()
    {
        return new SqlQuerySettings();
    }

    @Override
    public void afterClose(DBHandle dbHandle)
    {

    }
}
