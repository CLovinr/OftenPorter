package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.StateListener;
import cn.oftenporter.porter.core.base.WPObject;
import cn.oftenporter.porter.core.init.InitParamSource;
import cn.oftenporter.porter.core.util.LogUtil;
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
        LocalMain localMain = new LocalMain("LocalMain");
        localMain.getPorterConf().getSeekPackages().addPorters("cn.oftenporter.porter.local.porter");
        localMain.getPorterConf().setEnablePortInTiedNameDefault(false);

        localMain.getPorterConf().addStateListener(new StateListener()
        {
            @Override
            public void beforeSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager)
            {
                initParamSource.putInitParameter("debug",true);
                LogUtil.printErrPosLn();
            }

            @Override
            public void afterSeek(InitParamSource initParamSource, ParamSourceHandleManager paramSourceHandleManager)
            {
                LogUtil.printErrPosLn();
            }

            @Override
            public void afterStart(InitParamSource initParamSource)
            {
                LogUtil.printErrPosLn(initParamSource.getInitParameter("debug"));
            }

            @Override
            public void beforeDestroy()
            {
                LogUtil.printErrPosLn();
            }

            @Override
            public void afterDestroy()
            {
                LogUtil.printErrPosLn();
            }
        });

        localMain.getPorterConf().addGlobalCheck(new CheckPassable()
        {
            @Override
            public Object willPass(WPObject wpObject, Type type)
            {
                LogUtil.printErrPosLn();
                return null;
            }
        });

        localMain.start();
        long time = System.currentTimeMillis();

        localMain.getBridge()
                .request(new LRequest("/Hello/say").addParam("name", "小明").addParam("age", "22"), new LCallback()
                {
                    @Override
                    public void onResponse(LResponse lResponse)
                    {
                        LogUtil.printPosLn(lResponse);
                    }
                });

        LogUtil.printPosLn("time=", (System.currentTimeMillis() - time), "ms");
        localMain.destroy();
    }
}
