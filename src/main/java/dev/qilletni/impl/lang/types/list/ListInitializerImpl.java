package dev.qilletni.impl.lang.types.list;

import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.conversion.TypeConverter;
import dev.qilletni.api.lang.types.list.ListInitializer;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.impl.lang.exceptions.TypeMismatchException;
import dev.qilletni.impl.lang.types.ListTypeImpl;

import java.util.List;

public class ListInitializerImpl implements ListInitializer {
    
    private final ListTypeTransformer listTypeTransformer;
    private final TypeConverter typeConverter;

    public ListInitializerImpl(ListTypeTransformer listTypeTransformer, TypeConverter typeConverter) {
        this.listTypeTransformer = listTypeTransformer;
        this.typeConverter = typeConverter;
    }

    @Override
    public ListType createList(List<QilletniType> items) {
        if (items.isEmpty()) {
            return ListTypeImpl.emptyList();
        }

        var typeList = items.stream().map(QilletniType::getTypeClass).distinct().toList();
        QilletniTypeClass<?> listType = QilletniTypeClass.ANY;
        if (!typeList.isEmpty()) {
            listType = typeList.getFirst();
        }

        return new ListTypeImpl(listType, items);
    }

    @Override
    public ListType createList(List<QilletniType> items, QilletniTypeClass<?> typeClass) {
        if (items.isEmpty()) {
            return ListTypeImpl.emptyList();
        }
        
        var transformedItems = items.stream().map(listItem -> {
            if (typeClass.isAssignableFrom(listItem.getTypeClass())) {
                return listItem;
            }

            return listTypeTransformer.transformType(typeClass, listItem);
        }).toList();

        return new ListTypeImpl(typeClass, transformedItems);
    }

    @Override
    public ListType createListFromJava(List<?> items) {
        if (items.isEmpty()) {
            return ListTypeImpl.emptyList();
        }

        var qilletniItems = items.stream().map(typeConverter::convertToQilletniType).toList();

        var typeList = qilletniItems.stream().map(QilletniType::getTypeClass).distinct().toList();
        QilletniTypeClass<?> listType = QilletniTypeClass.ANY;
        if (!typeList.isEmpty()) {
            listType = typeList.getFirst();
        }

        return new ListTypeImpl(listType, qilletniItems);
    }

    @Override
    public <T extends QilletniType> ListType createListFromJava(List<?> items, QilletniTypeClass<T> typeClass) {
        if (items.isEmpty()) {
            return ListTypeImpl.emptyList();
        }

        var qilletniItems = items.stream().map(listItem -> {
            // Convert to Qilletni type (e.g. a String to a StringType)
            var directQilletniType = typeConverter.convertToQilletniType(listItem);
            
            if (typeClass.isAssignableFrom(directQilletniType.getTypeClass())) {
                return directQilletniType;
            }
            
            // The Qilletni type may need another round of transformation (e.g. a StringType to a SongType)
            return listTypeTransformer.transformType(typeClass, directQilletniType);
        }).toList();

        var typeList = qilletniItems.stream().map(QilletniType::getTypeClass).distinct().toList();
        if (typeList.size() > 1) {
            throw new TypeMismatchException("Multiple types found in list");
        }

        return new ListTypeImpl(typeClass, qilletniItems);
    }
}
