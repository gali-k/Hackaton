package com.taboola.hackathon.frontend.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;

/**
 * Created by boaz.y on 15/09/2016.
 *
 * Kafka serializer for proto UserSegmentsData
 */
public class UserSegmentsSerializer implements Serializer<UserSegmentsData> {

    private static final int LINKED_BUFFER_SIZE = 65536;

    private static ThreadLocal<LinkedBuffer> s_protoBuffers = new ThreadLocal<LinkedBuffer>() {
        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(LINKED_BUFFER_SIZE);
        }
    };

    public static LinkedBuffer newProtoBuffer() {
        LinkedBuffer output = s_protoBuffers.get();
        output.clear();
        return output;
    }

    @Override
    public void configure(Map configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, UserSegmentsData data) {
        byte[] byteArray = messageToProtobufByteArray2(data, UserSegmentsData.getSchema());
        return byteArray;
    }

    public static <T> byte[] messageToProtobufByteArray2(T message, Schema<T> schema){
        LinkedBuffer buffer = newProtoBuffer();
        return ProtostuffIOUtil.toByteArray(message, schema , buffer );
    }

    @Override
    public void close() {

    }
}