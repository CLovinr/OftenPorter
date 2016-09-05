package cn.oftenporter.porter.simple.parsers;

import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.TypeParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;

/**
 * 把json数组格式的字符串转换为{@linkplain JSONArray}
 */
public class JSONArrayParser implements TypeParser
{
    @Override
    public ParseResult parse(@NotNull String name, @NotNull Object value)
    {
        ParseResult result;
        try
        {
            Object v;
            if (value instanceof JSONArray)
            {
                v = value;
            } else
            {
                JSONArray array = JSON.parseArray(value.toString());
                v = array;
            }
            result = new ParseResult(v);
        } catch (JSONException e)
        {
            result = ParseResult.ILLEGAL;
        }
        return result;
    }
}
