package dev.qilletni.lib.core;

import dev.qilletni.api.lang.types.IntType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.StringType;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.api.lib.annotations.NativeOn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NativeOn("list")
public class ListFunctions {

    public static int size(ListType list) {
        return list.getItems().size();
    }

    public static void clear(ListType list) {
        list.setItems(Collections.emptyList());
    }

    public static void add(ListType list, QilletniType item) {
        var mutableItems = new ArrayList<>(list.getItems());
        mutableItems.add(item);
        list.setItems(mutableItems);
    }
    
    public static void add(ListType list, int index, QilletniType item) {
        var mutableItems = new ArrayList<>(list.getItems());
        mutableItems.add(index, item);
        list.setItems(mutableItems);
    }

    public static void addAll(ListType list, ListType otherList) {
        if (!list.getSubType().equals(otherList.getSubType())) {
            throw new RuntimeException("Cannot add lists of items of mismatched types (%s to %s)".formatted(otherList.getSubType().getTypeName(), list.getSubType().getTypeName()));
        }
        
        var mutableItems = new ArrayList<>(list.getItems());
        mutableItems.addAll(otherList.getItems());
        list.setItems(mutableItems);
    }

    public static QilletniType remove(ListType list, int index) {
        var mutableItems = new ArrayList<>(list.getItems());
        var removedItem = mutableItems.remove(index);
        list.setItems(mutableItems);
        
        return removedItem;
    }

    public static boolean contains(ListType list, QilletniType object) {
        return list.getItems().contains(object);
    }

    public static ListType subList(ListType list, int fromIndex, int toIndex) {
        var subList = list.getItems().subList(fromIndex, toIndex);
        var listCopy = list.copy();
        listCopy.setItems(subList);
        return listCopy;
    }

    public static int indexOf(ListType list, QilletniType object) {
        return list.getItems().indexOf(object);
    }

    public static String join(ListType list, String delimiter) {
        return list.getItems().stream().map(QilletniType::stringValue).collect(Collectors.joining(delimiter));
    }
    
    public static ListType sort(ListType list) {
        var contents = sortListContents(list);
        list.setItems(contents);
        return list;
    }
    
    public static ListType sortReverse(ListType list) {
        var contents = sortListContents(list);
        Collections.reverse(contents);
        list.setItems(contents);
        return list;
    }
    
    private static List<QilletniType> sortListContents(ListType list) {
        if (list.getSubType().equals(QilletniTypeClass.STRING)) {
            return list.getItems().stream()
                    .map(StringType.class::cast)
                    .sorted(Comparator.comparing(StringType::getValue))
                    .map(QilletniType.class::cast)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ArrayList::new));
        } else if (list.getSubType().equals(QilletniTypeClass.INT)) {
            return list.getItems().stream()
                    .map(IntType.class::cast)
                    .sorted(Comparator.comparing(IntType::getValue))
                    .map(QilletniType.class::cast)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), ArrayList::new));
        }

        throw new RuntimeException("Cannot sort list of type %s".formatted(list.getSubType().getTypeName()));
    }
}
