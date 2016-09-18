package cn.oftenporter.demo.core.test3;

import cn.oftenporter.porter.core.ParamSourceHandleManager;
import cn.oftenporter.porter.core.base.StateListener;
import cn.oftenporter.porter.core.init.InitParamSource;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.pbridge.PName;
import cn.oftenporter.porter.local.LocalMain;

public class Main2
{

    public static void main(String[] args)
    {
	/**
	 * <pre>
	 * 1.StateListener:用于监听框架Context的状态。
	 * </pre>
	 */
	LocalMain localMain = new LocalMain(true,new PName("P1"), "utf-8");

	// 进行配置
	PorterConf conf = localMain.newPorterConf();
	conf.setContextName("Test3-2Main");

	conf.addStateListener(new StateListener.Adapter()
	{
	    @Override
	    public void beforeSeek(InitParamSource initParamSource,
		    PorterConf porterConf,
		    ParamSourceHandleManager paramSourceHandleManager)
	    {
		super.beforeSeek(initParamSource, porterConf,
			paramSourceHandleManager);
	    }

	    @Override
	    public void afterStart(InitParamSource initParamSource)
	    {
		super.afterStart(initParamSource);
	    }
	});

	localMain.startOne(conf);

	localMain.destroyAll();

    }

}
