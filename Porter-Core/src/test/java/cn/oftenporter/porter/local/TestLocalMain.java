package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.StateListener;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.init.InitParamSource;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
public class TestLocalMain
{

    @Test
    public void main()
    {
        PropertyConfigurator.configure(getClass().getResourceAsStream("/log4j.properties"));
        LocalMain localMain = new LocalMain("LocalMain", "");
        localMain.getPorterConf().getSeekPackages().addPorters("cn.oftenporter.porter.local.porter");
        localMain.getPorterConf().setEnablePortInTiedNameDefault(false);
        localMain.getPorterConf().addGlobalAutoSetObject("globalName","全局对象");
        final Logger logger = LoggerFactory.getLogger(getClass());
        localMain.getPorterConf().addStateListener(new StateListener()
        {
            @Override
            public void beforeSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager)
            {
                initParamSource.putInitParameter("debug", true);
                logger.debug("");
            }

            @Override
            public void afterSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager)
            {
                logger.debug("");
            }

            @Override
            public void afterStart(InitParamSource initParamSource)
            {
                logger.debug("{}", initParamSource.getInitParameter("debug"));
            }

            @Override
            public void beforeDestroy()
            {
                logger.debug("");
            }

            @Override
            public void afterDestroy()
            {
                logger.debug("");
            }
        });

        localMain.getPorterConf().addGlobalCheck(new CheckPassable()
        {
            @Override
            public Object willPass(WObject wObject, Type type)
            {
                logger.debug("");
                return null;
            }
        });

        localMain.start();

        localMain.getBridge()
                .request(new LRequest("/Hello/say").addParam("name", "小明").addParam("age", "22"), new LCallback()
                {
                    @Override
                    public void onResponse(LResponse lResponse)
                    {
                        logger.debug("{}", lResponse);
                    }
                });

        localMain.getBridge()
                .request(new LRequest("/Hello/parseObject").addParam("title", "转换成对象")
                                .addParam("comments", "['c1','c2']")
                                .addParam("content", "this is content!")
                                .addParam("time", String.valueOf(System.currentTimeMillis()))
                                .addParam("name", "小傻").addParam("myAge", "18"),
                        new LCallback()
                        {
                            @Override
                            public void onResponse(LResponse lResponse)
                            {
                                logger.debug("{}", lResponse);
                            }
                        });

        localMain.getBridge()
                .request(new LRequest("/Hello/parseObject"),
                        new LCallback()
                        {
                            @Override
                            public void onResponse(LResponse lResponse)
                            {
                                logger.debug("{}", lResponse);
                            }
                        });

        localMain.getBridge()
                .request(new LRequest("/Hello/hihihi"), new LCallback()
                {
                    @Override
                    public void onResponse(LResponse lResponse)
                    {
                        logger.debug("{}", lResponse);
                    }
                });

        localMain.destroy();
    }
}
