package cn.oftenporter.porter.simple.parsers;

import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 * Created by 宇宙之灵 on 2015/9/14.
 */
public class ShortParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if ((value.getClass().isPrimitive() && value.getClass().equals(Short.class)) || (value instanceof Short))
            {
                v = value;
            } else
            {
                v = Short.parseShort(value.toString());
            }
            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
