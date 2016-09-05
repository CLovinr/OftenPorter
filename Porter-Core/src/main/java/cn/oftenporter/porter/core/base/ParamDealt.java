package cn.oftenporter.porter.core.base;

/**
 * Created by https://github.com/CLovinr on 2016/7/23.
 */
public interface ParamDealt
{
    /**
     * 参数处理错误的原因。
     */
    public interface FailedReason
    {
    }

    /**
     * 进行参数处理
     *
     * @param names           参数名称
     * @param values          用于放参数值
     * @param isNecessary     是否是必须参数
     * @param paramSource     参数原
     * @param typeParserStore 类型转换store
     * @return 转换成功返回null，否则返回对应的错误原因。
     */
    FailedReason deal(String[] names, Object[] values, boolean isNecessary, ParamSource paramSource,
            TypeParserStore typeParserStore);


}
