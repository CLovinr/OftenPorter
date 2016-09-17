package cn.oftenporter.servlet;

import cn.oftenporter.porter.core.PortExecutor;
import cn.oftenporter.porter.core.base.*;
import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.init.PorterMain;
import cn.oftenporter.porter.simple.DefaultPorterBridge;
import cn.oftenporter.porter.simple.DefaultUrlDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 用于servlet。
 * </p>
 * <pre>
 *     初始参数有：
 *     pathPrefix:路径前缀,默认为""
 *     urlEncoding:地址参数的字符编码,默认为utf-8
 *     responseWhenException:默认为true。
 * </pre>
 */
public class WMainServlet extends HttpServlet implements CommonMain
{
    private static final long serialVersionUID = 1L;
    private PorterMain porterMain;

    public WMainServlet()
    {
        porterMain = new PorterMain();
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doRequest(req, resp, PortMethod.TARCE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doRequest(req, resp, PortMethod.PUT);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doRequest(req, resp, PortMethod.HEAD);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doRequest(req, resp, PortMethod.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        doRequest(request, response, PortMethod.OPTIONS);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doRequest(request, response, PortMethod.POST);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doRequest(request, response, PortMethod.GET);
    }

    /**
     * 处理请求
     *
     * @param request
     * @param response
     * @param method
     * @throws IOException
     */
    private void doRequest(HttpServletRequest request, HttpServletResponse response,
            PortMethod method) throws IOException
    {

        WRequest wreq = new WServletRequest(request, method);
        WResponse wresp = new WServletResponse(response);

        PortExecutor.Request req = porterMain.forRequest(wreq, wresp);
        if (req != null)
        {
            request.setCharacterEncoding(req.context.getContentEncoding());
            response.setCharacterEncoding(req.context.getContentEncoding());
            porterMain.doRequest(req, wreq, wresp);
        }
    }


    @Override
    public void init() throws ServletException
    {
        super.init();

        String pathPrefix = getInitParameter("pathPrefix");
        if (pathPrefix == null)
        {
            pathPrefix = "";
        }

        String urlEncoding = getInitParameter("urlEncoding");
        if (urlEncoding == null)
        {
            urlEncoding = "utf-8";
        }

        boolean responseWhenException = !"false".equals(getInitParameter("responseWhenException"));
        porterMain.init(new DefaultUrlDecoder(pathPrefix, urlEncoding), responseWhenException);

    }

    /**
     * 获取WEB-INF目录的路径
     *
     * @return
     */
    public static String getWebInfDir()
    {
        // file:/D:/JavaWeb/.metadata/.me_tcat/webapps/TestBeanUtils/WEB-INF/classes/
        String path = Thread.currentThread().getContextClassLoader().getResource("").getFile();// .toString();
        path = path.replace('/', File.separatorChar);
        path = path.replace("file:", ""); // 去掉file:
        path = path.replace("classes" + File.separator, ""); // 去掉class\
        // if (path.startsWith(File.separator) && path.indexOf(':') != -1)
        // {
        // return path.substring(1);
        // }
        // else
        // {
        // return path;
        // }
        return path;

    }

    /**
     * 获取Context所在的路径,以File.separatorChar结尾
     *
     * @return
     */
    public static String getContextDir()
    {
        // WEB-INF/
        String path = WMainServlet.getWebInfDir();
        path = path.substring(0, path.length() - 8);
        return path;
    }

    @Override
    public void addGlobalAutoSet(String name, Object object)
    {
        porterMain.addGlobalAutoSet(name, object);
    }

    @Override
    public void addGlobalTypeParser(ITypeParser typeParser)
    {
        porterMain.addGlobalTypeParser(typeParser);
    }

    @Override
    public void addGlobalCheck(CheckPassable checkPassable) throws RuntimeException
    {
        porterMain.addGlobalCheck(checkPassable);
    }

    @Override
    public PorterConf newPorterConf()
    {
        return porterMain.newPorterConf();
    }

    @Override
    public void startOne(PorterConf porterConf)
    {
        porterMain.startOne(DefaultPorterBridge.defaultBridge(porterConf));
    }

    @Override
    public void destroyOne(String contextName)
    {
        porterMain.destroyOne(contextName);
    }

    @Override
    public void enableOne(String contextName, boolean enable)
    {
        porterMain.enableContext(contextName, enable);
    }

    @Override
    public void destroyAll()
    {
        porterMain.destroyAll();
    }

    @Override
    public void destroy()
    {
        destroyAll();
        super.destroy();
    }

}
