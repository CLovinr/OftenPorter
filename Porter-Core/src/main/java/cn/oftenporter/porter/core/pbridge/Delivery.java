package cn.oftenporter.porter.core.pbridge;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/19.
 */
public interface Delivery
{
    /**
     * 只可以访问当前实例的。
     *
     * @return
     */
    PBridge currentBridge();

    /**
     * 可以访问到所有可达的框架实例。
     *
     * @return
     */
    PBridge toAllBridge();

    /**
     * 当前实例名称。
     *
     * @return
     */
    PName currentPName();
}
