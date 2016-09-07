package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.ParamDealt;
import cn.oftenporter.porter.core.base.ParamSource;
import cn.oftenporter.porter.core.base.TypeParser;
import cn.oftenporter.porter.core.base.TypeParserStore;
import cn.oftenporter.porter.core.util.WPTool;

import java.util.Map;

/**
 * 默认的参数处理实现。
 * Created by https://github.com/CLovinr on 2016/9/3.
 */
public class DefaultParamDealt implements ParamDealt
{
    @Override
    public FailedReason deal(String[] names, Object[] values, boolean isNecessary, ParamSource paramSource,
            TypeParserStore typeParserStore)
    {
        for (int i = 0; i < names.length; i++)
        {
            Object value = getParam(names[i], paramSource, typeParserStore.byName(names[i]));
            if (value != null)
            {
                if (value instanceof FailedReason)
                {
                    return (FailedReason) value;
                } else
                {
                    values[i] = value;
                }
            } else if (isNecessary)
            {
                return DefaultFailedReason.lackNecessaryParams("Lack necessary params!", names[i]);
            }
        }
        return null;
    }

    public Object getParam(String name, ParamSource paramSource,
            TypeParser typeParser)
    {

        Object v = paramSource.getParam(name);
        if (!WPTool.isEmpty(v) && typeParser != null)
        {
            TypeParser.ParseResult parseResult = typeParser.parse(name, v);
            if (parseResult.isLegal())
            {
                Object obj = parseResult.getValue();
                if (obj instanceof TypeParser.DecodeParams)
                {
                    TypeParser.DecodeParams decodeParams = (TypeParser.DecodeParams) obj;
                    Map<String, Object> map = decodeParams.getParams();
                    paramSource.putNewParams(map);
                    v = map.get(name);
                } else
                {
                    v = obj;
                }
            } else
            {
                return DefaultFailedReason.illegalParams("Illegal param type!",name);
            }
        }
        if ("".equals(v))
        {
            v = null;
        }
        return v;
    }
}
