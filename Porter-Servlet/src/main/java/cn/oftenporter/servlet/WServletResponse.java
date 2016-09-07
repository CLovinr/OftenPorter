package cn.oftenporter.servlet;


import cn.oftenporter.porter.core.JResponse;
import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.WResponse;
import cn.oftenporter.porter.core.util.WPTool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WServletResponse implements WResponse
{

    private static final Logger LOGGER = LoggerFactory.getLogger(WServletResponse.class);
    private HttpServletResponse response;

    WServletResponse(HttpServletResponse response)
    {
        this.response = response;
    }


    public HttpServletResponse getServletResponse()
    {
        return response;
    }


    public void sendRedirect(String redirect) throws IOException
    {
        response.sendRedirect(redirect);
    }

    @Override
    public void write(@NotNull Object object)
    {
        try
        {
            setContentType(object);
            response.getWriter().print(object);
        } catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException
    {
        response.getWriter().close();
    }

    public void setContentType(Object object)
    {
        String ctype = response.getContentType();
        if (WPTool.isEmpty(ctype))
        {

            if (object != null && ((object instanceof JResponse) || (object instanceof JSONObject) ||
                    (object instanceof JSONArray) || (object instanceof JSONHeader)))
            {

                response.setContentType(ContentType.APP_JSON.getType());
            } else
            {
                response.setContentType(ContentType.TEXT_PLAIN.getType());
            }

        }
    }
}
