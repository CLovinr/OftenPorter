package cn.oftenporter.bridge.http;

import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.pbridge.*;
import cn.oftenporter.porter.core.util.LogUtil;
import cn.oftenporter.porter.local.LocalMain;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;


/**
 * HMain Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>10-7, 2016</pre>
 */
public class HMainTest
{

    @Before
    public void before() throws Exception
    {
    }

    @After
    public void after() throws Exception
    {
    }

    @Test
    public void test()
    {
        HMain hMain = new HMain(true, new PName("HMain"), "utf-8", HttpUtil.getClient(null),
                "http://127.0.0.1:8080/Porter-Demo/RemoteBridge/C/HServer/");


        hMain.getPInit().currentBridge()
                .request(new PRequest(PortMethod.GET, ":Servlet1/T1/Remote/hello"), new PCallback()
                {
                    @Override
                    public void onResponse(PResponse lResponse)
                    {
                        LogUtil.printErrPosLn(lResponse.getResponse());
                    }
                });
        LocalMain localMain = new LocalMain(true, new PName("Local"), "utf-8");
        localMain.getPInit().link(hMain.getPInit(), PInit.Direction.ToItAll);
        localMain.getPInit().toAllBridge().request(new PRequest(PortMethod.GET, ":HMain/=Servlet1/T1/Remote/hello"), new
                PCallback()
                {
                    @Override
                    public void onResponse(PResponse lResponse)
                    {
                        LogUtil.printPosLn(lResponse.getResponse());
                    }
                });
        try
        {
            Thread.sleep(9000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

} 
