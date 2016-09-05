package cn.oftenporter.porter.core.base;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public enum TiedType
{
    REST,
    Default;

    public static TiedType type(TiedType classTiedType, TiedType methodTiedType)
    {
        if (classTiedType == REST && methodTiedType == REST)
        {
            return REST;
        } else
        {
            return Default;
        }
    }
}
