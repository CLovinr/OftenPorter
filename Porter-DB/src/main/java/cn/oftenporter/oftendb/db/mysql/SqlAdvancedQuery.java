package cn.oftenporter.oftendb.db.mysql;


import cn.oftenporter.oftendb.db.AdvancedQuery;

/**
 * Created by 宇宙之灵 on 2015/9/23.
 */
public class SqlAdvancedQuery extends AdvancedQuery
{
    SqlUtil.WhereSQL whereSQL;
    String[] keys;

    /**
     *
     * @param whereSQL
     * @param keys 大小为0表示选择全部。
     */
    public SqlAdvancedQuery(SqlUtil.WhereSQL whereSQL, String... keys)
    {
        this.whereSQL = whereSQL;
        this.keys = keys;
    }


    @Override
    public Object toFinalObject()
    {
        throw new RuntimeException("can not be invoked!");
    }
}
