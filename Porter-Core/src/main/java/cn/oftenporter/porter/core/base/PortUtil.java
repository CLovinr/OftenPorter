package cn.oftenporter.porter.core.base;


import cn.oftenporter.porter.core.annotation.Parser;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortInObj;
import cn.oftenporter.porter.core.exception.InitException;
import cn.oftenporter.porter.core.util.WPTool;
import cn.oftenporter.porter.simple.parsers.ParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PortUtil
{
    private static final String TIED_ACCEPTED = "^[a-zA-Z0-9%_.*$&=-]+$";
    private static final Pattern TIED_NAME_PATTERN = Pattern.compile(TIED_ACCEPTED);
    private static final Logger LOGGER = LoggerFactory.getLogger(PortUtil.class);

    /**
     * 得到函数的绑定名。
     *
     * @param portIn
     * @param method
     * @return
     */
    public static String tied(PortIn portIn, Method method, boolean enableDefaultValue)
    {
        String name = portIn.value();
        if (WPTool.isEmpty(name))
        {
            if (!enableDefaultValue)
            {
                throw new InitException("default value is not enable for " + method);
            }
            name = method.getName();
        }
        return checkTied(name);
    }

    /**
     * 得到类的绑定名。
     *
     * @param portIn
     * @param clazz
     * @return
     */
    public static String tied(PortIn portIn, Class<?> clazz, boolean enableDefaultValue)
    {
        String name = portIn.value();
        if (WPTool.isEmpty(name))
        {
            if (!enableDefaultValue)
            {
                throw new InitException("default value is not enable for " + clazz);
            }
            String className = clazz.getSimpleName();
            if (className.endsWith("WPort"))
            {
                className = className.substring(0, className.length() - 4);
            } else if (className.endsWith("Porter"))
            {
                className = className.substring(0, className.length() - 6);
            }
            name = className;
        }
        return checkTied(name);
    }

    private static String checkTied(String tiedName)
    {
        if (!TIED_NAME_PATTERN.matcher(tiedName).find())
        {
            throw new RuntimeException("Illegal tied '" + tiedName + "'(accepted-pattern:" + TIED_ACCEPTED + ")");
        }
        return tiedName;
    }

    static PortMethod method(PortMethod classMethod, PortMethod funMethod)
    {
        if (classMethod == PortMethod.DEFAULT && funMethod == PortMethod.DEFAULT)
        {
            return PortMethod.GET;
        } else if (funMethod == PortMethod.DEFAULT)
        {
            return classMethod;
        } else
        {
            return funMethod;
        }
    }


    static void addTypeParser(Map<String, String> parsersVarAndType, Parser parser,
            TypeParserStore typeParserStore)
    {
        Parser.parse[] parses = parser.value();
        for (int i = 0; i < parses.length; i++)
        {
            addTypeParser(parsersVarAndType, parses[i], typeParserStore);
        }
    }

    static void addTypeParser(Map<String, String> parsersVarAndType, Parser.parse parse,
            TypeParserStore typeParserStore)
    {


        if (parse.parsers().length != 0)
        {
            Class<? extends TypeParser>[] parsers = parse.parsers();
            String[] names = parse.names();
            if (names.length > parsers.length && parsers.length > 1)
            {
                LOGGER.error("Parse.parsers() cannot be applied to Parse.names()!");
                return;
            } else if (parsers.length == 1)
            {
                try
                {
                    String typeName = parsers[0].getName();
                    if (!typeParserStore.contains(typeName))
                    {
                        typeParserStore.put(typeName, newObject(parsers[0]));
                    }
                    for (int i = 0; i < names.length; i++)
                    {
                        parsersVarAndType.put(names[i], typeName);
                    }
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            } else if (parsers.length > 1)
            {
                try
                {

                    for (int i = 0; i < names.length; i++)
                    {
                        String typeName = parsers[i].getName();
                        if (!typeParserStore.contains(typeName))
                        {
                            typeParserStore.put(typeName, newObject(parsers[i]));
                        }
                        parsersVarAndType.put(names[i], typeName);
                    }
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } else if (parse.globals().length != 0)
        {
            String[] names = parse.names();
            String[] types = parse.globals();
            if (names.length > types.length && types.length > 1)
            {
                LOGGER.error("Parse.globals() cannot be applied to Parse.names()!");
                return;
            } else if (types.length == 1)
            {
                try
                {
                    String typeName = types[0];
                    for (int i = 0; i < names.length; i++)
                    {
                        parsersVarAndType.put(names[i], typeName);
                    }
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            } else if (types.length > 1)
            {
                try
                {

                    for (int i = 0; i < names.length; i++)
                    {
                        String typeName = types[i];
                        parsersVarAndType.put(names[i], typeName);
                    }
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }


    }

    static void addCheckPassable(Map<Class<?>, CheckPassable> checkPassableMap,
            Class<? extends CheckPassable>[] checks)
    {
        for (int i = 0; i < checks.length; i++)
        {
            try
            {
                checkPassableMap.put(checks[i], newObject(checks[i]));
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static <T> T newObject(Class<T> clazz) throws Exception
    {
        Constructor<T> constructor = clazz.getConstructor();
        constructor.setAccessible(true);
        T t = constructor.newInstance();
        return t;
    }


    private static final Object[] EMPTY = new Object[0];

    public static Object[] newArray(String[] names)
    {
        if (names.length == 0)
        {
            return EMPTY;
        } else
        {
            return new Object[names.length];
        }
    }

    static WPortInObj dealPortInObj(Method method, Map<String, String> parsersVarAndType,
            TypeParserStore typeParserStore) throws Exception
    {
        WPortInObj wPortInObj = null;

        if (method.isAnnotationPresent(PortInObj.class))
        {
            PortInObj portInObj = method.getAnnotation(PortInObj.class);
            boolean defaultNecessary = portInObj.defaultNecessary();
            Class<?>[] types = portInObj.types();
            WPortInObj.One[] ones = new WPortInObj.One[types.length];
            for (int i = 0; i < types.length; i++)
            {
                ones[i] = buildOne(defaultNecessary, types[i], parsersVarAndType, typeParserStore);
            }
            wPortInObj = new WPortInObj(ones);
        }

        return wPortInObj;
    }

    private static WPortInObj.One buildOne(boolean defaultNecessary, Class<?> clazz,
            Map<String, String> parsersVarAndType,
            TypeParserStore typeParserStore) throws Exception
    {
        WPortInObj.One one;
        if (Modifier.isAbstract(clazz.getModifiers()))
        {
            throw new RuntimeException("abstract class is not supported!(" + clazz + ")");
        } else if (Modifier.isInterface(clazz.getModifiers()))
        {
            throw new RuntimeException("stub!");
        } else
        {
            Field[] fields = clazz.getDeclaredFields();
            List<Field> neces = new ArrayList<>();
            List<String> neceNames = new ArrayList<>();
            List<Field> unneces = new ArrayList<>();
            List<String> unneceNames = new ArrayList<>();
            for (int i = 0; i < fields.length; i++)
            {
                Field field = fields[i];
                Boolean isAuto = null;
                String name = null;
                field.setAccessible(true);
                if (field.isAnnotationPresent(PortInObj.InNece.class))
                {
                    neces.add(field);
                    name = field.getName();
                    neceNames.add(name);
                    isAuto = field.getAnnotation(PortInObj.InNece.class).autoParse();
                } else if (field.isAnnotationPresent(PortInObj.InUnNece.class))
                {
                    unneces.add(field);
                    name = field.getName();
                    unneceNames.add(name);
                    isAuto = field.getAnnotation(PortInObj.InUnNece.class).autoParse();
                }
                if (isAuto != null && isAuto)
                {
                    Class<? extends TypeParser> typeParser = ParserUtil.getTypeParser(field.getType());
                    String typeName = typeParser.getName();
                    if (!typeParserStore.contains(typeName))
                    {
                        typeParserStore.put(typeName, newObject(typeParser));
                    }
                    parsersVarAndType.put(name, typeName);
                }
            }
            one = new WPortInObj.One(clazz,
                    new InNames(neceNames.toArray(new String[0]), unneceNames.toArray(new String[0]), null),
                    neces.toArray(new Field[0]), unneces.toArray(new Field[0]));
        }
        return one;
    }


}
