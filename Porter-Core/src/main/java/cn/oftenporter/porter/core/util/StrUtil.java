package cn.oftenporter.porter.core.util;

import java.util.StringTokenizer;

/**
 * Created by https://github.com/CLovinr on 2016/9/6.
 */
public class StrUtil
{
    public static String[] split(String srcString, String splitStr)
    {
        StringTokenizer tokenizer = new StringTokenizer(srcString, splitStr);
        String[] rs = new String[tokenizer.countTokens()];
        for (int i = 0; i < rs.length; i++)
        {
            rs[i] = tokenizer.nextToken();
        }
        return rs;
    }
}
