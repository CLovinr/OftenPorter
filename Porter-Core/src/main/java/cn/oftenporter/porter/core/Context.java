package cn.oftenporter.porter.core;

import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.StateListener;
import cn.oftenporter.porter.core.init.InnerContextBridge;
import cn.oftenporter.porter.core.pbridge.Delivery;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/3.
 */
public class Context
{
    public final PortContext portContext;
    CheckPassable[] contextChecks;
    InnerContextBridge innerContextBridge;
    Delivery delivery;
    ParamSourceHandleManager paramSourceHandleManager;
    public final StateListener stateListenerForAll;

    boolean isEnable = true;
    String name, contentEncoding;

    public Context(Delivery delivery, PortContext portContext, CheckPassable[] contextChecks,
            ParamSourceHandleManager paramSourceHandleManager,
            StateListener stateListenerForAll, InnerContextBridge innerContextBridge)
    {
        this.delivery = delivery;
        this.portContext = portContext;
        this.contextChecks = contextChecks;
        this.paramSourceHandleManager = paramSourceHandleManager;
        this.stateListenerForAll = stateListenerForAll;
        this.innerContextBridge = innerContextBridge;
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