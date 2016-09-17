package cn.oftenporter.oftendb.data;


import cn.oftenporter.porter.core.base.WObject;

/**
 * 设置类型
 *
 */
public enum SetType
{

    ADD, REPLACE, UPDATE, DELETE, QUERY,
    /**
     * 用于{@linkplain Common#createData(Class, WObject)}
     */
    CREATE
}
