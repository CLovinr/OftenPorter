package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.PorterBridge;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.util.WPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class PortExecutor
{
    private PortContext portContext;
    private UrlDecoder urlDecoder;
    private ParamSourceHandleManager paramSourceHandleManager;
    private CheckPassable[] globalChecks;
    private ParamDealt paramDealt;
    private boolean responseWhenException;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortContext.class);

    public PortExecutor(PortContext portContext,
            ParamSourceHandleManager paramSourceHandleManager, PorterBridge bridge, PorterConf porterConf)
    {
        this.portContext = portContext;
        this.paramSourceHandleManager = paramSourceHandleManager;
        this.urlDecoder = bridge.urlDecoder();
        this.paramDealt = bridge.paramDealt();
        this.responseWhenException = porterConf.isResponseWhenException();
        this.globalChecks = porterConf.getGlobalChecks().toArray(new CheckPassable[0]);
    }

    public void doRequest(WRequest request, WResponse response, PortMethod method)
    {
        try
        {
            _doRequest(request, response, method);
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            ex(response, e);
        }
    }


    private void _doRequest(WRequest request, WResponse response,
            PortMethod method) throws InvocationTargetException, IllegalAccessException
    {
        UrlDecoder.Result result = urlDecoder.decode(request.getPath());
        WObject wObject = new WObjectImpl(request, response);
        if (result == null)
        {
            exNotFoundClassPort(request, response);
            return;
        }
        WPort classPort = portContext.getClassPort(result.classTied());

        if (classPort == null)
        {
            exNotFoundClassPort(request, response);
            return;
        }

        //全局通过检测
        Object rs = globalCheck(wObject);
        if (rs != null)
        {
            exCheckPassable(wObject, rs);
            return;
        }

        //类参数初始化
        InNames inNames = classPort.getInNames();
        wObject.cn = PortUtil.newArray(inNames.nece);
        wObject.cu = PortUtil.newArray(inNames.unece);
        wObject.cinner = PortUtil.newArray(inNames.inner);
        wObject.cInNames = inNames;

        ParamSource paramSource = getParamSource(result, method, request);

        TypeParserNameStoreImpl mTypeParserNameStoreImpl = new TypeParserNameStoreImpl(portContext.getTypeParserStore(),
                classPort);

        //类参数处理
        ParamDealt.FailedReason failedReason = paramDeal(inNames, wObject.cn, wObject.cu, paramSource,
                mTypeParserNameStoreImpl);
        if (failedReason != null)
        {
            exParamDeal(wObject, failedReason);
            return;
        }


        //类通过检测
        rs = willPass(classPort, wObject, CheckPassable.Type.CLASS);
        if (rs != null)
        {
            exCheckPassable(wObject, rs);
            return;
        }

        //////////////////////////
        //////////////////////////

        WPort funPort = classPort.getChild(result, method);
        if (funPort == null)
        {
            exNotFoundFun(wObject, result);
            return;
        }
        mTypeParserNameStoreImpl.setMethodStore(funPort);
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

        failedReason = paramDeal(inNames, wObject.fn, wObject.fu, paramSource, mTypeParserNameStoreImpl);
        if (failedReason != null)
        {
            exParamDeal(wObject, failedReason);
            return;
        }

        //函数通过检测
        rs = willPass(funPort, wObject, CheckPassable.Type.METHOD);
        if (rs != null)
        {
            exCheckPassable(wObject, rs);
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

    private Object globalCheck(WObject wObject)
    {
        for (int i = 0; i < globalChecks.length; i++)
        {
            Object rs = globalChecks[i].willPass(wObject, CheckPassable.Type.GLOBAL);
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
    private Object willPass(WPort wport, WObject wObject, CheckPassable.Type type)
    {
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
     * 参数处理
     *
     * @return
     */
    private ParamDealt.FailedReason paramDeal(InNames inNames, Object[] nece, Object[] unnece, ParamSource paramSource,
            TypeParserStore typeParserStore)
    {
        ParamDealt.FailedReason reason = paramDealt.deal(inNames.nece, nece, true, paramSource, typeParserStore);
        if (reason == null)
        {
            reason = paramDealt.deal(inNames.unece, unnece, false, paramSource, typeParserStore);
        }
        return reason;
    }

    /**
     * 整合地址栏查询参数。
     *
     * @return
     */
    private ParamSource getParamSource(final UrlDecoder.Result result, PortMethod method, WRequest request)
    {
        ParamSourceHandle handle = paramSourceHandleManager.fromName(result.classTied());
        if (handle == null)
        {
            handle = paramSourceHandleManager.fromMethod(method);
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

    private void ex(WResponse response, Throwable throwable)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.EXCEPTION);
            jResponse.setDescription(throwable.getMessage());
            response.write(jResponse);
        }
        close(response);
    }

    private void responseObject(WObject wObject, Object object)
    {
        if (object != null)
        {
            wObject.getResponse().write(object);
        }
        close(wObject);
    }

    private void exCheckPassable(WObject wObject, Object obj)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.ACCESS_DENIED);
            jResponse.setDescription(String.valueOf(obj));
            wObject.getResponse().write(jResponse);
        }
        close(wObject);
    }

    private void exParamDeal(WObject wObject, ParamDealt.FailedReason reason)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse();
            jResponse.setCode(ResultCode.PARAM_DEAL_EXCEPTION);
            jResponse.setDescription(reason.desc());
            jResponse.setResult(reason.toJSON());
            wObject.getResponse().write(jResponse);
        }
        close(wObject);
    }

    private void exNotFoundFun(WObject wObject, UrlDecoder.Result result)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.NOT_AVAILABLE);
            jResponse.setDescription("fun:" + result.toString());
            wObject.getResponse().write(jResponse);
        }
        close(wObject);
    }

    private void exNotFoundClassPort(WRequest request, WResponse response)
    {
        if (responseWhenException)
        {
            JResponse jResponse = new JResponse(ResultCode.NOT_AVAILABLE);
            jResponse.setDescription("path:" + request.getPath());
            response.write(jResponse);
        }
        close(response);
    }


}