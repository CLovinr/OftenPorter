package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.*;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.pbridge.PBridge;
import cn.oftenporter.porter.core.pbridge.PInit;
import cn.oftenporter.porter.core.pbridge.PName;
import cn.oftenporter.porter.core.util.WPTool;
import cn.oftenporter.porter.simple.DefaultPInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 接口入口对象。
 * <pre>
 *     请求格式为[=pname]/contextName/ClassTied/[funTied|restValue][?name1=value1&name2=value2...]
 * </pre>
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PorterMain
{
    private PortExecutor portExecutor;

    private boolean isInit;
    private static final Logger LOGGER = LoggerFactory.getLogger(PorterMain.class);
    private InnerBridge innerBridge;
    private PInit pInit;

    /**
     * @param pName  框架名称。
     * @param bridge 只能访问当前实例的bridge。
     */
    public PorterMain(PName pName, PBridge bridge)
    {
        this.innerBridge = new InnerBridge();
        pInit = new DefaultPInit(pName, bridge);
    }

    public PorterConf newPorterConf()
    {
        return new PorterConf();
    }


    public synchronized void addGlobalCheck(CheckPassable checkPassable) throws RuntimeException
    {
        if (innerBridge.allGlobalChecksTemp == null)
        {
            throw new RuntimeException("just for the time when has no context!");
        }
        innerBridge.allGlobalChecksTemp.add(checkPassable);
    }

    public synchronized void init(UrlDecoder urlDecoder, boolean responseWhenException)
    {
        if (isInit)
        {
            throw new RuntimeException("already init!");
        }
        isInit = true;
        portExecutor = new PortExecutor(pInit, urlDecoder, responseWhenException);
    }


    public PInit getPInit()
    {
        return pInit;
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
        innerBridge.globalParserStore.putParser(typeParser);
    }

    public synchronized void addGlobalAutoSet(String name, Object object)
    {
        Object last = innerBridge.globalAutoSet.put(name, object);
        if (last != null)
        {
            LOGGER.warn("the global object named '{}' added before [{}]", name, last);
        }
    }

    public synchronized void startOne(PorterBridge bridge) throws RuntimeException
    {
        checkInit();
        if (WPTool.isEmpty(bridge.contextName()))
        {
            throw new RuntimeException("Context name is empty!");
        } else if (portExecutor.containsContext(bridge.contextName()))
        {
            throw new RuntimeException("Context named '" + bridge.contextName() + "' already exist!");
        }


        if (innerBridge.allGlobalChecksTemp != null)
        {//全局检测，在没有启动任何context时有效。
            CheckPassable[] alls = innerBridge.allGlobalChecksTemp.toArray(new CheckPassable[0]);
            innerBridge.allGlobalChecksTemp = null;
            portExecutor.initAllGlobalChecks(alls);
        }


        PorterConf porterConf = bridge.porterConf();
        PortContext portContext = new PortContext();
        portContext.setClassLoader(porterConf.getClassLoader());

        LOGGER.debug("{} beforeSeek...", porterConf.getContextName());

        StateListenerForAll stateListenerForAll = new StateListenerForAll(porterConf.getStateListenerSet());
        ParamSourceHandleManager paramSourceHandleManager = bridge.paramSourceHandleManager();

        stateListenerForAll.beforeSeek(porterConf.getUserInitParam(), porterConf, paramSourceHandleManager);

        InnerContextBridge innerContextBridge = new InnerContextBridge(porterConf.getClassLoader(), innerBridge,
                porterConf.getContextAutoSetMap(), porterConf.getContextAutoGenImplMap(),
                porterConf.isEnableTiedNameDefault(), bridge, porterConf.isResponseWhenException());

        portContext.initSeek(porterConf, innerContextBridge);
        LOGGER.debug("{} afterSeek...", porterConf.getContextName());
        stateListenerForAll.afterSeek(porterConf.getUserInitParam(), paramSourceHandleManager);

        portContext.start();

        LOGGER.debug("{} afterStart...", porterConf.getContextName());
        stateListenerForAll.afterStart(porterConf.getUserInitParam());
        portExecutor.addContext(bridge, portContext, stateListenerForAll, innerContextBridge);
        porterConf.initOk();
        LOGGER.debug("{} started!", porterConf.getContextName());


    }

    public synchronized void destroyAll()
    {
        checkInit();
        LOGGER.debug("[{}] destroyAll...",getPInit().currentPName());
        Iterator<Context> iterator = portExecutor.contextIterator();
        while (iterator.hasNext())
        {
            Context context = iterator.next();
            context.setEnable(false);
            destroyOne(context);
        }
        portExecutor.clear();
        LOGGER.debug("[{}] destroyAll end!",getPInit().currentPName());
    }

    private void destroyOne(Context context)
    {
        checkInit();
        if (context != null && context.stateListenerForAll != null)
        {
            String contextName = context.getName();
            StateListener stateListenerForAll = context.stateListenerForAll;
            LOGGER.debug("Context [{}] beforeDestroy...", contextName);
            stateListenerForAll.beforeDestroy();
            context.portContext.destroy();
            LOGGER.debug("Context [{}] destroyed!", contextName);
            stateListenerForAll.afterDestroy();
        }
    }

    public synchronized void destroyOne(String contextName)
    {
        checkInit();
        Context context = portExecutor.removeContext(contextName);
        destroyOne(context);
    }

    public synchronized void enableContext(String contextName, boolean enable)
    {
        checkInit();
        portExecutor.enableContext(contextName, enable);
    }

    public void doRequest(PreRequest req, WRequest request, WResponse response)
    {
        portExecutor.doRequest(req, request, response);
    }

    public PreRequest forRequest(WRequest request, WResponse response)
    {
        return portExecutor.forRequest(request, response,pInit);
    }
}
