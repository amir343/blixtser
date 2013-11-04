package com.mojang.serialization;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

class SerializationUtils {

    public static Unsafe unsafe = getUnsafeInstance();

    private static Unsafe getUnsafeInstance() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    static interface Serializer {

        void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset);

    }

    static interface Deserializer {

        void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset);

    }

    /**
     *
     */
    static class IntSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeInt(unsafe.getInt(object, offset));
        }

    }

    /**
     *
     */
    static class IntDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putInt(object, offset, unsafeMemory.readInt());
        }

    }

    /**
     *
     */
    static class LongSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeLong(unsafe.getLong(object, offset));
        }

    }

    /**
     *
     */
    static class LongDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putLong(object, offset, unsafeMemory.readLong());
        }

    }

    /**
     *
     */
    static class ShortSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeShort(unsafe.getShort(object, offset));
        }

    }

    /**
     *
     */
    static class ShortDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putShort(object, offset, unsafeMemory.readShort());
        }

    }

    /**
     *
     */
    static class DoubleSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeDouble(unsafe.getDouble(object, offset));
        }

    }

    /**
     *
     */
    static class DoubleDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putDouble(object, offset, unsafeMemory.readDouble());
        }

    }

    /**
     *
     */
    static class FloatSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeFloat(unsafe.getFloat(object, offset));
        }

    }

    /**
     *
     */
    static class FloatDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putFloat(object, offset, unsafeMemory.readFloat());
        }

    }

    /**
     *
     */
    static class ByteSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeByte(unsafe.getByte(object, offset));
        }

    }

    /**
     *
     */
    static class ByteDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putByte(object, offset, unsafeMemory.readByte());
        }

    }

    /**
     *
     */
    static class BooleanSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeBoolean(unsafe.getBoolean(object, offset));
        }

    }

    /**
     *
     */
    static class BooleanDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putBoolean(object, offset, unsafeMemory.readBoolean());
        }

    }

    /**
     *
     */
    static class CharSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeChar(unsafe.getChar(object, offset));
        }

    }

    /**
     *
     */
    static class CharDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putChar(object, offset, unsafeMemory.readChar());
        }

    }

    /**
     *
     */
    static class StringSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeString((String) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class StringDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readString());
        }

    }

    /**
     *
     */
    static class IntegerSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeInteger((Integer) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class IntegerDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readInteger());
        }

    }

    /**
     *
     */
    static class LongWrapperSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeLongWrapper((Long) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class LongWrapperDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readLongWrapper());
        }

    }

    /**
     *
     */
    static class DoubleWrapperSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeDoubleWrapper((Double) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class DoubleWrapperDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readDoubleWrapper());
        }

    }

    /**
     *
     */
    static class ShortWrapperSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeShortWrapper((Short) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class ShortWrapperDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readShortWrapper());
        }

    }

    /**
     *
     */
    static class FloatWrapperSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeFloatWrapper((Float) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class FloatWrapperDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readFloatWrapper());
        }

    }

    /**
     *
     */
    static class CharacterSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeCharacter((Character) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class CharacterDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readCharacter());
        }

    }

    /**
     *
     */
    static class ByteWrapperSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeByteWrapper((Byte) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class ByteWrapperDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readByteWrapper());
        }

    }

    /**
     *
     */
    static class BooleanWrapperSerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeBooleanWrapper((Boolean) unsafe.getObject(object, offset));
        }


    }

    /**
     *
     */
    static class BooleanWrapperDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readBooleanWrapper());
        }

    }

    /**
     *
     */
    static class CharArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeCharArray((char[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class CharArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readCharArray());
        }

    }

    /**
     *
     */
    static class IntArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeIntArray((int[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class IntArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readIntArray());
        }

    }

    /**
     *
     */
    static class ShortArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeShortArray((short[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class ShortArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readShortArray());
        }

    }

    /**
     *
     */
    static class LongArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeLongArray((long[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class LongArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readLongArray());
        }

    }

    /**
     *
     */
    static class DoubleArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeDoubleArray((double[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class DoubleArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readDoubleArray());
        }

    }

    /**
     *
     */
    static class FloatArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeFloatArray((float[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class FloatArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readFloatArray());
        }

    }

    /**
     *
     */
    static class BooleanArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeBooleanArray((boolean[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class BooleanArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readBooleanArray());
        }

    }

    /**
     *
     */
    static class ByteArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeByteArray((byte[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class ByteArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readByteArray());
        }

    }

    /**
     *
     */
    static class StringArraySerializer implements Serializer {

        @Override
        public void serialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafeMemory.writeStringArray((String[]) unsafe.getObject(object, offset));
        }

    }

    /**
     *
     */
    static class StringArrayDeserializer implements Deserializer {

        @Override
        public void deserialize(UnsafeSerializer.UnsafeMemory unsafeMemory, Object object, long offset) {
            unsafe.putObject(object, offset, unsafeMemory.readStringArray());
        }

    }

}
