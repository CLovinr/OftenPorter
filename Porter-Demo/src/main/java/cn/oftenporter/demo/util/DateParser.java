package cn.oftenporter.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.ITypeParser;

public class DateParser implements ITypeParser
{

    private SimpleDateFormat format;

    public DateParser()
    {
	format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
	ParseResult result;
	try
	{
	    Object v;
	    if (value instanceof Date)
	    {
		v = value;
	    }
	    else
	    {
		Date date = format.parse(String.valueOf(value));
		v = date;
	    }
	    result = new ParseResult(v);
	}
	catch (Exception e)
	{
	    result = ParseResult
		    .failed(getClass().getSimpleName() + ":" + e.getMessage());
	}

	return result;
    }

    @Override
    public String id()
    {
	return getClass().getName();
    }

}
