package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.PorterBridge;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.Delivery;
import cn.oftenporter.porter.core.util.WPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PortExecutor
{

    public static class Context
    {
        public final PortContext portContext;
        private CheckPassable[] contextChecks;
        private ParamDealt paramDealt;
        private boolean responseWhenException;
        Map<String, Object> globalAutoSetMap;
        Map<String, Object> contextRuntimeMap;
        Delivery delivery;
        private ParamSourceHandleManager paramSourceHandleManager;
        public final StateListener stateListenerForAll;

        private boolean isEnable = true;
        private String name, contentEncoding;

        public Context(Delivery delivery, PortContext portContext, CheckPassable[] contextChecks,
                ParamDealt paramDealt, boolean responseWhenException, Map<String, Object> globalAutoSetMap,
                Map<String, Object> contextRuntimeMap, ParamSourceHandleManager paramSourceHandleManager,
                StateListener stateListenerForAll)
        {
            this.delivery = delivery;
            this.portContext = portContext;
            this.contextChecks = contextChecks;
            this.paramDealt = paramDealt;
            this.responseWhenException = responseWhenException;
            this.globalAutoSetMap = globalAutoSetMap;
            this.contextRuntimeMap = contextRuntimeMap;
            this.paramSourceHandleManager = paramSourceHandleManager;
            this.stateListenerForAll = stateListenerForAll;
            setEnable(true);
        }


        public String getContentEncoding()
        {
            return contentEncoding;
        }

        public String getName()
        {
            return name;
        }

        public void setEnable(boolean enable)
        {
            isEnable = enable;
        }

        public boolean isEnable()
        {
            return isEnable;
        }
    }

    public static class Request
    {
        public Context context;
        public UrlDecoder.Result result;

        public Request(Context context, UrlDecoder.Result result)
        {
            this.context = context;
            this.result = result;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PortExecutor.class);
    private Map<String, Context> contextMap = new ConcurrentHashMap<>();
    private Map<String, Object> globalAutoSetMap;
    private TypeParserStore globalParserStore;
    private CheckPassable[] allGlobalChecks;
    private UrlDecoder urlDecoder;
    private Delivery delivery;
    private boolean responseWhenException;

    public PortExecutor(Delivery delivery, Map<String, Object> globalAutoSetMap,
            TypeParserStore globalParserStore, UrlDecoder urlDecoder,
            boolean responseWhenException)
    {
        this.delivery = delivery;
        this.globalAutoSetMap = globalAutoSetMap;
        this.globalParserStore = globalParserStore;
        this.urlDecoder = urlDecoder;
        this.responseWhenException = responseWhenException;
    }

    public void initAllGlobalChecks(CheckPassable[] allGlobalChecks)
    {
        this.allGlobalChecks = allGlobalChecks;
    }

    public void addContext(PorterBridge bridge, PortContext portContext, StateListener stateListenerForAll)
    {
        PorterConf porterConf = bridge.porterConf();
        Context context = new Context(delivery, portContext,
                porterConf.getContextChecks().toArray(new CheckPassable[0]),
                bridge.paramDealt(), porterConf.isResponseWhenException(), globalAutoSetMap,
                porterConf.getContextRuntimeMap(),
                bridge.paramSourceHandleManager(), stateListenerForAll);
        context.name = bridge.contextName();
        context.contentEncoding = porterConf.getContentEncoding();
        contextMap.put(bridge.contextName(), context);
    }

    /**
     * 移除指定的context
     *
     * @param contextName context名称
     * @return 返回移除的Context，可能够为null。
     */
    public Context removeContext(String contextName)
    {
        return contextMap.remove(contextName);
    }

    /**
     * 是否包含指定的context
     *
     * @param contextName context名称
     * @return 存在返回true，不存在返回false。
     */
    public boolean containsContext(String contextName)
    {
        return contextMap.containsKey(contextName);
    }

    /**
     * 启用或禁用指定context
     *
     * @param contextName context名称
     * @param enable      是否启用
     * @return 返回对应Context，可能为null。
     */
    public Context enableContext(String contextName, boolean enable)
    {
        Context context = contextMap.get(contextName);
        if (context != null)
        {
            context.setEnable(enable);
        }
        return context;
    }

    public void doRequest(Request req, WRequest request, WResponse response)
    {
        try
        {
            _doRequest(req, request, response);
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            ex(response, e, responseWhenException);
        }
    }

    public void clear()
    {
        contextMap.clear();
    }

    public Iterator<Context> contextIterator()
    {
        return contextMap.values().iterator();
    }


    public Request forRequest(WRequest request, WResponse response)
    {
        UrlDecoder.Result result = urlDecoder.decode(request.getPath());
        Context context;
        if (result == null || (context = contextMap.get(result.contextName())) == null || !context.isEnable)
        {
            exNotFoundClassPort(request, response, responseWhenException);
            return null;
        } else
        {
            return new Request(context, result);
        }
    }

    private void _doRequest(Request req, WRequest request,
            WResponse response) throws InvocationTargetException, IllegalAccessException
    {
        Context context = req.context;
        UrlDecoder.Result result = req.result;

        WObjectImpl wObject = new WObjectImpl(result, request, response, context);

        WPort classPort = context.portContext.getClassPort(result.classTied());

        WPort funPort;

        if (classPort == null || (funPort = classPort.getChild(result, request.getMethod())) == null)
        {
            exNotFoundClassPort(request, response, context.responseWhenException);
            return;
        }

        //全局通过检测
        Object rs = globalCheck(context, wObject);
        if (rs != null)
        {
            exCheckPassable(wObject, rs, context.responseWhenException);
            return;
        }

        //类参数初始化
        InNames inNames = classPort.getInNames();
        wObject.cn = PortUtil.newArray(inNames.nece);
        wObject.cu = PortUtil.newArray(inNames.unece);
        wObject.cinner = PortUtil.newArray(inNames.inner);
        wObject.cInNames = inNames;

        ParamSource paramSource = getParamSource(context, result, request);

        TypeParserStore typeParserStore = globalParserStore;


        //类参数处理
        ParamDealt.FailedReason failedReason = PortUtil
                .paramDeal(context.paramDealt, inNames, wObject.cn, wObject.cu, paramSource,
                        typeParserStore);
        if (failedReason != null)
        {
            exParamDeal(wObject, failedReason, context.responseWhenException);
            return;
        }


        ///////////////////////////
        //转换成类或接口对象
        failedReason = paramDealOfPortInObj(context, classPort, true, wObject, paramSource, typeParserStore);
        if (failedReason != null)
        {
            exParamDeal(wObject, failedReason, context.responseWhenException);
            return;
        }
        //////////////////////////////


        //类通过检测
        rs = willPass(context, classPort, wObject, CheckPassable.DuringType.CLASS);
        if (rs != null)
        {
            exCheckPassable(wObject, rs, context.responseWhenException);
            return;
        }

        //////////////////////////
        //////////////////////////


        if (funPort == null)
        {
            exNotFoundFun(wObject, result, context.responseWhenException);
            return;
        }
        if (funPort.getTiedType() == TiedType.REST)
        {
            wObject.restValue = result.funTied();
        }

        //函数参数初始化
        inNames = funPort.getInNames();
        wObject.fn = PortUtil.newArray(inNames.nece);
        wObject.fu = PortUtil.newArray(inNames.unece);
        wObject.finner = PortUtil.newArray(inNames.inner);
        wObject.fInNames = inNames;

        //函数参数处理

        failedReason = PortUtil
                .paramDeal(context.paramDealt, inNames, wObject.fn, wObject.fu, paramSource, typeParserStore);
        if (failedReason != null)
        {
            exParamDeal(wObject, failedReason, context.responseWhenException);
            return;
        }
        ///////////////////////////
        //转换成类或接口对象
        failedReason = paramDealOfPortInObj(context, funPort, false, wObject, paramSource, typeParserStore);
        if (failedReason != null)
        {
            exParamDeal(wObject, failedReason, context.responseWhenException);
            return;
        }
        //////////////////////////////


        //函数通过检测
        rs = willPass(context, funPort, wObject, CheckPassable.DuringType.METHOD);
        if (rs != null)
        {
            exCheckPassable(wObject, rs, context.responseWhenException);
            return;
        }

        Method javaMethod = (Method) funPort.getPortObject();

        if (funPort.getArgCount() == 0)
        {
            rs = javaMethod.invoke(classPort.getPortObject());
        } else
        {
            rs = javaMethod.invoke(classPort.getPortObject(), wObject);
        }
        switch (funPort.getOutType())
        {
            case NoResponse:
                break;
            case Object:
                responseObject(wObject, rs);
                break;
        }

    }

    /**
     * 用于处理对象绑定。
     *
     * @param wPort
     * @param isInClass
     * @param wObjectImpl
     * @param paramSource
     * @param currentTypeParserStore
     * @return
     */
    private ParamDealt.FailedReason paramDealOfPortInObj(Context context, WPort wPort, boolean isInClass,
            WObjectImpl wObjectImpl,
            ParamSource paramSource, TypeParserStore currentTypeParserStore)
    {
        ParamDealt.FailedReason reason = null;
        WPortInObj inObj = wPort.getWPortInObj();
        if (inObj == null)
        {
            return null;
        }
        WPortInObj.One[] ones = inObj.ones;
        Object[] inObjects = new Object[ones.length];
        if (isInClass)
        {
            wObjectImpl.cinObjs = inObjects;
        } else
        {
            wObjectImpl.finObjs = inObjects;
        }
        for (int i = 0; i < ones.length; i++)
        {
            WPortInObj.One one = ones[i];
            Object object = PortUtil.paramDealOne(context.paramDealt, one, paramSource, currentTypeParserStore);
            if (object instanceof ParamDealt.FailedReason)
            {
                return (ParamDealt.FailedReason) object;
            } else
            {
                inObjects[i] = object;
            }
        }

        return reason;
    }

    private Object globalCheck(Context context, WObject wObject)
    {
        CheckPassable[] allGlobal = this.allGlobalChecks;

        for (int i = 0; i < allGlobal.length; i++)
        {
            Object rs = allGlobal[i].willPass(wObject, CheckPassable.DuringType.GLOBAL);
            if (rs != null)
            {
                return rs;
            }
        }

        CheckPassable[] contextChecks = context.contextChecks;
        for (int i = 0; i < contextChecks.length; i++)
        {
            Object rs = contextChecks[i].willPass(wObject, CheckPassable.DuringType.GLOBAL);
            if (rs != null)
            {
                return rs;
            }
        }
        return null;
    }

    /**
     * 通过检测
     */
    private Object willPass(Context context, WPort wport, WObject wObject, CheckPassable.DuringType type)
    {
        PortContext portContext = context.portContext;
        Class<? extends CheckPassable>[] cps = wport.getChecks();
        for (int i = 0; i < cps.length; i++)
        {
            CheckPassable cp = portContext.getCheckPassable(cps[i]);
            Object rs = cp.willPass(wObject, type);
            if (rs != null)
            {
                return rs;
            }
        }
        return null;
    }


    /**
     * 整合地址栏查询参数。
     *
     * @return
     */
    private ParamSource getParamSource(Context context, final UrlDecoder.Result result, WRequest request)
    {
        ParamSourceHandle handle = context.paramSourceHandleManager.fromName(result.classTied());
        if (handle == null)
        {
            handle = context.paramSourceHandleManager.fromMethod(request.getMethod());
        }
        ParamSource ps;
        if (handle == null)
        {
            ps = new ParamsSourceDefault(result, request);
        } else
        {
            ps = handle.get(request, result);
        }
        return ps;
    }

////////////////////////////////////////////////
    //////////////////////////////////////////

    private void close(WObject wObject)
    {
        WPTool.close(wObject.getResponse());
    }

    private void close(WResponse response)
    {
        WPTool.close(response);
    }

    private void ex(WResponse response, Throwable throwable, boolean responseWhenException)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.EXCEPTION);
            jResponse.setDescription(WPTool.getMessage(throwable));
            try
            {
                response.write(jResponse);
            } catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
        close(response);
    }

    private void responseObject(WObject wObject, Object object)
    {
        if (object != null)
        {
            try
            {
                wObject.getResponse().write(object);
            } catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
        close(wObject);
    }

    private void exCheckPassable(WObject wObject, Object obj, boolean responseWhenException)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.ACCESS_DENIED);
            jResponse.setDescription(String.valueOf(obj));
            try
            {
                wObject.getResponse().write(jResponse);
            } catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
        close(wObject);
    }

    private void exParamDeal(WObject wObject, ParamDealt.FailedReason reason, boolean responseWhenException)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse();
            jResponse.setCode(ResultCode.PARAM_DEAL_EXCEPTION);
            jResponse.setDescription(reason.desc());
            jResponse.setResult(reason.toJSON());
            try
            {
                wObject.getResponse().write(jResponse);
            } catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
        close(wObject);
    }

    private void exNotFoundFun(WObject wObject, UrlDecoder.Result result, boolean responseWhenException)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.NOT_AVAILABLE);
            jResponse.setDescription("fun:" + result.toString());
            try
            {
                wObject.getResponse().write(jResponse);
            } catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
        close(wObject);
    }

    private void exNotFoundClassPort(WRequest request, WResponse response, boolean responseWhenException)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.NOT_AVAILABLE);
            jResponse.setDescription("method:" + request.getMethod() + ",path:" + request.getPath());
            try
            {
                response.write(jResponse);
            } catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
        close(response);
    }


}