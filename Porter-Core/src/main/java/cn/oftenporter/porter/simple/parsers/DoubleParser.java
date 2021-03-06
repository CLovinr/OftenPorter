package cn.oftenporter.porter.simple.parsers;

import cn.oftenporter.porter.core.annotation.NotNull;

/**
 */
public class DoubleParser extends TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if (value instanceof Double)
            {
                v = value;
            } else
            {
                v = Double.parseDouble(value.toString());
            }

            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParserUtil.failed(this,e.getMessage());
        }
        return result;
    }
}
