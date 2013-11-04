package com.mojang.serialization;

import static com.mojang.serialization.SerializationUtils.*;
import static com.mojang.serialization.ClassSchemaBuilder.*;

@SuppressWarnings("all")
public class UnsafeSerializer {

    private final UnsafeMemory unsafeMemory = new UnsafeMemory(new byte[1024]);
    private final ClassSchemaBuilder classSchemaBuilder = new ClassSchemaBuilder();


    public UnsafeSerializer() {
        classSchemaBuilder.build();
    }

    public void register(Class c) {
        classSchemaBuilder.registerClass(c);
    }

    public synchronized byte[] serialize(Object obj) {
        unsafeMemory.reset();

        Class<?> c = obj.getClass();
        int code = c.hashCode();
        unsafeMemory.writeInt(code);

        for (FieldInfo f : classInfoCache.get(code).fieldInfos) {
            f.fieldSerializer.serialize(unsafeMemory, obj, f.offset);
        }

        return unsafeMemory.getBuffer();
    }

    public synchronized Object deserialize(byte[] arr) {
        UnsafeMemory unsafeMemory = new UnsafeMemory(arr);

        int code = unsafeMemory.readInt();
        ClassInfo classInfo = classInfoCache.get(code);
        try {
            Object obj = unsafe.allocateInstance(classInfo.clazz);

            for (FieldInfo f : classInfo.fieldInfos) {
                f.fieldSerializer.deserialize(unsafeMemory, obj, f.offset);
            }

            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    static class UnsafeMemory {

        private static final long byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
        private static final long shortArrayOffset = unsafe.arrayBaseOffset(short[].class);
        private static final long intArrayOffset = unsafe.arrayBaseOffset(int[].class);
        private static final long charArrayOffset = unsafe.arrayBaseOffset(char[].class);
        private static final long floatArrayOffset = unsafe.arrayBaseOffset(float[].class);
        private static final long longArrayOffset = unsafe.arrayBaseOffset(long[].class);
        private static final long doubleArrayOffset = unsafe.arrayBaseOffset(double[].class);
        private static final long booleanArrayOffset = unsafe.arrayBaseOffset(boolean[].class);

        private static final int SIZE_OF_LONG = 8;
        private static final int SIZE_OF_DOUBLE = 8;
        private static final int SIZE_OF_INT = 4;
        private static final int SIZE_OF_FLOAT = 4;
        private static final int SIZE_OF_CHAR = 2;
        private static final int SIZE_OF_SHORT = 2;
        private static final int SIZE_OF_BOOLEAN = 1;
        private static final int SIZE_OF_BYTE = 1;

        private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

        private int pos = 0;
        private byte[] buffer;

        private static final int STRING_HASH_CODE = String.class.hashCode();

        public UnsafeMemory(final byte[] buffer) {
            if (null == buffer) {
                throw new NullPointerException("buffer cannot be null");
            }

            this.buffer = buffer;
        }

        public void reset() {
            pos = 0;
        }

        public int getPos() {
            return pos;
        }

        public void writeByte(final byte value) {
            ensureCapacity(SIZE_OF_BYTE);
            unsafe.putByte(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_BYTE;
        }

        public byte readByte() {
            byte value = unsafe.getByte(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_BYTE;

            return value;
        }

        public void writeShort(final short value) {
            ensureCapacity(SIZE_OF_SHORT);
            unsafe.putShort(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_SHORT;
        }

        public short readShort() {
            short value = unsafe.getShort(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_SHORT;

            return value;
        }

        public void writeBoolean(final boolean value) {
            ensureCapacity(SIZE_OF_BOOLEAN);
            unsafe.putBoolean(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_BOOLEAN;
        }

        public boolean readBoolean() {
            boolean value = unsafe.getBoolean(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_BOOLEAN;

            return value;
        }

        public void writeInt(final int value) {
            ensureCapacity(SIZE_OF_INT);
            unsafe.putInt(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_INT;
        }

        public int readInt() {
            int value = unsafe.getInt(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_INT;

            return value;
        }

        public void writeLong(final long value) {
            ensureCapacity(SIZE_OF_LONG);
            unsafe.putLong(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_LONG;
        }

        public long readLong() {
            long value = unsafe.getLong(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_LONG;

            return value;
        }

        public void writeDouble(final double value) {
            ensureCapacity(SIZE_OF_DOUBLE);
            unsafe.putDouble(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_DOUBLE;
        }

        public double readDouble() {
            double value = unsafe.getDouble(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_DOUBLE;

            return value;
        }

        public void writeFloat(final float value) {
            ensureCapacity(SIZE_OF_FLOAT);
            unsafe.putFloat(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_FLOAT;
        }

        public float readFloat() {
            float value = unsafe.getFloat(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_FLOAT;

            return value;
        }

        public void writeString(final String value) {
            for (FieldInfo fi : stringClassInfo.fieldInfos) {
                fi.fieldSerializer.serialize(this, value, fi.offset);
            }
        }

        public String readString() {
            String object = new String();
            for (FieldInfo fi : stringClassInfo.fieldInfos) {
                fi.fieldSerializer.deserialize(this, object, fi.offset);
            }
            return object;
        }

        public void writeChar(final char value) {
            ensureCapacity(SIZE_OF_CHAR);
            unsafe.putChar(buffer, byteArrayOffset + pos, value);
            pos += SIZE_OF_CHAR;
        }

        public char readChar() {
            char value = unsafe.getChar(buffer, byteArrayOffset + pos);
            pos += SIZE_OF_CHAR;

            return value;
        }

        public void writeInteger(final Integer value) {
            ensureCapacity(SIZE_OF_INT + 1);
            if (writeAWrapper(value) == 1) {
                writeInt(value);
            }
        }

        public void writeLongWrapper(final Long value) {
            ensureCapacity(SIZE_OF_LONG + 1);
            if (writeAWrapper(value) == 1) {
                writeLong(value);
            }
        }

        public void writeFloatWrapper(final Float value) {
            ensureCapacity(SIZE_OF_FLOAT + 1);
            if (writeAWrapper(value) == 1) {
                writeFloat(value);
            }
        }

        public void writeDoubleWrapper(final Double value) {
            ensureCapacity(SIZE_OF_DOUBLE + 1);
            if (writeAWrapper(value) == 1) {
                writeDouble(value);
            }
        }

        public void writeShortWrapper(final Short value) {
            ensureCapacity(SIZE_OF_SHORT);
            if (writeAWrapper(value) == 1) {
                writeShort(value);
            }
        }

        public void writeBooleanWrapper(final Boolean value) {
            ensureCapacity(SIZE_OF_BOOLEAN + 1);
            if (writeAWrapper(value) == 1) {
                writeBoolean(value);
            }
        }

        public void writeCharacter(final Character value) {
            ensureCapacity(SIZE_OF_CHAR + 1);
            if (writeAWrapper(value) == 1) {
                writeChar(value);
            }
        }

        public void writeByteWrapper(final Byte value) {
            ensureCapacity(SIZE_OF_BYTE + 1);
            if (writeAWrapper(value) == 1) {
                writeByte(value);
            }
        }

        public Integer readInteger() {
            return readByte() == 0 ? null : readInt();
        }

        public Long readLongWrapper() {
            return readByte() == 0 ? null : readLong();
        }

        public Short readShortWrapper() {
            return readByte() == 0 ? null : readShort();
        }

        public Boolean readBooleanWrapper() {
            return readByte() == 0 ? null : readBoolean();
        }

        public Character readCharacter() {
            return readByte() == 0 ? null : readChar();
        }

        public Float readFloatWrapper() {
            return readByte() == 0 ? null : readFloat();
        }

        public Double readDoubleWrapper() {
            return readByte() == 0 ? null : readDouble();
        }

        public Byte readByteWrapper() {
            return readByte() == 0 ? null : readByte();
        }

        public byte[] getBuffer() {
            byte[] usedBuffer = new byte[pos];

            unsafe.copyMemory(buffer, byteArrayOffset, usedBuffer, byteArrayOffset, pos);

            return buffer;
        }

        public void writeCharArray(final char[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length << 1, charArrayOffset, values);
        }

        public void writeByteArray(final byte[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length, byteArrayOffset, values);
        }

        public void writeLongArray(final long[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length << 3, longArrayOffset, values);
        }

        public void writeDoubleArray(final double[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length << 3, doubleArrayOffset, values);
        }

        public void writeFloatArray(final float[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length << 2, floatArrayOffset, values);
        }

        public void writeIntArray(final int[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length << 2, intArrayOffset, values);
        }

        public void writeShortArray(final short[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length << 1, shortArrayOffset, values);
        }

        public void writeBooleanArray(final boolean[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            writeAnArray(values.length, booleanArrayOffset, values);
        }

        public void writeStringArray(final String[] values) {
            ensureCapacity(SIZE_OF_INT);
            writeInt(values.length);
            for (String value : values) {
                writeString(value);
            }
        }

        public char[] readCharArray() {
            char[] values = new char[readInt()];
            readAnArray(values.length << 1, charArrayOffset, values);
            return values;
        }

        public byte[] readByteArray() {
            byte[] values = new byte[readInt()];
            readAnArray(values.length, byteArrayOffset, values);
            return values;
        }

        public int[] readIntArray() {
            int[] values = new int[readInt()];
            readAnArray(values.length << 2, intArrayOffset, values);
            return values;
        }

        public long[] readLongArray() {
            long[] values = new long[readInt()];
            readAnArray(values.length << 3, longArrayOffset, values);
            return values;
        }

        public double[] readDoubleArray() {
            double[] values = new double[readInt()];
            readAnArray(values.length << 3, doubleArrayOffset, values);
            return values;
        }

        public float[] readFloatArray() {
            float[] values = new float[readInt()];
            readAnArray(values.length << 2, floatArrayOffset, values);
            return values;
        }

        public short[] readShortArray() {
            short[] values = new short[readInt()];
            readAnArray(values.length << 1, shortArrayOffset, values);
            return values;
        }

        public boolean[] readBooleanArray() {
            boolean[] values = new boolean[readInt()];
            readAnArray(values.length, booleanArrayOffset, values);
            return values;
        }

        public String[] readStringArray() {
            String[] values = new String[readInt()];
            for (int i = 0; i < values.length; i++) {
                values[i] = readString();
            }
            return values;
        }

        public void writeAnArray(long bytesToCopy, long arrayOffset, Object values) {
            ensureCapacity(bytesToCopy);
            unsafe.copyMemory(values, arrayOffset, buffer, byteArrayOffset + pos, bytesToCopy);
            pos += bytesToCopy;
        }

        private void readAnArray(long bytesToCopy, long arrayOffset, Object values) {
            unsafe.copyMemory(buffer, byteArrayOffset + pos, values, arrayOffset, bytesToCopy);
            pos += bytesToCopy;
        }

        private byte writeAWrapper(final Object value) {
            byte flag = 0;
            if (value == null) {
                writeByte(flag);
            } else {
                flag = 1;
                writeByte(flag);
            }
            return flag;
        }

        private void ensureCapacity(long minCapacity) {
            if (buffer.length - pos > minCapacity) {
                // there is enough space left in the buffer
                return;
            }

            int oldCapacity = buffer.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - MAX_ARRAY_SIZE > 0)
                throw new OutOfMemoryError("Could not allocate more than " + MAX_ARRAY_SIZE + " bytes for UnsafeMemory");

            byte[] newBuffer = new byte[newCapacity];

            unsafe.copyMemory(buffer, byteArrayOffset, newBuffer, byteArrayOffset, buffer.length);
            buffer = newBuffer;
        }

        public void resetWith(byte[] arr) {
            pos = 0;
            unsafe.copyMemory(arr, byteArrayOffset, buffer, byteArrayOffset, arr.length);
        }
    }

}
