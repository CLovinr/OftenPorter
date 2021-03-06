package cn.oftenporter.porter.core.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class WPTool
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WPTool.class);

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

    /**
     * 返回c1是c2的第几代子类。
     * -1表示不是子类，0表示是本身，1表示是第一代子类...
     *
     * @param c1 若为null,则会返回-1
     * @param c2
     * @return
     */
    public static int subclassOf(Class<?> c1, Class<?> c2)
    {
        if (c1 == null)
        {
            return -1;
        } else if (c1.getName().equals(c2.getName()))
        {
            return 0;
        } else
        {
            int n = subclassOf(c1.getSuperclass(), c2);
            return n == -1 ? -1 : n + 1;
        }
    }


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
                LOGGER.error(e.getMessage(), e);
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
     * 获取异常描述。
     *
     * @param throwable
     * @return
     */
    public static String getMessage(Throwable throwable)
    {
        Throwable cause = throwable.getCause();
        if (cause == null)
        {
            cause = throwable;
        }
        String msg = cause.getMessage();
        if (msg == null)
        {
            msg = cause.toString();
        }
        StackTraceElement element = cause.getStackTrace()[0];
        return msg + " " + LogUtil.toString(element);
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
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static void close(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            } catch (SQLException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static void close(Statement statement)
    {
        if (statement != null)
        {
            try
            {
                statement.close();
            } catch (SQLException e)
            {
                LOGGER.error(e.getMessage(), e);
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

    /**
     * 得到所有字段（任何访问类型，包括父类（除了Object））.
     *
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(Class<?> clazz)
    {
        List<Field> list = new ArrayList<>();
        if (!Modifier.isInterface(clazz.getModifiers()))
        {
            getAllFields(clazz, list);
        }
        return list.toArray(new Field[0]);
    }

    private static void getAllFields(Class<?> clazz, List<Field> list)
    {
        if (clazz.equals(Object.class))
        {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            list.add(fields[i]);
        }

        getAllFields(clazz.getSuperclass(), list);

    }

}
