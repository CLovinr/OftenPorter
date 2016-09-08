package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 * byte类型
 */
public class ByteParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if (value instanceof Byte)
            {
                v = value;
            } else
            {
                v = Byte.parseByte(value.toString());
            }
            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
