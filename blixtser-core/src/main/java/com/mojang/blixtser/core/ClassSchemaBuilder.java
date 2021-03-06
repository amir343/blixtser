package com.mojang.blixtser.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.*;

import static com.mojang.blixtser.core.TypeRepository.*;

class ClassSchemaBuilder {

    static final Map<Integer, ClassInfo> classInfoCache = new HashMap<>();

    static ClassInfo stringClassInfo;
    static ClassInfo stringBufferInfo;
    static ClassInfo stringBuilderInfo;
    static ClassInfo bigIntegerClassInfo;
    static ClassInfo dateClassInfo;

    ClassSchemaBuilder() {
        Set<String> ignoreFields = new HashSet<>();

        stringClassInfo = createClassInfo(String.class);
        stringBufferInfo = createClassInfo(StringBuffer.class);
        stringBuilderInfo = createClassInfo(StringBuilder.class);
        bigIntegerClassInfo = createClassInfo(BigInteger.class);

        ignoreFields.clear();
        ignoreFields.add("cdate");
        dateClassInfo = createClassInfo(Date.class, ignoreFields, true);
    }

    void registerClass(Class<?> c, Set<String> ignoreFields) {
        registerClass(c, ignoreFields, false);
    }

    ClassInfo createClassInfo(Class<?> c) {
        return createClassInfo(c, Collections.<String>emptySet(), false);
    }

    private void registerClass(Class<?> c, Set<String> ignoreFields, boolean withTransient) {
        ClassInfo classInfo = classInfoCache.get(c);
        if (classInfo == null) {
            validateClass(c);
            classInfo = createClassInfo(c, ignoreFields, withTransient);
            int code = c.hashCode();
            classInfoCache.put(code, classInfo);
        }
    }

    private ClassInfo createClassInfo(Class<?> c, Set<String> fieldNamesToIgnore, boolean withTransient) {
        ClassInfo classInfo = classInfoCache.get(c);
        if (classInfo == null) {
            validateClass(c);
            List<Field> fields = getFieldsFor(c, fieldNamesToIgnore, withTransient);
            List<FieldInfo> fieldsWithInfo = mapFieldsToFieldInfo(fields);
            FieldInfo[] fieldInfos = fieldsWithInfo.toArray(new FieldInfo[fieldsWithInfo.size()]);
            sortFieldOnOffset(fieldInfos);
            fieldInfos = mergeNonVolatilePrimitiveFields(fieldInfos);
            classInfo = new ClassInfo(c, fieldInfos);
        }
        return classInfo;
    }

    private void validateClass(Class<?> c) {
        if (Modifier.isAbstract(c.getModifiers()) || Modifier.isInterface(c.getModifiers())) {
            throw new RuntimeException("Can not register an abstract class");
        }

        if (Modifier.isInterface(c.getModifiers())) {
            throw new RuntimeException("Can not register an interface class");
        }
    }

    private List<FieldInfo> mapFieldsToFieldInfo(List<Field> fields) {
        List<FieldInfo> fieldsWithInfo = new ArrayList<>(fields.size());
        for (Field field : fields) {
            FieldInfo fi = createFieldInfo(field);
            if (fi != null) {
                fieldsWithInfo.add(fi);
            }
        }
        return fieldsWithInfo;
    }

    private FieldInfo createFieldInfo(Field field) {
        if (Modifier.isVolatile(field.getModifiers())) {
            return createVolatileField(field);
        } else {
            return createNonVolatileField(field);
        }
    }

    private FieldInfo createNonVolatileField(Field field) {
        if (nonVolatileTypeRepository.serializerDeserializerExistsFor(field.getType())) {
            return new FieldInfo(field, nonVolatileTypeRepository.getSerializer(field.getType()),
                    nonVolatileTypeRepository.getDeserializer(field.getType()));
        } else if (field.getType().getSuperclass() == Enum.class) {
            return new EnumFieldInfo(field, nonVolatileTypeRepository.getSerializer(Enum.class));
        } else {
            System.out.println("Unsupported declared field " + field.getName() + "[" + field.getType().getName() + "] in Class " + field.getDeclaringClass().getName() + ", skipping...");
            return null;
        }
    }

