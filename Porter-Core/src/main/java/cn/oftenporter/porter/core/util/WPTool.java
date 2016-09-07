package cn.oftenporter.porter.core.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class WPTool
{
    /**
     * 判断是否为null或"".
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj)
    {
        return obj == null || "".equals(obj);
    }


    /**
     * 判断c1是否是c2的子类、子接口或接口实现者。
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isAssignable(Class<?> c1, Class<?> c2)
    {
        return c2.isAssignableFrom(c1);
    }

    /**
     * 判断obj是否是clazz的实例。
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static boolean isAssignable(Object obj, Class<?> clazz)
    {
        return isAssignable(obj.getClass(), clazz);
    }

//    /**
//     * 判断c1是否是c2的直接或间接子类.
//     *
//     * @param c1
//     * @param c2
//     * @return
//     */
//    public static boolean isSubclassOf(Class<?> c1, Class<?> c2)
//    {
//        return subclassOf(c1, c2) > 0;
//    }


    /**
     * obj1和obj2都为null或者obj1不为null且obj1.equals(obj2)返回true时，结果为true；否则返回false。
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean isEqual(Object obj1, Object obj2)
    {
        if (obj1 == null)
        {
            return obj2 == null;
        } else
        {
            return obj1.equals(obj2);
        }
    }

    public static void close(PreparedStatement ps)
    {
        if (ps != null)
        {
            try
            {
                ps.close();
            } catch (SQLException e)
            {
            }
        }
    }

    /**
     * 通过反射构建一个实例，必须含有无参构造函数。
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T newObject(
            Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException
    {
        Constructor<T> c = clazz.getDeclaredConstructor();
        c.setAccessible(true);
        return c.newInstance();
    }

    /**
     * 若不为null则调用关闭closeable.close().
     *
     * @param closeable
     */
    public static void close(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否‘不为null且不为""’.
     *
     * @param object
     * @return
     */
    public static boolean notNullAndEmpty(Object object)
    {
        return object != null && !"".equals(object);
    }


    /**
     * @param array
     * @param object
     * @return 存在则，返回对应索引；不存在返回-1.
     */
    public static int contains(JSONArray array, Object object)
    {
        int index = -1;
        for (int i = 0; i < array.size(); i++)
        {
            if (array.get(i).equals(object))
            {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 在json对象里是否含有指定的键值
     *
     * @param array  存放的是json对象
     * @param key
     * @param object
     * @return 存在则，返回对应索引；不存在返回-1.
     */
    public static int containsJsonValue(JSONArray array, String key, Object object)
    {
        int index = -1;

        for (int i = 0; i < array.size(); i++)
        {
            JSONObject jsonObject = array.getJSONObject(i);
            if (jsonObject.containsKey(key) && jsonObject.get(key).equals(object))
            {
                index = i;
                break;
            }
        }

        return index;
    }

//
//    /**
//     * 支持基本数据类型，json，String类型.
//     *
//     * @param values 字符串的值
//     * @param types  类型，与values对应
//     * @return
//     * @throws SecurityException
//     * @throws NoSuchMethodException
//     * @throws ParseException
//     */
//    public static Object[] xparse(String[] values, Class<?>[] types) throws NoSuchMethodException, SecurityException,
//            ParseException
//    {
//        Object[] objects = new Object[values.length];
//        for (int i = 0; i < objects.length; i++)
//        {
//            Constructor<?> constructor = types[i].getConstructor(String.class);
//            try
//            {
//                Object object = constructor.newInstance(values[i]);
//                objects[i] = object;
//            } catch (Exception e)
//            {
//                ParseException parseException = new ParseException(types[i], values[i], e.getMessage());
//                throw parseException;
//            }
//        }
//        return objects;
//    }
//
//    public static class ParseException extends Exception
//    {
//
//        /**
//         *
//         */
//        private static final long serialVersionUID = 1L;
//
//        private String value;
//        private Class<?> type;
//        private String info;
//
//        public ParseException(Class<?> type, String value, String info)
//        {
//            this.value = value;
//            this.type = type;
//            this.info = info;
//        }
//
//        @Override
//        public String toString()
//        {
//            String string = "can't parse '" + value
//                    + "' to "
//                    + getType().getName()
//                    + " \n"
//                    + info;
//            return string;
//        }
//
//        /**
//         * 得到转换类型.
//         *
//         * @return
//         */
//        public Class<?> getType()
//        {
//            return type;
//        }
//
//        /**
//         * 得到参数值
//         *
//         * @return
//         */
//        public String getValue()
//        {
//            return value;
//        }
//
//    }
}
