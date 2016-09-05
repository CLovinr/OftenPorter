package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.TypeParser;
import cn.oftenporter.porter.core.base.TypeParserNameStore;
import cn.oftenporter.porter.core.base.TypeParserStore;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
class TypeParserNameStoreImpl implements TypeParserNameStore, TypeParserStore
{
    private TypeParserNameStore classStore, methodStore;
    private TypeParserStore typeParserStore;

    public TypeParserNameStoreImpl(TypeParserStore typeParserStore, TypeParserNameStore classStore)
    {
        this.typeParserStore = typeParserStore;
        this.classStore = classStore;
    }

    public void setMethodStore(TypeParserNameStore methodStore)
    {
        this.methodStore = methodStore;
    }

    @Override
    public String typeName(String varName)
    {
        String typeName;
        if (methodStore != null)
        {
            typeName = methodStore.typeName(varName);//优先获取函数自身的绑定。
            if (typeName == null)
            {
                typeName = classStore.typeName(varName);
            }
        } else
        {
            typeName = classStore.typeName(varName);
        }

        return typeName;
    }

    @Override
    public TypeParser byName(String varName)
    {
        String typeName = typeName(varName);
        TypeParser typeParser = null;
        if (typeName != null)
        {
            typeParser = typeParserStore.byName(typeName);
        }
        return typeParser;
    }

    @Override
    public void put(String typeName, TypeParser typeParser)
    {
        throw new RuntimeException("stub!");
    }

    @Override
    public boolean contains(String varName)
    {
        throw new RuntimeException("stub!");
    }
}
