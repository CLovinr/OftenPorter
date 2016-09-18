package cn.oftenporter.demo.core.test2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * 1.PortOut:用于标记输出类型
	 */
	final Logger logger = LoggerFactory.getLogger(Main1.class);

	LocalMain localMain = new LocalMain(true, "", "utf-8");

	// 进行配置
	PorterConf conf = localMain.newPorterConf();
	conf.setContextName("Test2Main");
	conf.getSeekPackages()
		.addPorters(Main1.class.getPackage().getName() + ".porter");

	localMain.startOne(conf);
	logger.debug("****************************************************");
	localMain.getBridge().request(
		new LRequest("/Test2Main/Hello1/say").addParam("name", "火星人"),
		new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.first()));
		    }
		});

	localMain.getBridge().request(
		new LRequest("/Test2Main/Hello1/say2").addParam("name", "火星人"),
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