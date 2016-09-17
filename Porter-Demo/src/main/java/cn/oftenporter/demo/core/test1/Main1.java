package cn.oftenporter.demo.core.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * 1.PortIn：用于标记web接口 2.设置生成REST
	 * 
	 */

	final Logger logger = LoggerFactory.getLogger(Main1.class);

	LocalMain localMain = new LocalMain(true,"","utf-8");

	// 进行配置
	PorterConf conf = localMain.newPorterConf();
	// 设置名称
	conf.setContextName("Test1Main");
	// 添加扫描的包（包含子包）
	conf.getSeekPackages()
		.addPorters(Main1.class.getPackage().getName() + ".porter");

	/**
	 * 使用当前配置启动一个context
	 */
	localMain.startOne(conf);
	logger.debug("****************************************************");

	localMain.getBridge().request(
		new LRequest("/Test1Main/Hello1/say").addParam("name", "The Earth").addParam("sth", "class-param"),
		new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.first()));
		    }
		});

	localMain.getBridge()
		.request(new LRequest("/Test1Main/Hello2/say").setMethod(PortMethod.POST)
			.addParam("name", "The Moon").addParam("msg", "beauty"),
			new LCallback()
			{

			    @Override
			    public void onResponse(LResponse lResponse)
			    {
				logger.debug(String.valueOf(lResponse.first()));
			    }
			});

	localMain.getBridge().request(
		new LRequest("/Test1Main/Hello3REST/123456").setMethod(PortMethod.POST)
			.addParam("name", "The Sun").addParam("msg", "beauty"),
		new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.first()));
		    }
		});

	localMain.getBridge().request(new LRequest("/Test1Main/Hello4REST/abcdef")
		.setMethod(PortMethod.POST).addParam("name", "The Mars"),
		new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(String.valueOf(lResponse.first()));
		    }
		});
	localMain.getBridge().request(
		new LRequest("/Test1Main/Hello4REST/add").addParam("content", "!!!!").setMethod(PortMethod.POST),
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
