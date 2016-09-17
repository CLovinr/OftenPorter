package cn.oftenporter.porter.simple.parsers;

import cn.oftenporter.porter.core.base.ITypeParser;

/**
 * <br>
 * Created by https://github.com/CLovinr on 2016/9/10.
 */
abstract class TypeParser implements ITypeParser
{
    @Override
    public String id()
    {
        return getClass().getName();
    }
}
