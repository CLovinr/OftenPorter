package cn.oftenporter.demo.oftendb.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import cn.oftenporter.oftendb.data.ParamsGetter;
import cn.oftenporter.oftendb.data.impl.MysqlSource;
import cn.oftenporter.oftendb.db.DBException;
import cn.oftenporter.oftendb.db.DBHandle;
import cn.oftenporter.oftendb.db.mysql.SqlHandle;

public class SqlDBSource extends MysqlSource
{

    public SqlDBSource()
    {
	Connection connection = null;
	try
	{
	    String initSql =
		    "CREATE TABLE IF NOT EXISTS `test1` (\n" + "  `_id` char(32) NOT NULL,\n"
			    + "  `name` varchar(35) NOT NULL,\n"
			    + "  `age` int(11) NOT NULL,\n"
			    + "  `sex` varchar(2) NOT NULL,\n"
			    + "  `time` datetime NOT NULL,\n"
			    + "  PRIMARY KEY (`_id`)\n"
			    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	    connection = getConn();
	    Statement statement = connection.createStatement();
	    statement.execute(initSql);
	    statement.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    if (connection != null)
	    {
		try
		{
		    connection.close();
		}
		catch (SQLException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
    }

    private Connection getConn()
    {

	try
	{
	    Class.forName("org.h2.Driver");
	    Connection conn = DriverManager.getConnection(
		    "jdbc:h2:~/PorterDemo/oftendb;MODE=MySQL", "sa", "");
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
