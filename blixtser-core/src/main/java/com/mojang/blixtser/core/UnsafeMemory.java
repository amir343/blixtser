package com.mojang.blixtser.core;

import static com.mojang.blixtser.core.ClassSchemaBuilder.stringClassInfo;
import static com.mojang.blixtser.core.SerializationUtils.unsafe;

class UnsafeMemory {

    private static final int INITIAL_CAPACITY = 1024;

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

    UnsafeMemory(final byte[] buffer) {
        if (null == buffer) {
            throw new NullPointerException("buffer cannot be null");
        }

        this.buffer = buffer;
    }

    UnsafeMemory() {
        this.buffer = new byte[INITIAL_CAPACITY];
    }

    final void reset() {
        pos = 0;
    }

    public final int getPos() {
        return pos;
    }

    final void writeByte(final byte value) {
        ensureCapacity(SIZE_OF_BYTE);
        unsafe.putByte(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_BYTE;
    }

    final byte readByte() {
        byte value = unsafe.getByte(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_BYTE;

        return value;
    }

    final void writeShort(final short value) {
        ensureCapacity(SIZE_OF_SHORT);
        unsafe.putShort(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_SHORT;
    }

    final short readShort() {
        short value = unsafe.getShort(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_SHORT;

        return value;
    }

    final void writeBoolean(final boolean value) {
        ensureCapacity(SIZE_OF_BOOLEAN);
        unsafe.putBoolean(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_BOOLEAN;
    }

    final boolean readBoolean() {
        boolean value = unsafe.getBoolean(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_BOOLEAN;

        return value;
    }

    final void writeInt(final int value) {
        ensureCapacity(SIZE_OF_INT);
        unsafe.putInt(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_INT;
    }

    final int readInt() {
        int value = unsafe.getInt(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_INT;

        return value;
    }

    final void writeLong(final long value) {
        ensureCapacity(SIZE_OF_LONG);
        unsafe.putLong(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_LONG;
    }

    final long readLong() {
        long value = unsafe.getLong(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_LONG;

        return value;
    }

    final void writeDouble(final double value) {
        ensureCapacity(SIZE_OF_DOUBLE);
        unsafe.putDouble(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_DOUBLE;
    }

    final double readDouble() {
        double value = unsafe.getDouble(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_DOUBLE;

        return value;
    }

    final void writeFloat(final float value) {
        ensureCapacity(SIZE_OF_FLOAT);
        unsafe.putFloat(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_FLOAT;
    }

    final float readFloat() {
        float value = unsafe.getFloat(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_FLOAT;

        return value;
    }

    final void writeString(final String value) {
        for (ClassSchemaBuilder.FieldInfo fi : stringClassInfo.fieldInfos) {
            fi.serialize(this, value);
        }
    }

    final String readString() {
        String object = "";
        for (ClassSchemaBuilder.FieldInfo fi : stringClassInfo.fieldInfos) {
            fi.deserialize(this, object);
        }
        return object;
    }

    final void writeChar(final char value) {
        ensureCapacity(SIZE_OF_CHAR);
        unsafe.putChar(buffer, byteArrayOffset + pos, value);
        pos += SIZE_OF_CHAR;
    }

    final char readChar() {
        char value = unsafe.getChar(buffer, byteArrayOffset + pos);
        pos += SIZE_OF_CHAR;

        return value;
    }

    final void writeInteger(final Integer value) {
        ensureCapacity(SIZE_OF_INT + 1);
        if (writeNullable(value)) {
            writeInt(value);
        }
    }

    final void writeLongWrapper(final Long value) {
        ensureCapacity(SIZE_OF_LONG + 1);
        if (writeNullable(value)) {
            writeLong(value);
        }
    }

    final void writeFloatWrapper(final Float value) {
        ensureCapacity(SIZE_OF_FLOAT + 1);
        if (writeNullable(value)) {
            writeFloat(value);
        }
    }

    final void writeDoubleWrapper(final Double value) {
        ensureCapacity(SIZE_OF_DOUBLE + 1);
        if (writeNullable(value)) {
            writeDouble(value);
        }
    }

    final void writeShortWrapper(final Short value) {
        ensureCapacity(SIZE_OF_SHORT);
        if (writeNullable(value)) {
            writeShort(value);
        }
    }

    final void writeBooleanWrapper(final Boolean value) {
        ensureCapacity(SIZE_OF_BOOLEAN + 1);
        if (writeNullable(value)) {
            writeBoolean(value);
        }
    }

    final void writeCharacter(final Character value) {
        ensureCapacity(SIZE_OF_CHAR + 1);
        if (writeNullable(value)) {
            writeChar(value);
        }
    }

    final void writeByteWrapper(final Byte value) {
        ensureCapacity(SIZE_OF_BYTE + 1);
        if (writeNullable(value)) {
            writeByte(value);
        }
    }

    final Integer readInteger() {
        return readByte() == 0 ? null : readInt();
    }

    final Long readLongWrapper() {
        return readByte() == 0 ? null : readLong();
    }

    final Short readShortWrapper() {
        return readByte() == 0 ? null : readShort();
    }

    final Boolean readBooleanWrapper() {
        return readByte() == 0 ? null : readBoolean();
    }

    final Character readCharacter() {
        return readByte() == 0 ? null : readChar();
    }

    final Float readFloatWrapper() {
        return readByte() == 0 ? null : readFloat();
    }

    final Double readDoubleWrapper() {
        return readByte() == 0 ? null : readDouble();
    }

    final Byte readByteWrapper() {
        return readByte() == 0 ? null : readByte();
    }

    final byte[] getBuffer() {
        byte[] usedBuffer = new byte[pos];

        unsafe.copyMemory(buffer, byteArrayOffset, usedBuffer, byteArrayOffset, pos);

        return usedBuffer;
    }

     final void writeCharArray(final char[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length << 1, charArrayOffset, values);
        }
    }

     final void writeByteArray(final byte[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length, byteArrayOffset, values);
        }
    }

     final void writeLongArray(final long[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length << 3, longArrayOffset, values);
        }
    }

     final void writeDoubleArray(final double[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length << 3, doubleArrayOffset, values);
        }
    }

     final void writeFloatArray(final float[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length << 2, floatArrayOffset, values);
        }
    }

     final void writeIntArray(final int[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length << 2, intArrayOffset, values);
        }
    }

     final void writeShortArray(final short[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length << 1, shortArrayOffset, values);
        }
    }

     final void writeBooleanArray(final boolean[] values) {
        ensureCapacity(SIZE_OF_INT);
        if (writeNullable(values)) {
            writeInt(values.length);
            writeGenericArray(values.length, booleanArrayOffset, values);
        }
    }

     final char[] readCharArray() {
        if (readNullable()) {
            char[] values = new char[readInt()];
            readAnArray(values.length << 1, charArrayOffset, values);
            return values;
        }
        return null;
    }

     final byte[] readByteArray() {
        if (readNullable()) {
            byte[] values = new byte[readInt()];
            readAnArray(values.length, byteArrayOffset, values);
            return values;
        }
        return null;
    }

     final int[] readIntArray() {
        if (readNullable()) {
            int[] values = new int[readInt()];
            readAnArray(values.length << 2, intArrayOffset, values);
            return values;
        }
        return null;
    }

     final long[] readLongArray() {
        if (readNullable()) {
            long[] values = new long[readInt()];
            readAnArray(values.length << 3, longArrayOffset, values);
            return values;
        }
        return null;
    }

     final double[] readDoubleArray() {
        if (readNullable()) {
            double[] values = new double[readInt()];
            readAnArray(values.length << 3, doubleArrayOffset, values);
            return values;
        }
        return null;
    }

     final float[] readFloatArray() {
        if (readNullable()) {
            float[] values = new float[readInt()];
            readAnArray(values.length << 2, floatArrayOffset, values);
            return values;
        }
        return null;
    }

     final short[] readShortArray() {
        if (readNullable()) {
            short[] values = new short[readInt()];
            readAnArray(values.length << 1, shortArrayOffset, values);
            return values;
        }
        return null;
    }

     final boolean[] readBooleanArray() {
        if (readNullable()) {
            boolean[] values = new boolean[readInt()];
            readAnArray(values.length, booleanArrayOffset, values);
            return values;
        }
        return null;
    }

     private final void writeGenericArray(long bytesToCopy, long arrayOffset, Object values) {
        ensureCapacity(bytesToCopy);
        unsafe.copyMemory(values, arrayOffset, buffer, byteArrayOffset + pos, bytesToCopy);
        pos += bytesToCopy;
    }

    private void readAnArray(long bytesToCopy, long arrayOffset, Object values) {
        unsafe.copyMemory(buffer, byteArrayOffset + pos, values, arrayOffset, bytesToCopy);
        pos += bytesToCopy;
    }

    private boolean writeNullable(final Object value) {
        if (value != null) {
            writeByte((byte) 1);
            return true;
        } else {
            writeByte((byte) 0);
            return false;
        }
    }

    private boolean readNullable() {
        return readByte() != 0;
    }

     final void writeBatch(Object object, long offset, int size) {
        ensureCapacity(size);
        unsafe.copyMemory(object, offset, buffer, byteArrayOffset + pos, size);
        pos += size;
    }

     final void readBatch(Object object, long offset, int size) {
        long off = offset;

        while (size >= SIZE_OF_LONG) {
            unsafe.putLong(object, off, readLong());
            off += SIZE_OF_LONG;
            size -= SIZE_OF_LONG;
        }

        if (size >= SIZE_OF_INT) {
            unsafe.putInt(object, off, readInt());
            off += SIZE_OF_INT;
            size -= SIZE_OF_INT;
        }

        if (size >= SIZE_OF_CHAR) {
            unsafe.putChar(object, off, readChar());
            off += SIZE_OF_CHAR;
        }

        if (size % 2 > 0) {
            unsafe.putByte(object, off, readByte());
        }

    }

    private void ensureCapacity(long minCapacity) {
        if (buffer.length - pos > minCapacity) {
            // there is enough space left in the buffer
            return;
        }

        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            throw new OutOfMemoryError("Could not allocate more than " + MAX_ARRAY_SIZE + " bytes for UnsafeMemory");
        }

        byte[] newBuffer = new byte[newCapacity];

        unsafe.copyMemory(buffer, byteArrayOffset, newBuffer, byteArrayOffset, buffer.length);
        buffer = newBuffer;
    }

}
