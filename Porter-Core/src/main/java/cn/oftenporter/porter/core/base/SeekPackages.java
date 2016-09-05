package cn.oftenporter.porter.core.base;


import com.alibaba.fastjson.JSONArray;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public class SeekPackages
{
    private JSONArray jsonArray = new JSONArray();

    public SeekPackages()
    {

    }

    public SeekPackages addPorters(String... packages)
    {

        for (int i = 0; i < packages.length; i++)
        {
            jsonArray.add(packages[i]);
        }

        return this;
    }

    public JSONArray getPackages()
    {
        return jsonArray;
    }
}
