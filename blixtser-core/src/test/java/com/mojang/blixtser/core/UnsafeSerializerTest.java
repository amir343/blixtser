package com.mojang.blixtser.core;

import org.junit.Assert;
import org.junit.Test;

public class UnsafeSerializerTest {

    private UnsafeSerializer unsafeSerializer = new UnsafeSerializer();

    @Test
    public void test_serialization_deserialization() {
        unsafeSerializer.register(SerializableClass.class);

        SerializableClass serializableClass = SerializableClass.createAnObject();
        byte[] serialized = unsafeSerializer.serialize(serializableClass);
        Object deserialized = unsafeSerializer.deserialize(serialized);

        Assert.assertTrue(serializableClass.equals(deserialized));
    }

    @Test
    public void test_null_objects() {
        unsafeSerializer.register(NullClass.class);

        NullClass nullClass = new NullClass();
        byte[] serialized = unsafeSerializer.serialize(nullClass);
        Object deserialized = unsafeSerializer.deserialize(serialized);

        Assert.assertTrue(nullClass.equals(deserialized));
    }

}