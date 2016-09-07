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
    private String pathPrefix;
    private String urlEncoding;

    /**
     * url参数的字符编码为utf-8
     */
    public LocalMain(String name, String pathPrefix)
    {
        this(name, pathPrefix, "utf-8");
    }

    /**
     * @param name        名称
     * @param pathPrefix  路径前缀
     * @param urlEncoding url参数的字符编码
     */
    public LocalMain(String name, String pathPrefix, String urlEncoding)
    {
        this.pathPrefix = pathPrefix;
        this.urlEncoding = urlEncoding;
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
                return new DefaultUrlDecoder(pathPrefix, urlEncoding);
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
