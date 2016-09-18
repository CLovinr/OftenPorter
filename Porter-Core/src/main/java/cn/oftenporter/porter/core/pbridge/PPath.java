package cn.oftenporter.porter.core.pbridge;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
public class PPath
{
    public final int step;
    public final PName pName;
    /**
     * 只能访问该实例
     */
    public final PBridge bridge;
    public final PInit pInit;

    public PPath(int step, PName pName, PBridge bridge, PInit init)
    {
        this.step = step;
        this.pName = pName;
        this.bridge = bridge;
        this.pInit = init;
    }

    public PPath newPath(int newStep)
    {
        return new PPath(newStep, pName, bridge, pInit);
    }

    @Override
    public String toString()
    {
        return step + ":" + pName;
    }
}
