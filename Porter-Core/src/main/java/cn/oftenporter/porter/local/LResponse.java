package cn.oftenporter.porter.local;

import java.util.List;

/**
 * Created by https://github.com/CLovinr on 2016/9/2.
 */
public class LResponse
{
    private List<Object> list;

    LResponse(List<Object> list)
    {
        this.list = list;
    }

    public List<Object> getResponse()
    {
        return list;
    }

    public Object first()
    {
        if (list.size() == 0)
        {
            return null;
        } else
        {
            return list.get(0);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        if (list != null && list.size() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                stringBuilder.append(list.get(i)).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
