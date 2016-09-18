package cn.oftenporter.porter.simple;

import cn.oftenporter.porter.core.pbridge.PUrlDecoder;

/**
 * @author Created by https://github.com/CLovinr on 2016/9/18.
 */
class DefaultPUrlDecoder implements PUrlDecoder
{

    public static class ResultImpl implements Result
    {
        private String pName, path;

        public ResultImpl(String pName, String path)
        {
            this.pName = pName;
            this.path = path;
        }

        @Override
        public String pName()
        {
            return pName;
        }

        @Override
        public String path()
        {
            return path;
        }
    }

    @Override
    public Result decode(String path)
    {
        Result result = null;

        if (path.startsWith(":"))
        {
            int index = path.indexOf('/', 1);
            if (index != -1)
            {
                result = new ResultImpl(path.substring(1, index), path.substring(index));
            }
        }
        return result;
    }
}
