package cn.oftenporter.uibinder.core;


import cn.oftenporter.porter.core.pbridge.Delivery;
import cn.oftenporter.porter.core.pbridge.PBridge;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class UIProvider
{
    private Prefix prefix;
    private ErrListener errListener;
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private int id;
    private Delivery delivery;

    /**
     * @param prefix 接口参数
     */
    public UIProvider(Prefix prefix)
    {
        setErrListener(prefix.errListener);
        this.prefix = prefix;
        id = atomicInteger.incrementAndGet();
    }

    void setDelivery(Delivery delivery)
    {
        this.delivery = delivery;
    }

    /**
     * 得到id
     *
     * @return 该UIProvider的id
     */
    public int getId()
    {
        return id;
    }

    public Prefix getPrefix()
    {
        return prefix;
    }

    public abstract void search(UIPlatform uiPlatform);

    /**
     * 得到UiId
     *
     * @return
     */
    public abstract Set<UiId> getUIs();

    /**
     * 得到绑定对象
     *
     * @param uiId
     * @return
     */
    public abstract Binder getBinder(UiId uiId);

    public abstract UIAttrGetter getUIAttrGetter();

    public final Delivery getDelivery()
    {
        return delivery;
    }

    public void setErrListener(ErrListener errListener)
    {
        this.errListener = errListener;
    }

    public ErrListener getErrListener()
    {
        return errListener;
    }

}
