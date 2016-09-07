package cn.oftenporter.porter.core.util;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by https://github.com/CLovinr on 2016/9/6.
 */
public class StrUtilTest
{
    @Test
    public void testSplit(){
        String src = "name=1&age=2&";
        String[] strs = StrUtil.split(src,"&");
        assertArrayEquals("wrong",new String[]{"name=1","age=2"},strs);
    }
}
