package cn.oftenporter.porter.core.base;

/**
 * Created by https://github.com/CLovinr on 2016/7/24.
 */
public interface TypeParserStore
{
    TypeParser byName(String name);

    void put(String name, TypeParser typeParser);

    boolean contains(String name);
}
