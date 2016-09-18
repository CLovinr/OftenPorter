package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;

/**
 * byte类型
 */
public class ByteParser extends TypeParser
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
            result = ParserUtil.failed(this,e.getMessage());;
        }
        return result;
    }
}
