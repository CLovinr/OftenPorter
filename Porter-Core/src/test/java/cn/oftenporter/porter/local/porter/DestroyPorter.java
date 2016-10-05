package cn.oftenporter.porter.local.porter;

import cn.oftenporter.porter.core.annotation.PortDestroy;
import cn.oftenporter.porter.core.annotation.PortIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by https://github.com/CLovinr on 2016/10/2.
 */
@PortIn("Destroy")
public class DestroyPorter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DestroyPorter.class);
    @PortDestroy
    public void onDestroy(){
        LOGGER.debug("[{}] destroy!",getClass());
    }
}
