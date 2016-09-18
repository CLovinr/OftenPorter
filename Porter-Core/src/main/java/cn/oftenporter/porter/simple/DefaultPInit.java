package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.JResponse;
import cn.oftenporter.porter.core.ResultCode;
import cn.oftenporter.porter.core.pbridge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认实现,记录所有可达路径。
 *
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
public class DefaultPInit implements PInit
{
    private PName pName;
    private PBridge current;
    private LinkListener linkListener;
    private Map<PName, LinkListener> listenerMap;

    private Map<String, PPath> pathMap;
    private static final Object LOCK = new Object();
    private PBridge toAll;
    private PUrlDecoder pUrlDecoder;

    private static class Response extends PResponse
    {

        protected Response(Object object)
        {
            super(object);
        }
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPInit.class);

    public DefaultPInit(PName pName, PBridge bridge)
    {
        this.pName = pName;
        this.current = bridge;
        pathMap = new ConcurrentHashMap<>();
        listenerMap = new ConcurrentHashMap<>();
        pUrlDecoder = new DefaultPUrlDecoder();

        //自己可达自己
        pathMap.put(pName.getName(), new PPath(0, pName, bridge, this));

        linkListener = new LinkListener()
        {
            @Override
            public void onItCanGo(PInit it, PPath pPath)
            {
                synchronized (LOCK)
                {
                    if (pPath.pName.equals(currentPName()))
                    {//不用再添加自己。
                        return;
                    }
                    PPath path = pathMap.get(pPath.pName.getName());
                    if (path == null || path.step > pPath.step + 1)
                    {//保存路径更短者
                        PPath newPath = pPath.newPath(pPath.step + 1);
                        putPath(newPath);
                        //通知其他人我所达到的路径
                        forAll().onItCanGo(DefaultPInit.this, newPath);
                        //接收对方的。
                        newPath.pInit.receiveLink(DefaultPInit.this, linkListener);
                    }
                }
            }

        };


        toAll = new PBridge()
        {
            @Override
            public void request(PRequest request, PCallback callback)
            {

                String pname = request.getPName();
                PPath path;
                if (pname == null||(path=pathMap.get(pname))==null )
                {
                    JResponse jResponse = new JResponse(ResultCode.NOT_AVAILABLE);
                    jResponse.setDescription(request.getPath());
                    PResponse response = new Response(jResponse);
                    callback.onResponse(response);
                } else
                {
                    path.bridge.request(request.withNewPath(null, request.getPath()), callback);
                }

            }
        };

    }


    @Override
    public PBridge currentBridge()
    {
        return current;
    }

    @Override
    public PName currentPName()
    {
        return pName;
    }

    @Override
    public PBridge toAllBridge()
    {
        return toAll;
    }

    @Override
    public LinkListener sendLink()
    {
        return linkListener;
    }


    @Override
    public void receiveLink(PInit init, LinkListener linkListener)
    {
        synchronized (LOCK)
        {

            if (linkListener == null)
            {
                listenerMap.remove(init.currentPName());
            } else
            {
                listenerMap.put(init.currentPName(), linkListener);

                sendCurrentPath2Listener(linkListener);
            }
        }
    }

    private void putPath(PPath path)
    {
        PPath last = pathMap.put(path.pInit.currentPName().getName(), path);
        if (last != null && !last.pInit.equals(path.pInit))
        {
            LOGGER.warn("PName '{}' been added before(current:{},last:{})", path.pName, path.pInit, last.pInit);
        }
    }

    //发送目前可达的路径
    private void sendCurrentPath2Listener(LinkListener listener)
    {
        synchronized (LOCK)
        {
            //自己可达自己，步数为0.
            listener.onItCanGo(this, new PPath(0, currentPName(), currentBridge(), this));
            Iterator<PPath> pathIterator = pathMap.values().iterator();
            while (pathIterator.hasNext())
            {
                listener.onItCanGo(this, pathIterator.next());
            }
        }
    }

    private LinkListener forAll()
    {
        synchronized (LOCK)
        {
            LinkListener all = new LinkListener()
            {
                @Override
                public void onItCanGo(PInit it, PPath pPath)
                {
                    Iterator<LinkListener> iterator = listenerMap.values().iterator();
                    while (iterator.hasNext())
                    {
                        iterator.next().onItCanGo(it, pPath);
                    }
                }
            };
            return all;
        }
    }

    @Override
    public void link(PInit it, PInit.Direction direction)
    {
        synchronized (LOCK)
        {
            boolean sendMyGoPath = false;
            boolean addIt = false;
            switch (direction)
            {
                case Both:
                {
                    addIt = true;
                    sendMyGoPath = true;
                }
                break;
                case ToMe:
                {
                    sendMyGoPath = true;
                }
                break;
                case ToIt:
                {
                    addIt = true;
                }
                break;
            }

            if (addIt)
            {
                PPath path = new PPath(1, it.currentPName(), it.currentBridge(), it);

                putPath(path);

                //发送新添加的可达路径给所有监听者。
                forAll().onItCanGo(this, path);

                //用于接收对方可达的路径
                it.receiveLink(this, linkListener);
            }

            if (sendMyGoPath)
            {
                LinkListener listener = it.sendLink();
                sendCurrentPath2Listener(listener);
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(currentPName().toString()).append("\n");
        Iterator<PPath> pathIterator = pathMap.values().iterator();
        while (pathIterator.hasNext())
        {
            builder.append("\t").append(pathIterator.next()).append("\n");
        }
        return builder.toString();
    }
}
