package cn.oftenporter.oftendb.db;


import cn.oftenporter.oftendb.annotation.Key;

import java.lang.reflect.Field;
import java.util.UUID;

public class BaseEasier
{

    /**
     * 若在c中找到了名称为name的变量，且含有@Key注解且value不为空，则返回该value值；否则返回name。
     *
     * @param c
     * @param name
     * @return
     */
    public static String dealWith_Key(Class<?> c, String name)
    {
        Field field = null;

        try
        {
            field = c.getField(name);
        } catch (NoSuchFieldException e)
        {
        } catch (SecurityException e)
        {
        }
        if (field != null && field.isAnnotationPresent(Key.class))
        {

            Key key = field.getAnnotation(Key.class);
            name = key.value().equals("") ? name : key.value();

        }
        return name;
    }

//    /**
//     * 得到基本地址(即:http://host:port/context)
//     *
//     * @param request 请求对象
//     * @return
//     */
//    public static String getBaseUrl(HttpServletRequest request)
//    {
//        String doBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
// request
//                .getContextPath();
//        return doBase;
//    }

    /**
     * 移除末尾指定的字符(若存在的话)。
     *
     * @param sb
     * @param c  要移除的字符
     */
    public static void removeEndChar(StringBuilder sb, char c)
    {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * 移除末尾指定的字符串(若存在的话)。
     *
     * @param sb
     * @param str 要移除的字符
     */
    public static void removeEndStr(StringBuilder sb, String str)
    {
        // System.out.println("BaseEasier.removeEndStr()");
        // System.out.println(sb.toString());
        // System.out.println(str);
        if ((sb.length() >= str.length()) && (sb.indexOf(str) == sb.length() - str.length()))
        {
            sb.delete(sb.length() - str.length(), sb.length());
        }
        // System.out.println(sb.toString());
    }

    /**
     * 得到当前时空的uuid
     *
     * @return
     */
    public static String getUUID()
    {
        UUID uuid = UUID.randomUUID();
        String uid = Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
        return uid;
    }

}
