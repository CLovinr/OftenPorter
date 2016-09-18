package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.base.ParamDealt;

/**
 */
public interface PorterBridge
{
    String contextName();

    ParamDealt paramDealt();

    PorterConf porterConf();

    ParamSourceHandleManager paramSourceHandleManager();
}
