package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.base.ParamDealt;
import cn.oftenporter.porter.core.base.UrlDecoder;
import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.PorterBridge;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.init.PorterMain;
import cn.oftenporter.porter.simple.DefaultParamDealt;
import cn.oftenporter.porter.simple.DefaultUrlDecoder;

/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
public class LocalMain implements CommonMain
{
    private PorterMain porterMain;
    private LBridge bridge;

    public LocalMain(String name)
    {
        porterMain = new PorterMain(name);
        bridge = new LBridge()
        {
            @Override
            public void request(LRequest request, LCallback callback)
            {
                porterMain.doRequest(new LocalRequest(request), new LocalResponse(callback), request.getMethod());
            }
        };
    }

    public LBridge getBridge()
    {
        return bridge;
    }

    @Override
    public void start()
    {
        porterMain.start(new PorterBridge()
        {
            @Override
            public UrlDecoder urlDecoder()
            {
                return new DefaultUrlDecoder("", "utf-8");
            }

            @Override
            public ParamDealt paramDealt()
            {
                return new DefaultParamDealt();
            }
        });
    }

    @Override
    public void destroy()
    {
        porterMain.destroy();
    }

    @Override
    public String getName()
    {
        return porterMain.getName();
    }

    @Override
    public PorterConf getPorterConf()
    {
        return porterMain.getPorterConf();
    }
}
