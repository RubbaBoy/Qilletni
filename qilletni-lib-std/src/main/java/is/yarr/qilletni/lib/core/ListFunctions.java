package is.yarr.qilletni.lib.core;

import is.yarr.qilletni.api.lang.types.ListType;
import is.yarr.qilletni.api.lang.types.QilletniType;
import is.yarr.qilletni.api.lib.NativeOn;

import java.util.ArrayList;
import java.util.Collections;

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
            throw new RuntimeException("Cannot add lists of items of mismatched types (" + otherList.getSubType().getTypeName() + " to " + list.getSubType().getTypeName() + ")");
        }
        
        var mutableItems = new ArrayList<>(list.getItems());
        mutableItems.addAll(otherList.getItems());
        list.setItems(mutableItems);
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
    
}
