package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.InitParamSource;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.*;
import cn.oftenporter.porter.core.util.FileTool;
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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

/**
 * Created by https://github.com/CLovinr on 2016/9/4.
 */
public class TestLocalMain
{

    interface Listener
    {
        void onEnd(long totalDTime, int n);
    }

    @Test
    public void main()
    {
        PropertyConfigurator.configure(getClass().getResourceAsStream("/log4j.properties"));
        final LocalMain localMain = new LocalMain(true, new PName("P1"), "utf-8");
        PorterConf porterConf = localMain.newPorterConf();
        porterConf.setContextName("Local-1");
        porterConf.getSeekPackages().addPorters(getClass().getPackage().getName() + ".porter");
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
            public Object willPass(WObject wObject, DuringType type,Aspect aspect)
            {
                //logger.debug("");
                return null;
            }
        });

        localMain.startOne(porterConf);
        int n = 100000;
        final int threads = Runtime.getRuntime().availableProcessors();

        final ExecutorService executorService = Executors.newFixedThreadPool(threads, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setDaemon(false);
                return thread;
            }
        });

        //多线程下测试
        // final long time = System.nanoTime();
        exe(executorService, n, localMain.getPInit().currentBridge(), new Listener()
        {
            @Override
            public void onEnd(long totalDtime, int N)
            {

                logger.debug("**************************************");
                logger.debug("threads={},n={}:total={}ms,average={}ms", threads, N, 1.0 * totalDtime / 1000000,
                        1.0d * totalDtime / 1000000 / N);
                logger.debug("**************************************");

                logger.debug("**************AutoSet delay test******************");
                localMain.getPInit().currentBridge().request(new PRequest("/Local-1/Delay/test"), new PCallback()
                {
                    @Override
                    public void onResponse(PResponse lResponse)
                    {
                        logger.debug("{}", lResponse);
                    }
                });

                try
                {
                    hotTest(logger, localMain);
                } catch (Exception e)
                {
                    logger.error(e.getMessage(), e);
                }


                localMain.destroyAll();
                executorService.shutdown();
            }
        });


    }

    private void hotTest(final Logger logger, CommonMain commonMain) throws Exception
    {
        logger.debug("热部署测试...");
        File dir = new File(System.getProperty(
                "user.home") + File.separator + "porter-core" + File.separator + "test" + File.separator);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File clazzFile = new File(dir.getPath() + File.separator + "HotPorter.jar");


        FileTool.write2File(getClass().getResourceAsStream("/hot/hot1.jar"), clazzFile, true);
        PorterConf conf = commonMain.newPorterConf();
        conf.setClassLoader(new URLClassLoader(new URL[]{clazzFile.toURI().toURL()}));
        conf.getSeekPackages().addPorters("cn.oftenporter.porter.local.hot");
        conf.setContextName("hot-test");
        commonMain.startOne(conf);
        commonMain.getPInit().currentBridge().request(new PRequest("/hot-test/Hot/show"), new PCallback()
        {
            @Override
            public void onResponse(PResponse lResponse)
            {
                logger.debug(lResponse.toString());
            }
        });
        commonMain.destroyOne("hot-test");
        //////////////////////////////////////
        FileTool.write2File(getClass().getResourceAsStream("/hot/hot2.jar"), clazzFile, true);
        conf = commonMain.newPorterConf();
        conf.setClassLoader(new URLClassLoader(new URL[]{clazzFile.toURI().toURL()}));
        conf.getSeekPackages().addPorters("cn.oftenporter.porter.local.hot");
        conf.setContextName("hot-test");
        commonMain.startOne(conf);
        commonMain.getPInit().currentBridge().request(new PRequest("/hot-test/Hot/show"), new PCallback()
        {
            @Override
            public void onResponse(PResponse lResponse)
            {
                logger.debug(lResponse.toString());
            }
        });
    }

    private void exe(final ExecutorService executorService, final int n, final PBridge bridge, final Listener listener)
    {

        final AtomicInteger count = new AtomicInteger(0);
        final AtomicLong dtime = new AtomicLong(0);

        for (int i = 0; i < n; i++)
        {

            if (executorService == null)
            {
                bridge.request(new PRequest("/Local-1/Hello/say").addParam("name", "小明").addParam("age", "22")
                                .addParam("myAge", 22),
                        new PCallback()
                        {
                            @Override
                            public void onResponse(PResponse lResponse)
                            {
                                assertEquals("小明+22", lResponse.getResponse());
                            }
                        });
            } else
            {
                executorService.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        long time = System.nanoTime();

                        bridge.request(new PRequest("/Local-1/Hello/parseObject").addParam("title", "转换成对象")
                                        .addParam("comments", "['c1','c2']")
                                        .addParam("content", "this is content!")
                                        .addParam("time", String.valueOf(System.currentTimeMillis()))
                                        .addParam("name", "小傻").addParam("myAge", "18"),
                                new PCallback()
                                {
                                    @Override
                                    public void onResponse(PResponse lResponse)
                                    {
                                        assertTrue(
                                                lResponse.getResponse() instanceof User || lResponse
                                                        .getResponse() instanceof Article);
                                    }
                                });

                        bridge.request(new PRequest("/Local-1/Hello/say").addParam("name", "小明").addParam("age", "22")
                                        .addParam("myAge", 22),
                                new PCallback()
                                {
                                    @Override
                                    public void onResponse(PResponse lResponse)
                                    {
                                        assertEquals("小明+22", lResponse.getResponse());
                                    }
                                });

                        bridge.request(new PRequest("/Local-1/Hello/").addParam("sex", "男").addParam("name", "name2")
                                .addParam("myAge", 10), new PCallback()
                        {
                            @Override
                            public void onResponse(PResponse lResponse)
                            {
                                assertEquals("=男", lResponse.getResponse());
                            }
                        });


                        bridge.request(
                                new PRequest("/Local-1/Hello").setMethod(PortMethod.POST).addParam("name", "name3")
                                        .addParam("myAge", 10).addParam("sex", "0"), new PCallback()
                                {
                                    @Override
                                    public void onResponse(PResponse lResponse)
                                    {
                                        assertEquals(":0", lResponse.getResponse());
                                    }
                                });

                        bridge.request(new PRequest("/Local-1/Hello/hihihi").setMethod(PortMethod.POST)
                                .addParam("name", "name4")
                                .addParam("myAge", 10).addParam("sex", "0"), new PCallback()
                        {
                            @Override
                            public void onResponse(PResponse lResponse)
                            {
                                assertEquals("hihihi:0", lResponse.getResponse());
                            }
                        });

                        bridge.request(new PRequest("/Local-1/My2/hello"), new PCallback()
                        {
                            @Override
                            public void onResponse(PResponse lResponse)
                            {
                                assertEquals("My2Porter", lResponse.getResponse());
                            }
                        });
                        bridge.request(new PRequest("/Local-1/My/hello").addParam("name", "Demo001"), new PCallback()
                        {
                            @Override
                            public void onResponse(PResponse lResponse)
                            {
                                assertTrue(lResponse.getResponse() instanceof IDemo);
                            }
                        });
                        dtime.addAndGet(System.nanoTime() - time);
                        if (count.incrementAndGet() == n)
                        {
                            listener.onEnd(dtime.get(), n);
                            executorService.shutdown();
                        }
                    }
                });
            }


        }

        if (executorService != null)
        {

            try
            {
                while (!executorService.isTerminated())
                {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();

            }
        }

    }
}