    private FieldInfo createVolatileField(Field field) {
        if (volatileTypeRepository.serializerDeserializerExistsFor(field.getType())) {
            return new FieldInfo(field, volatileTypeRepository.getSerializer(field.getType()), volatileTypeRepository.getDeserializer(field.getType()));
        } else if (field.getType().getSuperclass() == Enum.class) {
            return new EnumVolatileFieldInfo(field, volatileTypeRepository.getSerializer(Enum.class));
        } else {
            System.out.println("Unsupported declared field " + field.getName() + "[" + field.getType().getName() + "] in Class " + field.getDeclaringClass().getName() + ", skipping...");
            return null;
        }
    }

    private void sortFieldOnOffset(FieldInfo[] infos) {
        Arrays.sort(infos, new Comparator<FieldInfo>() {
            @Override
            public int compare(FieldInfo o1, FieldInfo o2) {
                return (int) (o1.offset - o2.offset);
            }
        });
    }

    private List<Field> getFieldsFor(Class<?> c, Set<String> ignoreFields, boolean withTransient) {
        List<Field> fields = new ArrayList<>();
        List<Field> ffs = getAllFieldsRecursiveFor(c);
        for (Field f : ffs) {
            if (ignoreFields.contains(f.getName())) {
                continue;
            }
            if ((f.getModifiers() & Modifier.STATIC) == 0) {
                if (withTransient || (f.getModifiers() & Modifier.TRANSIENT) == 0) {
                    fields.add(f);
                }
            }
        }
        return fields;
    }

    private List<Field> getAllFieldsRecursiveFor(Class<?> c) {
        if (c == Object.class) {
            return new ArrayList<>();
        }
        List<Field> allFields = getAllFieldsRecursiveFor(c.getSuperclass());
        allFields.addAll(Arrays.asList(c.getDeclaredFields()));
        return allFields;
    }

    private FieldInfo[] mergeNonVolatilePrimitiveFields(FieldInfo[] fields) {
        if (fields.length <= 1) {
            return fields;
        }

        List<FieldInfo> mergingResult = new ArrayList<>();
        mergingResult.add(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            FieldInfo previousField = mergingResult.get(mergingResult.size() - 1);
            FieldInfo currentField = fields[i];

            if (isNonVolatilePrimitive(previousField) && isNonVolatilePrimitive(currentField)) {
                BatchFieldInfo batchField = previousField.merge(currentField);
                mergingResult.set(mergingResult.size() - 1, batchField);
            } else {
                mergingResult.add(currentField);
            }
        }

        return mergingResult.toArray(new FieldInfo[mergingResult.size()]);
    }

    private boolean isNonVolatilePrimitive(FieldInfo fieldInfo) {
        return fieldInfo.type().isPrimitive() &&
                !Modifier.isVolatile(fieldInfo.field.getModifiers());
    }

    /**
     *
     */
    static class FieldInfo {

        SerializationUtils.Serializer fieldSerializer;
        SerializationUtils.Deserializer fieldDeserializer;
        final Field field;
        long offset;

        FieldInfo(Field field, SerializationUtils.Serializer fieldSerializer, SerializationUtils.Deserializer fieldDeserializer) {
            this.field = field;
            this.fieldSerializer = fieldSerializer;
            this.fieldDeserializer = fieldDeserializer;
            this.offset = SerializationUtils.unsafe.objectFieldOffset(field);
        }

        void serialize(UnsafeMemory unsafeMemory, Object object) {
            fieldSerializer.serialize(unsafeMemory, object, offset);
        }

