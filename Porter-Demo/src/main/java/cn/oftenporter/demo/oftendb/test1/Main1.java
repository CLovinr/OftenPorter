package cn.oftenporter.demo.oftendb.test1;

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
	LocalMain localMain = new LocalMain(true, "", "utf-8");
	PorterConf porterConf = localMain.newPorterConf();
	porterConf.setContextName("T1");
	porterConf.getSeekPackages()
		.addPorters(Main1.class.getPackage().getName() + ".porter");

	localMain.startOne(porterConf);
	final Logger logger = LoggerFactory.getLogger(Main1.class);

	localMain.getBridge()
		.request(new LRequest(PortMethod.POST, "/T1/Hello1/add")
			.addParam("name", "小明").addParam("age", "21")
			.addParam("sex", "男"), new LCallback()
			{

			    @Override
			    public void onResponse(LResponse lResponse)
			    {
				logger.debug(lResponse.toString());
			    }
			});
	localMain.getBridge()
		.request(new LRequest(PortMethod.GET, "/T1/Hello1/count")
			.addParam("name", "小明"), new LCallback()
			{

			    @Override
			    public void onResponse(LResponse lResponse)
			    {
				logger.debug(lResponse.toString());
			    }
			});
	localMain.getBridge()
		.request(new LRequest(PortMethod.GET, "/T1/Hello1/update")
			.addParam("name", "小明"), new LCallback()
			{

			    @Override
			    public void onResponse(LResponse lResponse)
			    {
				logger.debug(lResponse.toString());
			    }
			});
	localMain.getBridge()
		.request(new LRequest(PortMethod.GET, "/T1/Hello1/del")
			.addParam("name", "小明1"), new LCallback()
			{

			    @Override
			    public void onResponse(LResponse lResponse)
			    {
				logger.debug(lResponse.toString());
			    }
			});
	localMain.getBridge().request(
		new LRequest(PortMethod.GET, "/T1/Hello1/list"), new LCallback()
		{

		    @Override
		    public void onResponse(LResponse lResponse)
		    {
			logger.debug(lResponse.toString());
		    }
		});
	localMain.destroyAll();

    }

}
