package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

import java.math.BigDecimal;

/**
 */
public class BigDecimalParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;

        try
        {
            Object v;
            if (value instanceof BigDecimal)
            {
                v = value;
            } else
            {
                v = new BigDecimal(value.toString());
            }

            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
