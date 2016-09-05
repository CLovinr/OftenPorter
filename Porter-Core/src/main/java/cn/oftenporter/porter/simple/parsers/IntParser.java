package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 * 十进制int类型
 */
public class IntParser implements TypeParser
{


    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if ((value.getClass().isPrimitive() && value.getClass()
                    .equals(Integer.class)) || (value instanceof Integer))
            {
                v = value;
            } else
            {
                v = Integer.parseInt(value.toString());
            }

            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
