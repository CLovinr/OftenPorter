package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.StateListener;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.init.InitParamSource;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.local.porter.Article;
import cn.oftenporter.porter.local.porter.Demo;
import cn.oftenporter.porter.local.porter.IDemo;
import cn.oftenporter.porter.local.porter.User;
import cn.oftenporter.porter.local.porter2.My2Porter;
import cn.oftenporter.porter.local.porter2.MyPorter;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.junit.Assert.*;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
public class TestLocalMain
{

    @Test
    public void main()
    {
        PropertyConfigurator.configure(getClass().getResourceAsStream("/log4j.properties"));
        LocalMain localMain = new LocalMain(true, "", "utf-8");
        PorterConf porterConf = localMain.newPorterConf();
        porterConf.setContextName("Local-1");
        porterConf.getSeekPackages().addPorters("cn.oftenporter.porter.local.porter");
        porterConf.getSeekPackages().addClassPorter(My2Porter.class)
                .addObjectPorter(new MyPorter("Hello MyPorter!"));
        porterConf.addContextAutoGenImpl(IDemo.class.getName(), Demo.class);
        porterConf.setEnableTiedNameDefault(false);
        porterConf.addContextAutoSet("globalName", "全局对象");
        final Logger logger = LoggerFactory.getLogger(getClass());
        porterConf.addStateListener(new StateListener()
        {
            @Override
            public void beforeSeek(InitParamSource initParamSource, PorterConf porterConf,
                    ParamSourceHandleManager paramSourceHandleManager)
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

        porterConf.addContextCheck(new CheckPassable()
        {
            @Override
            public Object willPass(WObject wObject, DuringType type)
            {
                //logger.debug("");
                return null;
            }
        });

        localMain.startOne(porterConf);
        long time = System.currentTimeMillis();
        int n = 100000;
        int threads = 30;
        //多线程下测试
        exe(threads, n, localMain.getBridge());
        long total = System.currentTimeMillis()-time;
        logger.debug("**************************************");
        logger.debug("threads={},n={}:total={}ms,average={}ms", threads,n,total,total/n);
        logger.debug("**************************************");
        localMain.destroyAll();
    }

    private void exe(int threads, int n, final LBridge bridge)
    {
        ExecutorService executorService = Executors.newFixedThreadPool(threads, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });

        for (int i = 0; i < n; i++)
        {
            executorService.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    bridge.request(new LRequest("/Local-1/Hello/say").addParam("name", "小明").addParam("age", "22")
                                    .addParam("myAge", 22),
                            new LCallback()
                            {
                                @Override
                                public void onResponse(LResponse lResponse)
                                {
                                    assertEquals("小明+22", lResponse.first());
                                }
                            });

                    bridge.request(new LRequest("/Local-1/Hello/parseObject").addParam("title", "转换成对象")
                                    .addParam("comments", "['c1','c2']")
                                    .addParam("content", "this is content!")
                                    .addParam("time", String.valueOf(System.currentTimeMillis()))
                                    .addParam("name", "小傻").addParam("myAge", "18"),
                            new LCallback()
                            {
                                @Override
                                public void onResponse(LResponse lResponse)
                                {
                                    assertTrue(
                                            lResponse.first() instanceof User || lResponse.first() instanceof Article);
                                }
                            });

                    bridge.request(new LRequest("/Local-1/Hello/").addParam("sex", "男").addParam("name", "name2")
                            .addParam("myAge", 10), new LCallback()
                    {
                        @Override
                        public void onResponse(LResponse lResponse)
                        {
                            assertEquals("=男", lResponse.first());
                        }
                    });


                    bridge.request(new LRequest("/Local-1/Hello").setMethod(PortMethod.POST).addParam("name", "name3")
                            .addParam("myAge", 10).addParam("sex", "0"), new LCallback()
                    {
                        @Override
                        public void onResponse(LResponse lResponse)
                        {
                            assertEquals(":0", lResponse.first());
                        }
                    });

                    bridge.request(new LRequest("/Local-1/Hello/hihihi").setMethod(PortMethod.POST)
                            .addParam("name", "name4")
                            .addParam("myAge", 10).addParam("sex", "0"), new LCallback()
                    {
                        @Override
                        public void onResponse(LResponse lResponse)
                        {
                            assertEquals("hihihi:0", lResponse.first());
                        }
                    });

                    bridge.request(new LRequest("/Local-1/My2/hello"), new LCallback()
                    {
                        @Override
                        public void onResponse(LResponse lResponse)
                        {
                            assertEquals("My2Porter", lResponse.first());
                        }
                    });
                    bridge.request(new LRequest("/Local-1/My/hello").addParam("name", "Demo001"), new LCallback()
                    {
                        @Override
                        public void onResponse(LResponse lResponse)
                        {
                            assertTrue(lResponse.first() instanceof IDemo);
                        }
                    });

                }
            });
        }

        try
        {
            executorService.shutdown();
            while (!executorService.isTerminated())
            {
                Thread.sleep(20);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
