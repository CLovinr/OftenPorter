package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.PreRequest;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.ITypeParser;
import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.init.PorterMain;
import cn.oftenporter.porter.core.pbridge.*;
import cn.oftenporter.porter.simple.DefaultPorterBridge;
import cn.oftenporter.porter.simple.DefaultUrlDecoder;

/**
 * Created by https://github.com/CLovinr on 2016/9/1.
 */
public class LocalMain implements CommonMain
{
    private PorterMain porterMain;


    public LocalMain(boolean responseWhenException, PName pName, String urlEncoding)
    {
        PBridge bridge = new PBridge()
        {
            @Override
            public void request(PRequest request, PCallback callback)
            {
                LocalResponse resp = new LocalResponse(callback);
                PreRequest req = porterMain.forRequest(request, resp);
                if (req != null)
                {
                    porterMain.doRequest(req, request, resp);
                }
            }
        };
        porterMain = new PorterMain(pName, bridge);
        porterMain.init(new DefaultUrlDecoder(urlEncoding), responseWhenException);
    }

    protected LocalMain(boolean responseWhenException, PName pName, String urlEncoding, PBridge bridge)
    {
        porterMain = new PorterMain(pName, bridge);
        porterMain.init(new DefaultUrlDecoder(urlEncoding), responseWhenException);
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
    public PInit getPInit()
    {
        return porterMain.getPInit();
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
