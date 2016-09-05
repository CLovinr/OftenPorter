package cn.oftenporter.porter.core.init;

/**
 * Created by https://github.com/CLovinr on 2016/9/3.
 */
public interface CommonMain
{
    String getName();
    PorterConf getPorterConf();
    void start();
    void destroy();
}
