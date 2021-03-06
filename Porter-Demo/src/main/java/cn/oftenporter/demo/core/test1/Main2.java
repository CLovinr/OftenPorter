package cn.oftenporter.demo.core.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.oftenporter.demo.core.test1.check.GlobalCheckPassable;
import cn.oftenporter.demo.core.test1.porter.Hello5Porter;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.PCallback;
import cn.oftenporter.porter.core.pbridge.PName;
import cn.oftenporter.porter.core.pbridge.PRequest;
import cn.oftenporter.porter.core.pbridge.PResponse;
import cn.oftenporter.porter.local.LocalMain;

public class Main2
{

    public static void main(String[] args)
    {
	/**
	 * 1.CheckPassable:用于认证检测
	 */

	final Logger logger = LoggerFactory.getLogger(Main1.class);

	LocalMain localMain = new LocalMain(true, new PName("P1"), "utf-8");

	// 进行配置
	PorterConf conf = localMain.newPorterConf();
	// 设置名称
	conf.setContextName("Test1-2Main");
	// 添加接口类
	conf.getSeekPackages().addClassPorter(Hello5Porter.class);
	// 设置全局检测
	conf.addContextCheck(new GlobalCheckPassable());

	localMain.startOne(conf);
	logger.debug("****************************************************");

	localMain.getPInit().currentBridge().request(
		new PRequest("/Test1-2Main/Hello5/say"), new PCallback()
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
