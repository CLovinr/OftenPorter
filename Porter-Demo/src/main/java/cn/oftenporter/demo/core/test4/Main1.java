package cn.oftenporter.demo.core.test4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.oftenporter.demo.core.test4.porter.Hello1Porter;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.PBridge;
import cn.oftenporter.porter.core.pbridge.PCallback;
import cn.oftenporter.porter.core.pbridge.PName;
import cn.oftenporter.porter.core.pbridge.PRequest;
import cn.oftenporter.porter.core.pbridge.PResponse;
import cn.oftenporter.porter.local.LocalMain;

public class Main1
{

    public static void main(String[] args)
    {
	/**
	 * 1.Parser:转换数据类型,简单使用。
	 */
	final Logger logger = LoggerFactory.getLogger(Main1.class);

	LocalMain localMain = new LocalMain(true, new PName("P1"), "utf-8");

	// 进行配置
	PorterConf conf = localMain.newPorterConf();
	conf.setContextName("Test4Main");
	conf.getSeekPackages().addObjectPorter(new Hello1Porter());

	PBridge bridge = localMain.getPInit().currentBridge();
	localMain.startOne(conf);
	logger.debug("****************************************************");
	bridge.request(new PRequest("/Test4Main/Hello1/say")
		.setMethod(PortMethod.POST).addParam("age", "20"),
		new PCallback()
		{

		    @Override
		    public void onResponse(PResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.getResponse()));
		    }
		});

	bridge.request(
		new PRequest("/Test4Main/Hello1/say2")
			.setMethod(PortMethod.POST).addParam("age", "500"),
		new PCallback()
		{

		    @Override
		    public void onResponse(PResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.getResponse()));
		    }
		});
	bridge.request(
		new PRequest("/Test4Main/Hello1/say3")
			.setMethod(PortMethod.POST).addParam("age", "300"),
		new PCallback()
		{

		    @Override
		    public void onResponse(PResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.getResponse()));
		    }
		});

	logger.debug("****************************************************");
	localMain.destroyAll();

    }

}
