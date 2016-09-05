package cn.oftenporter.porter.core.base;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by https://github.com/CLovinr on 2016/7/24.
 */
public interface WResponse extends Closeable
{
    void write(Object object);

    void close() throws IOException;
}
