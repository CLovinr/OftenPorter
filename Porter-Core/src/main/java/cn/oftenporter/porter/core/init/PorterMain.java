package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.*;
import cn.oftenporter.porter.core.base.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PorterMain
{
    private String name;
    private PortContext portContext;
    private PortExecutor portExecutor;
    private PorterConf porterConf;
    private boolean isStarted;
    private static final Logger LOGGER = LoggerFactory.getLogger(PorterMain.class);

    public PorterMain(String name)
    {
        this.name = name;
        porterConf = new PorterConf();
    }

    public PorterConf getPorterConf()
    {
        return porterConf;
    }

    public String getName()
    {
        return name;
    }


    public void start(PorterBridge bridge)
    {
        if (isStarted)
        {
            throw new RuntimeException("already started!");
        }
        isStarted = true;
        portContext = new PortContext();
        portContext.setClassLoader(porterConf.getClassLoader());

        LOGGER.debug("{} beforeSeek...", getName());

        ParamSourceHandleManager paramSourceHandleManager = new ParamSourceHandleManager();
        Iterator<StateListener> stateListenerIterator = porterConf.getStateListenerSet().iterator();
        while (stateListenerIterator.hasNext())
        {
            stateListenerIterator.next().beforeSeek(porterConf.getUserInitParam(), paramSourceHandleManager);
        }
        portContext.initSeek(porterConf);

        LOGGER.debug("{} afterSeek...", getName());
        stateListenerIterator = porterConf.getStateListenerSet().iterator();
        while (stateListenerIterator.hasNext())
        {
            stateListenerIterator.next().afterSeek(porterConf.getUserInitParam(), paramSourceHandleManager);
        }
        portExecutor = new PortExecutor(portContext, paramSourceHandleManager,
                bridge, porterConf);
        portContext.start();

        LOGGER.debug("{} afterStart...", getName());
        stateListenerIterator = porterConf.getStateListenerSet().iterator();
        while (stateListenerIterator.hasNext())
        {
            stateListenerIterator.next().afterStart(porterConf.getUserInitParam());
        }
        porterConf.initOk();
        LOGGER.debug("{} started!", getName());
    }

    public void destroy()
    {
        LOGGER.debug("{} beforeDestroy...", getName());
        Iterator<StateListener> stateListenerIterator = porterConf.getStateListenerSet().iterator();
        while (stateListenerIterator.hasNext())
        {
            stateListenerIterator.next().beforeDestroy();
        }
        portContext.destroy();
        LOGGER.debug("{} destroyed!", getName());
        stateListenerIterator = porterConf.getStateListenerSet().iterator();
        while (stateListenerIterator.hasNext())
        {
            stateListenerIterator.next().afterDestroy();
        }
    }

    public void doRequest(WRequest request, WResponse response, PortMethod method)
    {
        portExecutor.doRequest(request, response, method);
    }
}
