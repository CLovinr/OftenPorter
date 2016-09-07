package cn.oftenporter.porter.local;

import cn.oftenporter.porter.core.annotation.NotNull;
import cn.oftenporter.porter.core.base.WResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
class LocalResponse implements WResponse
{
    private List<Object> list;
    private LCallback callback;

    public LocalResponse(LCallback callback)
    {
        this.callback = callback;
        list = new ArrayList<>(1);
    }

    @Override
    public void write(@NotNull Object object)
    {
        list.add(object);
    }

    @Override
    public void close() throws IOException
    {
        if (callback != null)
        {
            callback.onResponse(new LResponse(list));
        }
    }
}
