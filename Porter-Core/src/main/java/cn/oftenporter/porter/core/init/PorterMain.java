package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.*;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.simple.DefaultTypeParserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 接口入口对象。
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PorterMain
{
    private PortExecutor portExecutor;

    private boolean isInit;
    private static final Logger LOGGER = LoggerFactory.getLogger(PorterMain.class);
    private Map<String, Object> globalAutoSet;
    private TypeParserStore globalParserStore;
    private List<CheckPassable> allGlobalChecks;

    public PorterMain()
    {
        globalAutoSet = new ConcurrentHashMap<>();
        this.globalParserStore = new DefaultTypeParserStore();
        this.allGlobalChecks = new Vector<>();
    }

    public PorterConf newPorterConf()
    {
        return new PorterConf();
    }


    public synchronized void addGlobalCheck(CheckPassable checkPassable) throws RuntimeException
    {
        if (allGlobalChecks == null)
        {
            throw new RuntimeException("illegal invoke time!");
        }
        allGlobalChecks.add(checkPassable);
    }

    public synchronized void init(UrlDecoder urlDecoder, boolean responseWhenException)
    {
        if (isInit)
        {
            throw new RuntimeException("already init!");
        }
        isInit = true;
        portExecutor = new PortExecutor(globalAutoSet, globalParserStore, urlDecoder, responseWhenException);
    }

    private void checkInit()
    {
        if (!isInit)
        {
            throw new RuntimeException("not init!");
        }
    }

    public synchronized void addGlobalTypeParser(ITypeParser typeParser)
    {
        globalParserStore.put(typeParser.id(), typeParser);
    }

    public synchronized void addGlobalAutoSet(String name, Object object)
    {
        Object last = globalAutoSet.put(name, object);
        if (last != null)
        {
            LOGGER.warn("the global object named '{}' added before [{}]", name, last);
        }
    }

    public synchronized void startOne(PorterBridge bridge) throws RuntimeException
    {
        checkInit();
        if (portExecutor.containsContext(bridge.contextName()))
        {
            throw new RuntimeException("Context named '" + bridge.contextName() + "' already exist!");
        }

        if (allGlobalChecks != null)
        {
            CheckPassable[] alls = allGlobalChecks.toArray(new CheckPassable[0]);
            allGlobalChecks = null;
            portExecutor.initAllGlobalChecks(alls);
        }

        PorterConf porterConf = bridge.porterConf();
        PortContext portContext = new PortContext();
        portContext.setClassLoader(porterConf.getClassLoader());

        LOGGER.debug("{} beforeSeek...", porterConf.getContextName());

        StateListenerForAll stateListenerForAll = new StateListenerForAll(porterConf.getStateListenerSet());
        ParamSourceHandleManager paramSourceHandleManager = bridge.paramSourceHandleManager();

        stateListenerForAll.beforeSeek(porterConf.getUserInitParam(), porterConf, paramSourceHandleManager);
        portContext.initSeek(porterConf, globalParserStore, globalAutoSet, bridge);
        LOGGER.debug("{} afterSeek...", porterConf.getContextName());
        stateListenerForAll.afterSeek(porterConf.getUserInitParam(), paramSourceHandleManager);

        portContext.start();

        LOGGER.debug("{} afterStart...", porterConf.getContextName());
        stateListenerForAll.afterStart(porterConf.getUserInitParam());
        portExecutor.addContext(bridge, portContext, stateListenerForAll);
        porterConf.initOk();
        LOGGER.debug("{} started!", porterConf.getContextName());


    }

    public synchronized void destroyAll()
    {
        checkInit();
        LOGGER.debug("destroyAll...");
        Iterator<PortExecutor.Context> iterator = portExecutor.contextIterator();
        while (iterator.hasNext())
        {
            PortExecutor.Context context = iterator.next();
            context.setEnable(false);
            destroyOne(context);
        }
        portExecutor.clear();
        LOGGER.debug("destroyAll end!");
    }

    private void destroyOne(PortExecutor.Context context)
    {
        checkInit();
        if (context != null && context.stateListenerForAll != null)
        {
            String contextName = context.getName();
            StateListener stateListenerForAll = context.stateListenerForAll;
            LOGGER.debug("{} beforeDestroy...", contextName);
            stateListenerForAll.beforeDestroy();
            context.portContext.destroy();
            LOGGER.debug("{} destroyed!", contextName);
            stateListenerForAll.afterDestroy();
        }
    }

    public synchronized void destroyOne(String contextName)
    {
        checkInit();
        PortExecutor.Context context = portExecutor.removeContext(contextName);
        destroyOne(context);
    }

    public synchronized void enableContext(String contextName, boolean enable)
    {
        checkInit();
        portExecutor.enableContext(contextName, enable);
    }

    public void doRequest(PortExecutor.Request req, WRequest request, WResponse response)
    {
        portExecutor.doRequest(req, request, response);
    }

    public PortExecutor.Request forRequest(WRequest request, WResponse response)
    {
        return portExecutor.forRequest(request, response);
    }
}
