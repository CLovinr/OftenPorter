package cn.oftenporter.uibinder.core;

import cn.oftenporter.porter.core.JResponse;
import cn.oftenporter.porter.core.init.CommonMain;

import java.util.*;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/3.
 */
public class UIBinderManager implements BinderDataSender
{


    final static class UITemp
    {
        Binder binder;
        String varName;

        UITemp(Binder binder, String varName)
        {
            this.binder = binder;
            this.varName = varName;
        }

        @Override
        public int hashCode()
        {
            return varName.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (o != null && (o instanceof UITemp))
            {
                UITemp temp = (UITemp) o;
                return varName.equals(temp.varName);
            } else
            {
                return false;
            }
        }

        public void release()
        {
            binder.release();
        }

    }

    private CommonMain commonMain;
    private UIPlatform uiPlatform;
    private FireBlock fireBlock;
    //<pathPrefix,>
    private Map<String, List<UINamesOccurStore>> uiMap;
    private ErrListener errListener;

    public UIBinderManager(UIPlatform uiPlatform, CommonMain commonMain)
    {
        this.uiPlatform = uiPlatform;
        this.commonMain = commonMain;
        uiMap = new HashMap<>();
    }


    public void setErrListener(ErrListener errListener)
    {
        this.errListener = errListener;
    }

    public UIPlatform getUIPlatform()
    {
        return uiPlatform;
    }

    public CommonMain getCommonMain()
    {
        return commonMain;
    }

    public synchronized void setFireBlock(FireBlock fireBlock)
    {
        this.fireBlock = fireBlock;
    }

    public FireBlock getFireBlock()
    {
        return fireBlock;
    }

    public synchronized void bind(UIProvider uiProvider)
    {
        if(uiProvider.getErrListener()==null){
            uiProvider.setErrListener(errListener);
        }
        uiProvider.setDelivery(commonMain.getPInit());
        uiProvider.search(getUIPlatform());
        UINamesOccurStore uiNamesOccurStore = new UINamesOccurStore(this, uiProvider);
        Prefix prefix = uiProvider.getPrefix();
        List<UINamesOccurStore> list = uiMap.get(prefix.pathPrefix);
        if (list == null)
        {
            list = new ArrayList<>(1);
            uiMap.put(prefix.pathPrefix, list);
        }
        list.add(uiNamesOccurStore);
    }

    public synchronized void delete(Prefix deletePrefix)
    {
        List<UINamesOccurStore> list = uiMap.remove(deletePrefix.pathPrefix);
        if (list != null)
        {
            for (UINamesOccurStore aList : list)
            {
                aList.clear();
            }
        }
    }

    public synchronized void clear()
    {
        Iterator<List<UINamesOccurStore>> iterator = uiMap.values().iterator();
        while (iterator.hasNext())
        {
            List<UINamesOccurStore> list = iterator.next();
            for (UINamesOccurStore store : list)
            {
                store.clear();
            }
            iterator.remove();
        }
    }

    @Override
    public synchronized void sendBinderData(String pathPrefix, BinderData binderData)
    {
        List<UINamesOccurStore> list = uiMap.get(pathPrefix);
        if (list != null)
        {
            JResponse jResponse = binderData.toResponse();
            for (UINamesOccurStore aList : list)
            {
                aList.doResponse(pathPrefix, jResponse);
            }
        }
    }

}
