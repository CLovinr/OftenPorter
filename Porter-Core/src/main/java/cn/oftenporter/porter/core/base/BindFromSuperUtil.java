package cn.oftenporter.porter.core.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * <br>
 * Created by https://github.com/CLovinr on 2016/9/11.
 */
class BindFromSuperUtil
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BindFromSuperUtil.class);
    /**
     * 获取父类的绑定。
     *
     * @param clazz
     * @param cacheOne
     */
    public static void bindFromSuperClass(Class<?> clazz, PortUtil.CacheOne cacheOne)
    {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class))
        {
            PortUtil.CacheOne superCache = PortUtil.CacheOne.getCacheOne(superClass);
            if (superCache == null)
            {
                return;
            }
            WPortInObj.One superOne = superCache.getOne();
            InNames superInNames = superOne.inNames;

            WPortInObj.One currentOne = cacheOne.getOne();
            InNames currentInNames = currentOne.inNames;

            mayBind(clazz,currentOne.neceObjFields, currentInNames.nece,superClass, superOne.neceObjFields, superInNames.nece);
            mayBind(clazz,currentOne.unneceObjFields, currentInNames.unece,superClass, superOne.unneceObjFields, superInNames.unece);

            bindFromSuperClass(superClass, cacheOne);
        }
    }

    private static void mayBind(Class<?> clazz,Field[] fields, InNames.Name[] names,Class<?> superClass , Field[] fieldsSuper, InNames.Name[] namesSuper)
    {
        for (int i = 0; i < names.length; i++)
        {
            InNames.Name name = names[i];
            //未绑定的才会执行
            if (name.typeParserId == null)
            {
                Field field = fields[i];
                for (int k = 0; k < fieldsSuper.length; k++)
                {
                    if (field.equals(fieldsSuper[k]))
                    {
                        name.typeParserId = namesSuper[k].typeParserId;
                        if(name.typeParserId!=null){
                            LOGGER.debug("bind [{}]({}) with [{}] ({})",field,clazz,fieldsSuper[k],superClass);
                        }
                        break;
                    }
                }
            }
        }
    }
}
