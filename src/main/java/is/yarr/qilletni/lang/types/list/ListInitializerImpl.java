package is.yarr.qilletni.lang.types.list;

import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lang.types.conversion.TypeConverter;
import is.yarr.qilletni.api.lang.types.list.ListInitializer;
import is.yarr.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.types.ListTypeImpl;
import is.yarr.qilletni.lang.types.TypelessListType;

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
            return new TypelessListType();
        }

        var typeList = items.stream().map(QilletniType::getTypeClass).distinct().toList();
        if (typeList.size() > 1) {
            throw new TypeMismatchException("Multiple types found in list");
        }

        return new ListTypeImpl(typeList.getFirst(), items);
    }

    @Override
    public ListType createList(List<QilletniType> items, QilletniTypeClass<?> typeClass) {
        if (items.isEmpty()) {
            return new TypelessListType();
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
    public ListType createListFromJava(List<Object> items) {
        if (items.isEmpty()) {
            return new TypelessListType();
        }

        var qilletniItems = items.stream().map(listItem -> {
            if (listItem instanceof QilletniType qilletniType) {
                return qilletniType;
            }

            return typeConverter.convertToQilletniType(listItem);
        }).toList();

        var typeList = qilletniItems.stream().map(QilletniType::getTypeClass).distinct().toList();
        if (typeList.size() > 1) {
            throw new TypeMismatchException("Multiple types found in list");
        }

        return new ListTypeImpl(typeList.getFirst(), qilletniItems);
    }

    @Override
    public <T extends QilletniType> ListType createListFromJava(List<Object> items, QilletniTypeClass<T> typeClass) {
        if (items.isEmpty()) {
            return new TypelessListType();
        }

        var qilletniItems = items.stream().map(listItem -> {
            if (listItem instanceof QilletniType qilletniType) {
                return qilletniType;
            }

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
