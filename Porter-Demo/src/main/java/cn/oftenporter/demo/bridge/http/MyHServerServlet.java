package cn.oftenporter.demo.bridge.http;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import cn.oftenporter.bridge.http.server.HServerServlet;
import cn.oftenporter.porter.core.pbridge.Delivery;

@WebServlet(urlPatterns = "/RemoteBridge/*", loadOnStartup = 10,
	initParams = { @WebInitParam(name = "pname", value = "HServer"),
		@WebInitParam(name = "contextName", value = "C"),
		@WebInitParam(name = "urlPatternPrefix", value = "/RemoteBridge") })
public class MyHServerServlet extends HServerServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static Delivery delivery;

    public MyHServerServlet()
    {

    }

    @Override
    public Delivery getBridgeDelivery()
    {
	return delivery;
    }

}
