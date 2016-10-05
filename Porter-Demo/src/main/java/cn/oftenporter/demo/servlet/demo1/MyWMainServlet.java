package cn.oftenporter.demo.servlet.demo1;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.oftenporter.porter.core.base.CheckPassable;
import cn.oftenporter.porter.core.base.DuringType;
import cn.oftenporter.porter.core.base.WObject;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.PCallback;
import cn.oftenporter.porter.core.pbridge.PInit;
import cn.oftenporter.porter.core.pbridge.PInit.Direction;
import cn.oftenporter.porter.core.util.LogUtil;
import cn.oftenporter.porter.core.pbridge.PName;
import cn.oftenporter.porter.core.pbridge.PRequest;
import cn.oftenporter.porter.core.pbridge.PResponse;
import cn.oftenporter.porter.local.LocalMain;
import cn.oftenporter.servlet.WMainServlet;

/**
 * 
 * @author https://github.com/CLovinr <br>
 *         2016年9月6日 下午11:55:04
 *
 */
@WebServlet(name = "PorterServlet", urlPatterns = "/*",
	loadOnStartup = 10,
	initParams = { @WebInitParam(name = "pname", value = "Servlet1")
	// @WebInitParam(name = "urlEncoding", value = "utf-8")
	})
public class MyWMainServlet extends WMainServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER =
	    LoggerFactory.getLogger(MyWMainServlet.class);

    public MyWMainServlet()
    {
    }

    @Override
    public void init() throws ServletException
    {
	super.init();
	PropertyConfigurator
		.configure(getClass().getResource("/log4j.properties"));
	PorterConf porterConf = newPorterConf();

	porterConf.addContextCheck(new CheckPassable()
	{

	    @Override
	    public Object willPass(WObject wObject, DuringType type)
	    {
		LOGGER.debug("");
		return null;
	    }

	});

	porterConf.getSeekPackages()
		.addPorters(getClass().getPackage().getName()+".porter");
	porterConf.setContextName("T1");

	startOne(porterConf);

	PInit servletInit = getPInit();

	LocalMain localMain = new LocalMain(true, new PName("local"), "utf-8");
	PorterConf porterConf2 = localMain.newPorterConf();
	porterConf2.setContextName("T2");
	porterConf2.getSeekPackages()
		.addPorters(getClass().getPackage().getName() + ".lporter");
	localMain.startOne(porterConf2);

	localMain.getPInit().link(servletInit, Direction.BothAll);

	PRequest request = new PRequest(":Servlet1/T1/Hello/say")
		.addParam("name", "xiaoming").addParam("age", 15)
		.addParam("sex", "男");

	servletInit.currentBridge().request(request, new PCallback()
	{

	    @Override
	    public void onResponse(PResponse lResponse)
	    {
		LogUtil.printPosLn(lResponse.getResponse());

	    }
	});

	servletInit.toAllBridge().request(request, new PCallback()
	{

	    @Override
	    public void onResponse(PResponse lResponse)
	    {
		LogUtil.printPosLn(lResponse.getResponse());

	    }
	});

	localMain.getPInit().toAllBridge().request(request, new PCallback()
	{

	    @Override
	    public void onResponse(PResponse lResponse)
	    {
		LogUtil.printPosLn(lResponse.getResponse());

	    }
	});
    }
}
