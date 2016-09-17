package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.UrlDecoder;
import  static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/16.
 */
public class DefaultDecoderTest
{
    @Test
    public void testDecode(){
        DefaultUrlDecoder defaultDecoder = new DefaultUrlDecoder("","utf-8");

        UrlDecoder.Result result = defaultDecoder.decode("/C1/Hello/say");
        assertEquals("C1",result.contextName());
        assertEquals("Hello",result.classTied());
        assertEquals("say",result.funTied());
    }
}
