package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.PortInObj;
import com.alibaba.fastjson.JSONArray;

import java.util.Date;

/**
 * Created by https://github.com/CLovinr on 2016/9/8.
 */
public class Article
{
    @PortInObj.InNece
    private String title;
    @PortInObj.InNece
    private long time;
    @PortInObj.InUnNece
    private String content;
    @PortInObj.InUnNece
    private JSONArray comments;

    @Override
    public String toString()
    {
        return title + "," + new Date(time) + "," + content + "," + comments;
    }
}
