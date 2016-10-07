package cn.oftenporter.uibinder.platform.fx.binders;

import cn.oftenporter.uibinder.core.AttrEnum;
import cn.oftenporter.uibinder.core.ui.OnValueChangedListener;
import cn.oftenporter.uibinder.platform.fx.FXBinder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/7.
 */
public class ImageViewBinder extends FXBinder<ImageView>
{
    private EventHandler<ActionEvent> actionHandler;
    private ChangeListener<Image> changeListener;

    public ImageViewBinder(ImageView view)
    {
        super(view);
        actionHandler = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                onOccur();
            }
        };
        view.addEventFilter(ActionEvent.ACTION, actionHandler);
        changeListener = new ChangeListener<Image>()
        {
            @Override
            public synchronized void changed(ObservableValue<? extends Image> observable, Image oldValue,
                    Image newValue)
            {
                doOnchange(oldValue,newValue);
            }
        };
        view.imageProperty().addListener(changeListener);
    }


    @Override
    public void set(AttrEnum attrEnum, Object value)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            Image image = (Image) value;
            view.setImage(image);
        } else if (attrEnum == AttrEnum.ATTR_VALUE_CHANGE_LISTENER)
        {
            onValueChangedListener = (OnValueChangedListener) value;
        } else
        {
            super.set(attrEnum, value);
        }
    }

    @Override
    public Object get(AttrEnum attrEnum)
    {
        if (AttrEnum.ATTR_VALUE == attrEnum)
        {
            return view.getImage();
        } else
        {
            return super.get(attrEnum);
        }
    }

    @Override
    public void release()
    {
        super.release();
        view.removeEventHandler(ActionEvent.ACTION, actionHandler);
        view.imageProperty().removeListener(changeListener);
    }
}
