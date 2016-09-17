package cn.oftenporter.oftendb.annotation;

public interface MyConsumer<T, E extends Exception>
{
    void accept(T t) throws E;
}
