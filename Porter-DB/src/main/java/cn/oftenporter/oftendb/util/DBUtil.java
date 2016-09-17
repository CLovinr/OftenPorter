package cn.oftenporter.oftendb.util;

import java.util.UUID;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/17.
 */
public class DBUtil
{
    public static String uuid()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
