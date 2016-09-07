package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.PortInObj;

import java.util.Date;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
public class Article
{
    @PortInObj.InNece
    private String title;
    @PortInObj.InNece
    public long time;
    @PortInObj.InUnNece
    public String content;

    @Override
    public String toString()
    {
        return title + "," + new Date(time) + "," + content;
    }
}
