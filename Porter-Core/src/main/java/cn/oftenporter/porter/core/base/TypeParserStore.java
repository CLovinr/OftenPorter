package cn.oftenporter.porter.core.base;

/**
 * Created by https://github.com/CLovinr on 2016/7/24.
 */
public interface TypeParserStore
{
    ITypeParser byId(String id);

    void put(String id, ITypeParser typeParser);

    boolean contains(String id);
}
