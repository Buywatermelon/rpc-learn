package com.github.watermelon.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    private static final Map<Class<?>, Schema<?>> cachedScheme = new ConcurrentHashMap<>();

    private static final Objenesis objenesis = new ObjenesisStd(true);

    /**
     * 序列化 (对象 -> 字节数组)
     *
     * @param obj 对象
     * @param <T> 对象泛型
     * @return 字节数组
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化 (字节数组 -> 对象)
     *
     * @param data 字节数组
     * @param cls  对象类型
     * @param <T>  对象泛型
     * @return 对象
     */
    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T message = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedScheme.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedScheme.put(cls, schema);
        }
        return schema;
    }
}
