package cn.oftenporter.uibinder.platform.fx;

import cn.oftenporter.uibinder.core.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.HashMap;
import java.util.Set;

/**
 * <pre>
 * 支持的javafx组件包括:
 * </pre>
 *
 * @author Created by https://github.com/CLovinr on 2016/10/6.
 */
public class FXUIProvider extends UIProvider
{
    private UIAttrGetter uiAttrGetter;
    private HashMap<UiId, Binder> hashMap;
    private Parent parent;

    /**
     * @param prefix 接口参数
     */
    public FXUIProvider(Prefix prefix, Parent parent)
    {
        super(prefix);
        this.parent = parent;
    }

    @Override
    public void search(UIPlatform uiPlatform)
    {
        hashMap = new HashMap<>();
        search(uiPlatform, parent);
    }

    private void search(UIPlatform uiPlatform, Parent parent)
    {
        ObservableList<Node> list = parent.getChildrenUnmodifiable();
        if (list.size() == 0)
        {
            return;
        }
        final String idPrefix = getPrefix().idPrefix;
        final String pathPrefix = getPrefix().pathPrefix;
        IdDeal idDeal = uiPlatform.getIdDeal();
        BinderFactory factory = uiPlatform.getBinderFactory();
        for (int i = 0; i < list.size(); i++)
        {
            Node node = list.get(i);
            UiId id = UiId.newInstance(node.getId(), idPrefix);
            if (id != null)
            {
                IdDeal.Result result = idDeal.dealId(id, pathPrefix);
                if (result != null)
                {
                    hashMap.put(id, factory.getBinder(node));
                }
            }
            if (node instanceof Parent)
            {
                search(uiPlatform, (Parent) node);
            }
        }
    }

    @Override
    public Set<UiId> getUIs()
    {
        return hashMap.keySet();
    }

    @Override
    public Binder getBinder(UiId uiId)
    {
        return hashMap.get(uiId);
    }

    @Override
    public UIAttrGetter getUIAttrGetter()
    {
        if (uiAttrGetter == null)
        {
            uiAttrGetter = new UIAttrGetterImpl();
        }
        return uiAttrGetter;
    }


}
