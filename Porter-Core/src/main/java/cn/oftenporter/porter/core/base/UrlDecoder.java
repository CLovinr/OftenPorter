package cn.oftenporter.porter.core.base;


/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface UrlDecoder
{
    public interface Result extends ParamSource
    {
        String classTied();
        String funTied();
    }

    Result decode(String path);
}
