package cn.oftenporter.servlet;

import cn.oftenporter.porter.core.base.ParamDealt;
import cn.oftenporter.porter.core.base.PortMethod;
import cn.oftenporter.porter.core.base.UrlDecoder;
import cn.oftenporter.porter.core.init.CommonMain;
import cn.oftenporter.porter.core.init.PorterBridge;
import cn.oftenporter.porter.core.init.PorterConf;
import cn.oftenporter.porter.core.init.PorterMain;
import cn.oftenporter.porter.simple.DefaultParamDealt;
import cn.oftenporter.porter.simple.DefaultUrlDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 用于servlet。
 * <p>
 * <strong>
 * 注意：必须设置Servlet的名称。</strong>对于注解方式，加上name属性@WebServlet(name="不能为空，否则tomcat等会启动失败。")
 * <p>
 * </p>
 * <pre>
 *     初始参数有：
 *     pathPrefix:路径前缀
 *     urlEncoding:地址参数的字符编码
 *     contentEncoding:内容的字符编码
 * </pre>
 */
public class WMainServlet extends HttpServlet implements CommonMain
{
    private static final long serialVersionUID = 1L;
    private String pathPrefix, urlEncoding, contentEncoding;
    private PorterMain porterMain;

    public WMainServlet(String pathPrefix, String urlEncoding, String contentEncoding)
    {
        this.pathPrefix = pathPrefix;
        this.urlEncoding = urlEncoding;
        this.contentEncoding = contentEncoding;
        porterMain = new PorterMain(getClass().getSimpleName());
    }

    public WMainServlet()
    {
        this("", "utf-8", "utf-8");
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
        request.setCharacterEncoding(contentEncoding);
        response.setCharacterEncoding(contentEncoding);
        porterMain.doRequest(new WServletRequest(request), new WServletResponse(response),
                method);
    }


    @Override
    public void init() throws ServletException
    {
        super.init();
        porterMain.setName(getServletName());
        String pathPrefix = getInitParameter("pathPrefix");
        if (pathPrefix != null)
        {
            this.pathPrefix = pathPrefix;
        }

        String urlEncoding = getInitParameter("urlEncoding");
        if (urlEncoding != null)
        {
            this.urlEncoding = urlEncoding;
        }
        String contentEncoding = getInitParameter("contentEncoding");
        if (contentEncoding != null)
        {
            this.contentEncoding = contentEncoding;
        }
        start();
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
    public String getName()
    {
        return porterMain.getName();
    }

    @Override
    public PorterConf getPorterConf()
    {
        return porterMain.getPorterConf();
    }

    @Override
    public void start()
    {
        porterMain.start(new PorterBridge()
        {
            @Override
            public UrlDecoder urlDecoder()
            {
                return new DefaultUrlDecoder(pathPrefix, urlEncoding);
            }

            @Override
            public ParamDealt paramDealt()
            {
                return new DefaultParamDealt();
            }
        });
    }

    @Override
    public void destroy()
    {
        porterMain.destroy();
        super.destroy();
    }

}
