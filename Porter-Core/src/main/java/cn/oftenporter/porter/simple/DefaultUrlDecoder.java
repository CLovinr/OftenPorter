package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.UrlDecoder;
import cn.oftenporter.porter.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的地址解析器。
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
public class DefaultUrlDecoder implements UrlDecoder
{
    private String pathPrefix;
    private String encoding;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUrlDecoder.class);
//    private static final String URL_CHARS = "a-zA-Z-0-9%_\\.\\*\\+\\$&=#-";
//    private static final Pattern PATTERN = Pattern.compile("^/([" + URL_CHARS + "]+)/([" + URL_CHARS + "]*)$");

    public DefaultUrlDecoder(String pathPrefix, String encoding)
    {
        this.pathPrefix = pathPrefix;
        this.encoding = encoding;
    }

    @Override
    public Result decode(String path)
    {
        if (!path.startsWith(pathPrefix))
        {
            return null;
        }
        path = path.substring(pathPrefix.length());
        int index = path.indexOf('?');

        String tiedPath = index == -1 ? path : path.substring(0, index);

        if (!tiedPath.startsWith("/"))
        {
            return null;
        }
        int forwardSlash = tiedPath.indexOf('/', 1);
        String contextName, classTied, funTied;

        if (forwardSlash == -1)
        {
            return null;
        } else
        {
            contextName = tiedPath.substring(1, forwardSlash);
        }
        tiedPath = tiedPath.substring(forwardSlash);

        forwardSlash = tiedPath.indexOf('/', 1);
        if (forwardSlash == -1)
        {
            classTied = tiedPath.substring(1);
            funTied = "";
        } else
        {
            classTied = tiedPath.substring(1, forwardSlash);
            funTied = tiedPath.substring(forwardSlash + 1);
        }


        Map<String, Object> params;
        if (index != -1)
        {
            params = new HashMap<>();
            int sharpIndex = path.indexOf("#", index);
            if (sharpIndex == -1)
            {
                sharpIndex = path.length();
            }
            String[] strs = StrUtil.split(path.substring(index + 1, sharpIndex), "&");
            try
            {
                for (String string : strs)
                {
                    index = string.indexOf('=');
                    if (index != -1)
                    {
                        params.put(URLDecoder.decode(string.substring(0, index), encoding),
                                URLDecoder.decode(string.substring(index + 1), encoding));
                    }
                }
            } catch (UnsupportedEncodingException e)
            {
                LOGGER.debug(e.getMessage(), e);
            }
        } else
        {
            params = new HashMap<>(0);
        }
        DefaultUrlResult result = new DefaultUrlResult(params, contextName, classTied, funTied);
        return result;
    }

}
