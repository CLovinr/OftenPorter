package cn.oftenporter.bridge.http.server;

import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.Delivery;
import cn.oftenporter.servlet.WMainServlet;

import javax.servlet.ServletException;

/**
 * <pre>
 *     初始参数还有：
 *     contextName
 * </pre>
 *
 * @author Created by https://github.com/CLovinr on 2016/10/7.
 */
public abstract class HServerServlet extends WMainServlet
{
    private String contextName;

    public HServerServlet()
    {
        super();
    }

    public HServerServlet(String urlPatternPrefix,String pname, String contextName, String urlEncoding, boolean responseWhenException)
    {
        super(urlPatternPrefix,pname, urlEncoding, responseWhenException);
        this.contextName = contextName;
    }

    public abstract Delivery getBridgeDelivery();

    @Override
    public final void init() throws ServletException
    {
        super.init();
        if (contextName == null)
        {
            contextName = getInitParameter("contextName");
        }
        PorterConf porterConf = newPorterConf();
        porterConf.setContextName(contextName);
        porterConf.addContextAutoSet("hServerDelivery", getBridgeDelivery());
        porterConf.getSeekPackages().addObjectPorter(new HServerPorter()
        {
        });
        startOne(porterConf);
    }
}
