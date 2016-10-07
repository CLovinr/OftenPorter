package cn.oftenporter.uibinder;

import cn.oftenporter.porter.core.annotation.PortIn;
import cn.oftenporter.uibinder.core.Prefix;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * Prefix Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Ê®ÔÂ 3, 2016</pre>
 */
public class PrefixTest
{

    @Before
    public void before() throws Exception
    {
    }

    @After
    public void after() throws Exception
    {
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception
    {
//TODO: Test goes here... 
    }

    /**
     * Method: forDelete(String porterPrefix)
     */
    @Test
    public void testForDelete() throws Exception
    {
        Prefix prefix = Prefix.forDelete("C0", TempPorter.class);
        Assert.assertEquals("/C0/Temp/", prefix.pathPrefix);
    }

    /**
     * Method: buildPrefix(Class<?> c)
     */
    @Test
    public void testBuildPrefix() throws Exception
    {
        Prefix prefix = Prefix.buildPrefix("C0", TempPorter.class);
        Assert.assertEquals("temp_", prefix.idPrefix);
        Assert.assertEquals("/C0/Temp/", prefix.pathPrefix);
    }


    @PortIn("Temp")
    public static class TempPorter
    {

    }


} 
