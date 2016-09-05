package cn.oftenporter.porter.core.init;

import cn.oftenporter.porter.core.base.ParamDealt;
import cn.oftenporter.porter.core.base.UrlDecoder;

/**
 * Created by 宇宙之灵 on 2016/8/31.
 */
public interface PorterBridge
{
     UrlDecoder urlDecoder();

     ParamDealt paramDealt();
}
