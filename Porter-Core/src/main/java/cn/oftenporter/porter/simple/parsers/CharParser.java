package cn.oftenporter.porter.simple.parsers;

import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
public class CharParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if (value instanceof Character)
            {
                v = value;
            } else
            {
                v = String.valueOf(value).charAt(0);
            }
            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
