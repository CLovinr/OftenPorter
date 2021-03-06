package cn.oftenporter.bridge.http;

import cn.oftenporter.porter.core.JResponse;
import cn.oftenporter.porter.core.ResultCode;
import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.ITypeParser;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.*;
import cn.oftenporter.porter.local.LocalMain;
import com.squareup.okhttp.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/7.
 */
public class HMain extends LocalMain
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HMain.class);

    public HMain(boolean responseWhenException, PName pName,
            final String urlEncoding, final OkHttpClient okHttpClient, final String hostUrlPrefix)
    {
        super();
        newLocalMain(responseWhenException, pName, urlEncoding, new PBridge()
        {
            @Override
            public void request(PRequest request, final PCallback callback)
            {
                try
                {
                    String path = request.getPath();
                    if (path.startsWith("/="))
                    {
                        path = ":" + path.substring(2);
                    }
                    HttpUtil.requestWPorter(request.getParameterMap(), HttpMethod.valueOf(request.getMethod().name()),
                            okHttpClient, hostUrlPrefix + path,
                            new JRCallback()
                            {
                                @Override
                                public void onResult(JResponse jResponse)
                                {
                                    if (callback != null)
                                    {
                                        callback.onResponse(new PResponseImpl(jResponse));
                                    }
                                }
                            });
                } catch (Exception e)
                {
                    LOGGER.error(e.getMessage(), e);
                    if (callback != null)
                    {
                        callback.onResponse(PResponseImpl.exception(ResultCode.EXCEPTION, e));
                    }
                }
            }
        });
    }

    @Override
    public void addGlobalAutoSet(String name, Object object)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGlobalTypeParser(ITypeParser typeParser)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGlobalCheck(CheckPassable checkPassable) throws RuntimeException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startOne(PorterConf porterConf)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PInit getPInit()
    {
        return super.getPInit();
    }

    @Override
    public void destroyOne(String contextName)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enableOne(String contextName, boolean enable)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroyAll()
    {

    }

    @Override
    public PorterConf newPorterConf()
    {
        throw new UnsupportedOperationException();
    }
}
