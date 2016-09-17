package cn.oftenporter.oftendb.data;


import cn.oftenporter.oftendb.db.Condition;
import cn.oftenporter.oftendb.db.DBException;
import cn.oftenporter.oftendb.db.DBHandle;
import cn.oftenporter.oftendb.db.QuerySettings;

/**
 */
public class DBHandleAccess implements DBHandleSource
{
    private DBHandleSource dbHandleSource;
    private DBHandle dbHandle;


    DBHandleAccess(DBHandleSource dbHandleSource, DBHandle dbHandle)
    {
        this.dbHandleSource = dbHandleSource;
        this.dbHandle = dbHandle;
    }

    /**
     * 得到当前的(与common当前的为同一个)
     *
     * @return
     */
    public DBHandle getCurrentDBHandle()
    {
        return dbHandle;
    }

    @Override
    public Condition newCondition()
    {
        return dbHandleSource.newCondition();
    }

    @Override
    public QuerySettings newQuerySettings()
    {
        return dbHandleSource.newQuerySettings();
    }

    @Override
    public DBHandle getDbHandle(ParamsGetter paramsGetter, DBHandle dbHandle) throws DBException
    {
        return dbHandleSource.getDbHandle(paramsGetter, dbHandle);
    }

    @Override
    public void afterClose(DBHandle dbHandle)
    {

    }
}
