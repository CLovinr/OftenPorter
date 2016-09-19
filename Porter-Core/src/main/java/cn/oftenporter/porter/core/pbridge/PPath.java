package cn.oftenporter.porter.core.pbridge;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
public class PPath
{
    public final int step;
    public final PName pName;
    public final PInit pInit;

    public PPath(int step, PName pName, PInit init)
    {
        this.step = step;
        this.pName = pName;
        this.pInit = init;
    }

    public PPath newPath(int newStep)
    {
        return new PPath(newStep, pName, pInit);
    }

    @Override
    public String toString()
    {
        return step + ":" + pName;
    }
}
