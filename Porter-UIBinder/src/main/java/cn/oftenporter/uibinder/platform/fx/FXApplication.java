package cn.oftenporter.uibinder.platform.fx;

import cn.oftenporter.uibinder.core.BinderFactory;
import cn.oftenporter.uibinder.core.UIPlatform;
import cn.oftenporter.uibinder.platform.fx.binders.*;
import cn.oftenporter.uibinder.simple.DefaultUIPlatform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/6.
 */
public class FXApplication
{
    private static UIPlatform uiPlatform;

    static
    {
        BinderFactory<Node> binderFactory = new BinderFactory<>(Node.class);
        binderFactory.put(Button.class, ButtonBaseBinder.class);
        binderFactory.put(TextInputControl.class, TextBinder.class);
        binderFactory.put(Labeled.class, LabeledBinder.class);
        binderFactory.put(CheckBox.class, CheckBoxBinder.class);
        binderFactory.put(ToggleButton.class, ToggleButtonBinder.class);
        binderFactory.put(ProgressIndicator.class, ProgressIndicatorBinder.class);
        binderFactory.put(Slider.class, SliderBinder.class);
        binderFactory.put(ChoiceBox.class, ChoiceBoxBinder.class);
        binderFactory.put(ComboBoxBase.class, ComboBoxBaseBinder.class);
        binderFactory.put(ComboBox.class, ComboBoxBinder.class);
        /////
        binderFactory.put(ImageView.class, ImageViewBinder.class);
        uiPlatform = new DefaultUIPlatform(binderFactory);
    }


    public static UIPlatform getUIPlatform()
    {
        return uiPlatform;
    }
}
