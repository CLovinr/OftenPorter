package cn.oftenporter.porter.core.base;


import cn.oftenporter.porter.core.annotation.Parser;
import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.porter.core.annotation.PortInObj;
import cn.oftenporter.porter.core.apt.AutoGen;
import cn.oftenporter.porter.core.apt.PorterProcessor;
import cn.oftenporter.porter.core.exception.InitException;
import cn.oftenporter.porter.core.util.PackageUtil;
import cn.oftenporter.porter.core.util.WPTool;
import cn.oftenporter.porter.simple.DefaultFailedReason;
import cn.oftenporter.porter.simple.parsers.ParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.oftenporter.porter.core.base.BackableSeek.SeekType;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 接口处理的工具类。
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

    public static String tied(PortInObj.UnNece unNece, Field field, boolean enableDefaultValue)
    {
        String name = unNece.value();
        if (WPTool.isEmpty(name))
        {
            if (!enableDefaultValue)
            {
                throw new InitException("default value is not enable for " + unNece + " in field '" + field + "'");
            }
            name = field.getName();
        }
        return name;
    }

    public static String tied(PortInObj.Nece nece, Field field, boolean enableDefaultValue)
    {
        String name = nece.value();
        if (WPTool.isEmpty(name))
        {
            if (!enableDefaultValue)
            {
                throw new InitException("default value is not enable for " + nece + " in field '" + field + "'");
            }
            name = field.getName();
        }
        return name;
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
            if (className.equals(""))
            {
                className = clazz.getSimpleName();
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


    private static String putTypeParser(Class<? extends ITypeParser> clazz,
            TypeParserStore typeParserStore) throws Exception
    {
        ITypeParser typeParser = WPTool.newObject(clazz);
        String id = typeParser.id();
        if (!typeParserStore.contains(id))
        {
            typeParserStore.put(id, typeParser);
        }
        return id;
    }


    /**
     * 查找多个{@linkplain Parser.parse}绑定
     *
     * @param inNames         输入参数
     * @param parser
     * @param typeParserStore 转换器Store
     * @param backableSeek
     * @param seekType
     */
    static void bindTypeParsers(InNames inNames, Parser parser,
            TypeParserStore typeParserStore, BackableSeek backableSeek, SeekType seekType)
    {
        Parser.parse[] parses = parser.value();
        if (parses.length == 0)
        {
            return;
        }

        //注意！！！：对于前n-1一个，不能执行Bind操作。
        SeekType type = seekType == SeekType.Add_Bind ? SeekType.Add_NotBind : SeekType.NotAdd_NotBind;
        for (int i = 0; i < parses.length - 1; i++)
        {
            bindTypeParser(inNames, parses[i], typeParserStore, backableSeek, type);
        }
        bindTypeParser(inNames, parses[parses.length - 1], typeParserStore, backableSeek, seekType);
    }

    static void bindTypeParser(InNames inNames, Parser.parse parse,
            TypeParserStore typeParserStore, BackableSeek backableSeek, SeekType seekType)
    {
        if (parse != null)
        {
            if (parse.parsers().length != 0)
            {
                Class<? extends ITypeParser>[] parsers = parse.parsers();
                String[] names = parse.names();
                if (names.length > parsers.length && parsers.length > 1)
                {
                    LOGGER.error("Parse.parsers() cannot be applied to Parse.names()!");
                    return;
                } else if (parsers.length == 1)
                {
                    try
                    {
                        String typeId = putTypeParser(parsers[0], typeParserStore);
                        for (int i = 0; i < names.length; i++)
                        {
                            BackableSeek.bindVarNameWithTypeId(inNames, names[i], typeId);
                            if (seekType == SeekType.Add_Bind || seekType == SeekType.Add_NotBind)
                            {
                                backableSeek.put(names[i], typeId);
                            }
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
                            String typeId = putTypeParser(parsers[i], typeParserStore);
                            BackableSeek.bindVarNameWithTypeId(inNames, names[i], typeId);
                            if (seekType == SeekType.Add_Bind || seekType == SeekType.Add_NotBind)
                            {
                                backableSeek.put(names[i], typeId);
                            }
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
                        String typeId = types[0];
                        for (int i = 0; i < names.length; i++)
                        {
                            BackableSeek.bindVarNameWithTypeId(inNames, names[i], typeId);
                            if (seekType == SeekType.Add_Bind || seekType == SeekType.Add_NotBind)
                            {
                                backableSeek.put(names[i], typeId);
                            }
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
                            String typeId = types[i];
                            BackableSeek.bindVarNameWithTypeId(inNames, names[i], typeId);
                            if (seekType == SeekType.Add_Bind || seekType == SeekType.Add_NotBind)
                            {
                                backableSeek.put(names[i], typeId);
                            }
                        }
                    } catch (Exception e)
                    {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }

        if (seekType == SeekType.Add_Bind || seekType == SeekType.NotAdd_Bind)
        {
            backableSeek.bindTypeId2NameNull(inNames);
        }

    }

    static void addCheckPassable(Map<Class<?>, CheckPassable> checkPassableMap,
            Class<? extends CheckPassable>[] checks)
    {
        for (int i = 0; i < checks.length; i++)
        {
            try
            {
                checkPassableMap.put(checks[i], WPTool.newObject(checks[i]));
            } catch (Exception e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private static final Object[] EMPTY = new Object[0];

    public static Object[] newArray(InNames.Name[] names)
    {
        if (names.length == 0)
        {
            return EMPTY;
        } else
        {
            return new Object[names.length];
        }
    }

    /**
     * 处理类上的对象绑定。
     */
    static WPortInObj dealPortInObj(Class<?> clazz, PortInObjConf portInObjConf, BackableSeek backableSeek,
            SeekType seekType) throws Exception
    {
        WPortInObj wPortInObj = null;

        if (clazz.isAnnotationPresent(PortInObj.class))
        {
            PortInObj portInObj = clazz.getAnnotation(PortInObj.class);
            wPortInObj = dealPortInObj(portInObj, portInObjConf, backableSeek, seekType, "in-class");
        }

        return wPortInObj;
    }

    /**
     * 处理函数上的对象绑定。
     */
    static WPortInObj dealPortInObj(Method method, PortInObjConf portInObjConf, BackableSeek backableSeek,
            SeekType seekType) throws Exception
    {
        WPortInObj wPortInObj = null;

        PortInObj portInObj = getAnnotation(method, PortInObj.class);
        if (portInObj != null)
        {
            wPortInObj = dealPortInObj(portInObj, portInObjConf, backableSeek, seekType, "in-method");
        }

        return wPortInObj;
    }

    private static WPortInObj dealPortInObj(PortInObj portInObj, PortInObjConf portInObjConf, BackableSeek backableSeek,
            SeekType seekType, String tag) throws Exception
    {
        Class<?>[] types = portInObj.value();
        WPortInObj.One[] ones = new WPortInObj.One[types.length];
        for (int i = 0; i < types.length; i++)
        {
            ones[i] = buildOne(types[i], portInObjConf, tag, backableSeek, seekType);
        }
        WPortInObj wPortInObj = new WPortInObj(ones);
        return wPortInObj;
    }

    /**
     * 返回结果不为null。
     * 返回{@linkplain ParamDealt.FailedReason}表示失败，否则成功。
     */
    public static Object paramDealOne(ParamDealt paramDealt, WPortInObj.One one,
            ParamSource paramSource,
            TypeParserStore currentTypeParserStore)
    {
        Object obj;
        try
        {
            Object[] neces = PortUtil.newArray(one.inNames.nece);
            Object[] unneces = PortUtil.newArray(one.inNames.unece);
            Object reason = paramDeal(paramDealt, one.inNames, neces, unneces, paramSource, currentTypeParserStore);
            if (reason == null)
            {
                Object object = WPTool.newObject(one.clazz);

                for (int k = 0; k < neces.length; k++)
                {
                    one.neceObjFields[k].set(object, neces[k]);
                }

                for (int k = 0; k < unneces.length; k++)
                {
                    if (!(unneces[k] == null && one.unneceObjFields[k].getType().isPrimitive()))
                    {
                        one.unneceObjFields[k].set(object, unneces[k]);
                    }
                }

                obj = object;
            } else
            {
                obj = reason;
            }
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            obj = DefaultFailedReason.parsePortInObjException(e.getMessage());
        }
        return obj;
    }

    /**
     * 参数处理
     *
     * @return 返回null表示转换成功，否则表示失败。
     */
    public static ParamDealt.FailedReason paramDeal(ParamDealt paramDealt, InNames inNames, Object[] nece,
            Object[] unnece,
            ParamSource paramSource,
            TypeParserStore currentTypeParserStore)
    {
        ParamDealt.FailedReason reason = paramDealt.deal(inNames.nece, nece, true, paramSource, currentTypeParserStore);
        if (reason == null)
        {
            reason = paramDealt.deal(inNames.unece, unnece, false, paramSource, currentTypeParserStore);
        }
        return reason;
    }

//    public static String idOfField(Field field, Class<?> clazz)
//    {
//        return field.toString();
//    }

    /**
     * 用于对象和变量参数的绑定
     */
    public static WPortInObj.One buildInOne(Class<?> clazz,
            PortInObjConf portInObjConf, String tag) throws Exception
    {
        return buildOne(clazz, portInObjConf, tag, null, null);
    }


    public static class CacheOne
    {
        private WPortInObj.One one;
        private static final Map<Class<?>, CacheOne> CACHES = new ConcurrentHashMap<>();

        public CacheOne(WPortInObj.One one)
        {
            this.one = one;
        }

        public WPortInObj.One getOne()
        {
            return one;
        }

        public static void put(Class<?> clazz, CacheOne cacheOne)
        {
            CACHES.put(clazz, cacheOne);
        }

        public static CacheOne getCacheOne(Class<?> clazz)
        {
            return CACHES.get(clazz);
        }
    }

    /**
     * 获取注解，若当前函数为找到且此注解具有继承性则会尝试从父类中的(public)函数中查找。
     *
     * @param method
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass)
    {
        T t = getAnnotation(method, annotationClass, annotationClass.isAnnotationPresent(Inherited.class));
        return t;
    }

    private static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass, boolean seekSuper)
    {
        T t = method.getAnnotation(annotationClass);
        if (t == null && seekSuper)
        {
            Class<?> clazz = method.getDeclaringClass().getSuperclass();
            if (clazz != null)
            {
                try
                {
                    method = clazz.getMethod(method.getName(), method.getParameterTypes());
                    t = getAnnotation(method, annotationClass, true);
                } catch (NoSuchMethodException e)
                {

                }
            }
        }
        return t;
    }

//    /**
//     * 移除被重写的父类方法
//     *
//     * @param methods
//     * @return
//     */
//    public static Method[] removeSuperMethods(Method[] methods)
//    {
//        ArrayList<Method> list = new ArrayList<>();
//        for (int i = 0; i < methods.length; i++)
//        {
//            List<Method> ms = getSameMethods(methods, i);
//            if (ms != null)
//            {
//                Method method = ms.get(0);
//                for (int k = 1; k < ms.size(); k++)
//                {
//                    Method m = ms.get(k);
//                    if (WPTool.isAssignable(m.getDeclaringClass(), method.getDeclaringClass()))
//                    {
//                        method = m;
//                    }
//                }
//                list.add(method);
//            }
//        }
//
//        return list.toArray(new Method[0]);
//    }
//
//    private static List<Method> getSameMethods(Method[] methods, int index)
//    {
//        Method method = methods[index];
//        if (method != null)
//        {
//            ArrayList<Method> indexes = new ArrayList<>();
//            indexes.add(method);
//            for (int i = index + 1; i < methods.length; i++)
//            {
//                Method m = methods[i];
//                if (m == null)
//                {
//                    continue;
//                } else if (m.getName().equals(method.getName()))
//                {
//                    Type[] types = method.getGenericParameterTypes();
//                    Type[] ts = m.getGenericExceptionTypes();
//                    if (types.length != ts.length)
//                    {
//                        continue;
//                    }
//                    boolean isS = true;
//                    for (int k = 0; k < types.length; k++)
//                    {
//                        if (!types[k].equals(ts[k]))
//                        {
//                            isS = false;
//                            break;
//                        }
//                    }
//                    if (isS)
//                    {
//                        indexes.add(m);
//                        methods[i] = null;
//                    }
//                }
//
//            }
//            return indexes;
//        }
//        return null;
//    }

    /**
     * 用于对象和变量参数的绑定
     */
    private static WPortInObj.One buildOne(Class<?> clazz,
            PortInObjConf portInObjConf, String tag, BackableSeek backableSeek,
            SeekType seekType) throws Exception
    {
        WPortInObj.One one;
        if (Modifier.isInterface(clazz.getModifiers()))
        {
            if (clazz.isAnnotationPresent(AutoGen.class))
            {
                AutoGen autoGen = clazz.getAnnotation(AutoGen.class);
                String name;
                Class<?> key = autoGen.classValue();
                if (key.equals(AutoGen.class))
                {
                    name = autoGen.value();
                } else
                {
                    name = key.getName();
                }
                if ("".equals(name))
                {
                    clazz = PackageUtil
                            .newClass(clazz.getName() + PorterProcessor.SUFFIX, portInObjConf.getClassLoader());
                } else
                {
                    Class<?> c = portInObjConf.getAutoGenImplMap().get(name);
                    if (c == null)
                    {
                        throw new RuntimeException("not find interface implementation of " + clazz);
                    } else
                    {
                        clazz = c;
                    }
                }
            } else
            {
                throw new RuntimeException("interface have to be with annotation @" + AutoGen.class.getSimpleName());
            }
        } else if (Modifier.isAbstract(clazz.getModifiers()))
        {//若是接口，执行此句也会为true。
            throw new RuntimeException("abstract class is not supported!(" + clazz + ")");
        }

        Field[] fields = WPTool.getAllFields(clazz);
        List<Field> neces = new ArrayList<>();
        List<InNames.Name> neceNames = new ArrayList<>();
        List<Field> unneces = new ArrayList<>();
        List<InNames.Name> unneceNames = new ArrayList<>();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            boolean isAuto = true;
            InNames.Name name;
            field.setAccessible(true);
            List<InNames.Name> list = null;
            String nameStr = null;
            if (field.isAnnotationPresent(PortInObj.Nece.class))
            {
                PortInObj.Nece nece = field.getAnnotation(PortInObj.Nece.class);
                nameStr = tied(nece, field, portInObjConf.isEnableDefaultValue());
                neces.add(field);
                list = neceNames;
                isAuto = field.getAnnotation(PortInObj.Nece.class).autoParse();
            } else if (field.isAnnotationPresent(PortInObj.UnNece.class))
            {
                PortInObj.UnNece unNece = field.getAnnotation(PortInObj.UnNece.class);
                nameStr = tied(unNece, field, portInObjConf.isEnableDefaultValue());
                unneces.add(field);
                list = unneceNames;
                isAuto = field.getAnnotation(PortInObj.UnNece.class).autoParse();
            }
            if (list != null)
            {

                if (isAuto)
                {
                    Class<? extends ITypeParser> typeParser;
                    try
                    {
                        typeParser = ParserUtil.getTypeParser(field.getType());
                    } catch (ClassNotFoundException e)
                    {
                        LOGGER.error("auto get {} for field '{}' failed!", ITypeParser.class.getSimpleName(), field);
                        throw e;
                    }
                    String typeId = putTypeParser(typeParser, portInObjConf.getTypeParserStore());
                    name = new InNames.Name(nameStr, typeId);
                } else
                {
                    name = new InNames.Name(nameStr, null);
                    LOGGER.warn("No parser for [{}]({},tag={})", field, clazz,tag);
                }
                if (backableSeek != null)
                {
                    if ((seekType == SeekType.Add_Bind || seekType == SeekType.Add_NotBind) && name.typeParserId !=
                            null)
                    {
                        backableSeek.put(name.varName, name.typeParserId);
                    }
                    //未绑定的才会进行绑定
                    if ((seekType == SeekType.Add_Bind || seekType == SeekType.NotAdd_Bind) && name.typeParserId ==
                            null)
                    {
                        name.typeParserId = backableSeek.getTypeId(name.varName);
                        if(name.typeParserId!=null){
                            LOGGER.debug("bind parser for [{}](parser id={})",field,backableSeek,name.typeParserId);
                        }
                    }
                }
                list.add(name);
            }
        }
        one = new WPortInObj.One(clazz,
                new InNames(neceNames.toArray(new InNames.Name[0]), unneceNames.toArray(new InNames.Name[0]), null),
                neces.toArray(new Field[0]), unneces.toArray(new Field[0]));


        CacheOne cacheOne = new CacheOne(one);
        //获取父类的绑定。
        BindFromSuperUtil.bindFromSuperClass(clazz, cacheOne);
        CacheOne.put(clazz, cacheOne);
        return one;
    }


}
