package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.pbridge.PInit;
import cn.oftenporter.porter.core.pbridge.PName;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
public class DefaultPInitTest
{
    @Test
    public void test()
    {
        testLink(PInit.Direction.ToIt);
        testLink(PInit.Direction.BothAll);
    }

    private void testLink(PInit.Direction direction)
    {
        int count = 10;
        int threads = 10;
        int n = 10;
        PInit[] pInits = new PInit[count];
        for (int i = 0; i < pInits.length; i++)
        {
            char c = (char) ('A' + i);
            pInits[i] = new DefaultPInit(new PName(c + ""), null);
        }
        //exe(threads, n, pInits);


        pInits[0].link(pInits[1], direction);
        pInits[0].link(pInits[4], direction);
        pInits[0].link(pInits[5], direction);

        pInits[1].link(pInits[2], direction);
        pInits[4].link(pInits[3], direction);
        pInits[2].link(pInits[5], direction);

        pInits[5].link(pInits[4], direction);
        pInits[5].link(pInits[0], direction);
        pInits[3].link(pInits[2], direction);

        for (int i = 0; i < pInits.length; i++)
        {
            System.out.println("***********************************");
            System.out.println(pInits[i]);
        }
    }

    private void exe(int threads, int n, final PInit[] pInits)
    {
        ExecutorService executorService = Executors.newFixedThreadPool(threads, new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
        final Random rand = new Random();
        final int enumLen = PInit.Direction.values().length;
        for (int i = 0; i < n; i++)
        {
            executorService.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    PInit init1 = pInits[rand.nextInt(pInits.length)];
                    PInit init2 = pInits[rand.nextInt(pInits.length)];
                    init1.link(init2, PInit.Direction.values()[rand.nextInt(enumLen)]);
                }

            });
        }

        try
        {
            executorService.shutdown();
            while (!executorService.isTerminated())
            {
                Thread.sleep(20);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
