package cn.oftenporter.porter.simple.parsers;

import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 */
public class DoubleParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if ((value.getClass().isPrimitive() && value.getClass().equals(Double.class)) || (value instanceof Double))
            {
                v = value;
            } else
            {
                v = Double.parseDouble(value.toString());
            }

            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
