package cn.oftenporter.demo.core.test4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.oftenporter.demo.core.test4.porter.Hello1Porter;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.local.LCallback;
import cn.oftenporter.porter.local.LRequest;
import cn.oftenporter.porter.local.LResponse;
import cn.oftenporter.porter.local.LocalMain;

public class Main1
{

    public static void main(String[] args)
    {
	/**
	 * 1.Parser:转换数据类型,简单使用。
	 */
	final Logger logger = LoggerFactory.getLogger(Main1.class);

	LocalMain localMain = new LocalMain(true, "", "utf-8");

	// 进行配置
	PorterConf conf = localMain.newPorterConf();
	conf.setContextName("Test4Main");
	conf.getSeekPackages().addObjectPorter(new Hello1Porter());

	localMain.startOne(conf);
	logger.debug("****************************************************");
	localMain.getBridge()
		.request(new LRequest("/Test4Main/Hello1/say")
			.setMethod(PortMethod.POST).addParam("age", "20"),
			new LCallback()
			{

			    @Override
			    public void onResponse(LResponse lResponse)
			    {
				logger.debug(String.valueOf(lResponse.first()));
			    }
			});
	
	localMain.getBridge()
	.request(new LRequest("/Test4Main/Hello1/say2")
		.setMethod(PortMethod.POST).addParam("age", "500"),
		new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.first()));
		    }
		});
	localMain.getBridge()
	.request(new LRequest("/Test4Main/Hello1/say3")
		.setMethod(PortMethod.POST).addParam("age", "300"),
		new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.first()));
		    }
		});

	logger.debug("****************************************************");
	localMain.destroyAll();

    }

}
