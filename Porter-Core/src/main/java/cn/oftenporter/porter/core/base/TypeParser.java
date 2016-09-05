package cn.oftenporter.porter.core.base;


import cn.oftenporter.porter.core.annotation.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface TypeParser
{

    ParseResult parse(@NotNull String name, @NotNull Object value);

    public class ParseResult
    {

        public static final ParseResult ILLEGAL = new ParseResult();

        private boolean isLegal;
        private Object value;

        /**
         * 转换不合法
         */
        public ParseResult()
        {
            this.isLegal = false;
        }

        /**
         * 转换合法
         *
         * @param value 结果值,可以为{@link DecodeParams}类型。
         */
        public ParseResult(@NotNull Object value)
        {
            setValue(value);
            this.isLegal = true;
        }

        /**
         * 设置值
         *
         * @param value
         */
        public void setValue(@NotNull Object value)
        {
            if (value == null)
            {
                throw new NullPointerException();
            }
            this.value = value;
        }

        /**
         * 设置结果是否合法
         *
         * @param isLegal
         */
        public void setIsLegal(boolean isLegal)
        {
            this.isLegal = isLegal;
        }

        public boolean isLegal()
        {
            return isLegal;
        }

        /**
         * 得到转换后的值
         *
         * @return
         */
        public Object getValue()
        {
            return value;
        }
    }

    /**
     * 用于分解参数。
     * <pre>
     *     假如：dec=obj
     *     obj为分解类型：1）若obj中含有名为dec的值val，则最终dec=val；。
     * </pre>
     * 返回的值会被统一放到一个临时Map中，以后在获取参数值时，优先从该map中获取,并且从它中获取的参数无法再被转换。
     */
    public static class DecodeParams
    {

        private Map<String, Object> params = new HashMap<>();

        public DecodeParams()
        {

        }

        public Map<String, Object> getParams()
        {
            return params;
        }

        public DecodeParams put(String name, Object value)
        {
            params.put(name, value);
            return this;
        }
    }
}
