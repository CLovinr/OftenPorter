package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.base.ParamDealt;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 默认的参数处理错误的原因的实现。
 * Created by https://github.com/CLovinr on 2016/9/3.
 */
public class ParamDealtFailedReason
{

    static class FailedReasonImpl implements ParamDealt.FailedReason
    {
        private JSONObject jsonObject;

        public FailedReasonImpl(JSONObject jsonObject)
        {
            this.jsonObject = jsonObject;
        }

        @Override
        public String toString()
        {
            return jsonObject.toString();
        }
    }

    /**
     * 缺乏必须参数.
     *
     * @param names
     * @return
     */
    public static ParamDealt.FailedReason lackNecessaryParams(String... names)
    {
        JSONObject json = new JSONObject(2);
        json.put("type", "lack");
        JSONArray jsonArray = new JSONArray(names.length);
        json.put("names", jsonArray);
        for (int i = 0; i < names.length; i++)
        {
            jsonArray.add(names[i]);
        }
        return new FailedReasonImpl(json);
    }

    /**
     * 参数类型不合法。
     *
     * @param names
     * @return
     */
    public static ParamDealt.FailedReason illegalParams(String... names)
    {
        JSONObject json = new JSONObject(2);
        json.put("type", "illegal");
        JSONArray jsonArray = new JSONArray(names.length);
        json.put("names", jsonArray);
        for (int i = 0; i < names.length; i++)
        {
            jsonArray.add(names[i]);
        }
        return new FailedReasonImpl(json);
    }


}
