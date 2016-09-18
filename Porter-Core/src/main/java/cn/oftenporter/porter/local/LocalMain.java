package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.PortExecutor;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.ITypeParser;
import cn.oftenporter.porter.core.base.WRequest;
import cn.oftenporter.porter.core.base.WResponse;
import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.init.PorterMain;
import cn.oftenporter.porter.simple.DefaultPorterBridge;
import cn.oftenporter.porter.simple.DefaultUrlDecoder;

/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
public class LocalMain implements CommonMain
{
    private PorterMain porterMain;
    private LBridge bridge;


    public LocalMain(boolean responseWhenException, String pathPrefix, String urlEncoding)
    {
        porterMain = new PorterMain();
        bridge = new LBridge()
        {
            @Override
            public void request(LRequest request, LCallback callback)
            {
                WRequest wreq = new LocalRequest(request);
                WResponse wresp = new LocalResponse(callback);
                PortExecutor.Request req = porterMain.forRequest(wreq, wresp);
                if (req != null)
                {
                    porterMain.doRequest(req, wreq, wresp);
                }
            }
        };
        porterMain.init(new DefaultUrlDecoder(pathPrefix, urlEncoding), responseWhenException);
    }

    public LBridge getBridge()
    {
        return bridge;
    }

    @Override
    public void addGlobalAutoSet(String name, Object object)
    {
        porterMain.addGlobalAutoSet(name, object);
    }

    @Override
    public void addGlobalTypeParser(ITypeParser typeParser)
    {
        porterMain.addGlobalTypeParser(typeParser);
    }

    @Override
    public void addGlobalCheck(CheckPassable checkPassable) throws RuntimeException
    {
        porterMain.addGlobalCheck(checkPassable);
    }

    @Override
    public void startOne(PorterConf porterConf)
    {
        porterMain.startOne(DefaultPorterBridge.defaultBridge(porterConf));
    }

    @Override
    public void destroyOne(String contextName)
    {
        porterMain.destroyOne(contextName);
    }

    @Override
    public void enableOne(String contextName, boolean enable)
    {
        porterMain.enableContext(contextName, enable);
    }

    @Override
    public void destroyAll()
    {
        porterMain.destroyAll();
    }

    @Override
    public PorterConf newPorterConf()
    {
        return porterMain.newPorterConf();
    }
}
