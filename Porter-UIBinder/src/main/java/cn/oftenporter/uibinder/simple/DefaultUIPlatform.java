package cn.oftenporter.uibinder.simple;

import cn.oftenporter.uibinder.core.BinderFactory;
import cn.oftenporter.uibinder.core.IdDeal;
import cn.oftenporter.uibinder.core.UIPlatform;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/6.
 */
public class DefaultUIPlatform implements UIPlatform
{
    private IdDeal idDeal = new DefaultIdDeal();
    private BinderFactory binderFactory;

    public DefaultUIPlatform(BinderFactory binderFactory)
    {
        this.binderFactory = binderFactory;
    }

    @Override
    public BinderFactory getBinderFactory()
    {
        return binderFactory;
    }

    @Override
    public IdDeal getIdDeal()
    {
        return idDeal;
    }
}
