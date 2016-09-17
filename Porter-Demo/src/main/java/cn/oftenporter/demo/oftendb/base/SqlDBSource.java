package cn.oftenporter.demo.oftendb.base;

import java.sql.Connection;
import java.sql.DriverManager;

import cn.oftenporter.oftendb.data.ParamsGetter;
import cn.oftenporter.oftendb.data.impl.MysqlSource;
import cn.oftenporter.oftendb.db.DBException;
import cn.oftenporter.oftendb.db.DBHandle;
import cn.oftenporter.oftendb.db.mysql.SqlHandle;

public class SqlDBSource extends MysqlSource
{
    private Connection getConn()
    {

	try
	{
	    Class.forName("org.h2.Driver");
	    Connection conn = DriverManager
		    .getConnection("jdbc:h2:~/PorterDemo/oftendb;MODE=MySQL", "sa", "");
	    return conn;
	}
	catch (Exception e)
	{
	    throw new DBException(e);
	}

    }

    @Override
    public DBHandle getDbHandle(ParamsGetter paramsGetter, DBHandle dbHandle)
	    throws DBException
    {
	SqlHandle sqlHandle = (SqlHandle) dbHandle;
	if (sqlHandle == null)
	{
	    sqlHandle = new SqlHandle(getConn(),
		    paramsGetter.getParams().getCollName());
	}
	else
	{
	    sqlHandle.setTableName(paramsGetter.getParams().getCollName());
	}
	return sqlHandle;
    }

}
