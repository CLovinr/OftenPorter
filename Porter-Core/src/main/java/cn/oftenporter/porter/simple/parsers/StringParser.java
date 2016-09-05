package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;

/**
 * Created by 宇宙之灵 on 2015/9/14.
 */
public class StringParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        return new ParseResult(String.valueOf(value));
    }
}
