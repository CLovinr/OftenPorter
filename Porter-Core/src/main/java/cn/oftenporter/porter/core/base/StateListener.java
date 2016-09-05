package cn.oftenporter.porter.core.base;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.init.InitParamSource;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface StateListener
{
    void beforeSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager);

    void afterSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager);

    void afterStart(InitParamSource initParamSource);

    void beforeDestroy();

    void afterDestroy();

    public class Adapter implements StateListener
    {

        @Override
        public void beforeSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager)
        {

        }

        @Override
        public void afterSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager)
        {

        }

        @Override
        public void afterStart(InitParamSource initParamSource)
        {

        }

        @Override
        public void beforeDestroy()
        {

        }

        @Override
        public void afterDestroy()
        {

        }
    }
}
