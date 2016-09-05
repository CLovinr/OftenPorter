package cn.oftenporter.porter.local;

/**
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
public interface LBridge
{
    void request(LRequest request, LCallback callback);
}
