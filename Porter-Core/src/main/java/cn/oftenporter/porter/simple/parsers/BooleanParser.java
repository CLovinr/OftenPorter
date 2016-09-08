package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 * boolean类型
 */
public class BooleanParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if (value instanceof Boolean)
            {
                v = value;
            } else
            {
                v = Boolean.parseBoolean(value.toString());
            }
            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
