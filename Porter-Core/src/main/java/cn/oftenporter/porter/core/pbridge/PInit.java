package cn.oftenporter.porter.core.pbridge;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
public interface PInit
{
    public enum Direction
    {
        /**
         * 可以双向访问
         */
        Both,
        /**
         * 只是被添加者访问我。
         */
        ToMe,
        /**
         * 只是我访问被添加者。
         */
        ToIt
    }

    public interface LinkListener
    {
        /**
         * 得到一个被添加者可达的路径。如果当前实例需要访问it，则需要该监听。
         *
         * @param pPath
         */
        void onItCanGo(PInit it, PPath pPath);
    }

    /**
     * 只可以访问当前实例的。
     *
     * @return
     */
    PBridge currentBridge();

    /**
     * 当前实例名称。
     *
     * @return
     */
    PName currentPName();

    /**
     * 可以访问到所有可达的框架实例。
     *
     * @return
     */
    PBridge toAllBridge();

    /**
     * 调用者会把自己可达的路径发给自己返回的listener中。
     *
     * @return
     */
    LinkListener sendLink();

    /**
     * 把自己可达的路径发给该listener。
     *
     * @param init
     * @param linkListener 如果为null，表示移除监听；不为null，则表示设置监听。
     */
    void receiveLink(PInit init, LinkListener linkListener);

    /**
     * 连接两个框架实例。
     *
     * @param it        被添加的框架实例（it）
     * @param direction 可访问的方向.
     */
    void link(PInit it, Direction direction);
}
