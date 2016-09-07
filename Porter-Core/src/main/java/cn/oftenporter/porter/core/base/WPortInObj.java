package cn.oftenporter.porter.core.base;

import cn.oftenporter.porter.core.annotation.Parser;

import java.lang.reflect.Field;

/**
 * 参数类型应该通过过{@linkplain Parser.parse}来转换。
 * Created by https://github.com/CLovinr on 2016/9/7.
 */
public class WPortInObj
{
    public static class One
    {
        public final Class<?> clazz;
        /**
         * {@linkplain InNames#inner}无效。
         */
        public final InNames inNames;
        public final Field[] neceObjFields, unneceObjFields;

        public One(Class<?> clazz, InNames inNames, Field[] neceObjFields, Field[] unneceObjFields)
        {
            this.clazz = clazz;
            this.inNames = inNames;
            this.neceObjFields = neceObjFields;
            this.unneceObjFields = unneceObjFields;
        }

        private boolean isObject = true;

        public boolean isObject()
        {
            return isObject;
        }
    }

    public final One[] ones;

    public WPortInObj(One[] ones)
    {
        this.ones = ones;
    }
}
