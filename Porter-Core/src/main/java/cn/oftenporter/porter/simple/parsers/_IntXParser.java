package cn.oftenporter.porter.simple.parsers;


import cn.oftenporter.porter.core.annotation.NotNull;

/**
 *
 */
class _IntXParser extends TypeParser
{
    private int radix;

    public _IntXParser(int radix)
    {
        this.radix = radix;
    }

    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if (value instanceof Integer)
            {
                v = value;
            } else
            {
                int x = (int) Long.parseLong(value.toString(), radix);
                v = x;
            }

            result = new ParseResult(v);
        } catch (NumberFormatException e)
        {
            result = ParserUtil.failed(this,e.getMessage());;
        }
        return result;
    }
}