        void deserialize(UnsafeMemory unsafeMemory, Object object) {
            fieldDeserializer.deserialize(unsafeMemory, object, offset);
        }

        Class<?> type() {
            return field.getType();
        }

        public BatchFieldInfo merge(FieldInfo fieldInfo) {
            int size = (int) (fieldInfo.offset - this.offset + sizeOf(fieldInfo.type()));
            return new BatchFieldInfo(this, size);
        }

        @Override
        public String toString() {
            return "FieldInfo{" +
                    "offset=" + offset +
                    ", name=" + field.getName() +
                    ", type=" + field.getType() +
                    '}';
        }
    }

    /**
     *
     */
    private final static class BatchFieldInfo extends FieldInfo {

        private final FieldInfo firstField;

        BatchFieldInfo(FieldInfo firstField, int size) {
            super(firstField.field, new SerializationUtils.BatchSerializer(size), new SerializationUtils.BatchDeserializer(size));
            this.firstField = firstField;
        }

        public BatchFieldInfo merge(FieldInfo fieldInfo) {
            int size = (int) (fieldInfo.offset - this.offset + sizeOf(fieldInfo.type()));
            return new BatchFieldInfo(firstField, size);
        }

    }

    /**
     *
     */
    static class EnumFieldInfo extends FieldInfo {

        Field ordinalField;
        long ordinalOffset;
        Field valuesField;
        long valuesOffset;

        EnumFieldInfo(Field field, SerializationUtils.Serializer fieldSerializer) {
            super(field, fieldSerializer, null);
            offset = SerializationUtils.unsafe.objectFieldOffset(field);
            getOrdinalField();
        }

        private void getOrdinalField() {
            try {
                ordinalField = field.getType().getSuperclass().getDeclaredField("ordinal");
                valuesField = field.getType().getDeclaredField("$VALUES");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Failed to get field from Enum Class:" + e.getMessage());
            }

            ordinalOffset = SerializationUtils.unsafe.objectFieldOffset(ordinalField);
            valuesOffset = SerializationUtils.unsafe.staticFieldOffset(valuesField);
        }

        void serialize(UnsafeMemory unsafeMemory, Object object) {
            Object enumReference = SerializationUtils.unsafe.getObject(object, offset);
            fieldSerializer.serialize(unsafeMemory, enumReference, ordinalOffset);
        }

        void deserialize(UnsafeMemory unsafeMemory, Object object) {
            Object[] values = (Object[]) SerializationUtils.unsafe.getObject(field.getType(), valuesOffset);
            int ordinal = unsafeMemory.readInt();
            SerializationUtils.unsafe.putObject(object, offset, values[ordinal]);
        }
    }

    /**
     *
     */
    private final static class EnumVolatileFieldInfo extends EnumFieldInfo {

        EnumVolatileFieldInfo(Field field, SerializationUtils.Serializer fieldSerializer) {
            super(field, fieldSerializer);
        }

        void serialize(UnsafeMemory unsafeMemory, Object object) {
            Object enumReference = SerializationUtils.unsafe.getObjectVolatile(object, offset);
            fieldSerializer.serialize(unsafeMemory, enumReference, ordinalOffset);
        }

        void deserialize(UnsafeMemory unsafeMemory, Object object) {
            Object[] values = (Object[]) SerializationUtils.unsafe.getObject(field.getType(), valuesOffset);
            int ordinal = unsafeMemory.readInt();
            SerializationUtils.unsafe.putObjectVolatile(object, offset, values[ordinal]);
        }
    }

    /**
     *
     */
    final static class ClassInfo {
        final Class<?> clazz;
        final FieldInfo[] fieldInfos;

        ClassInfo(Class<?> clazz, FieldInfo[] fieldInfos) {
            this.clazz = clazz;
            this.fieldInfos = fieldInfos;
        }

        Object instance() throws InstantiationException {
            return SerializationUtils.unsafe.allocateInstance(clazz);
        }
    }
}
