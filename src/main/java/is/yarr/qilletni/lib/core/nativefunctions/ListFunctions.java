package is.yarr.qilletni.lib.core.nativefunctions;

import is.yarr.qilletni.lang.exceptions.TypeMismatchException;
import is.yarr.qilletni.lang.internal.NativeOn;
import is.yarr.qilletni.lang.types.IntType;
import is.yarr.qilletni.lang.types.ListType;
import is.yarr.qilletni.lang.types.QilletniType;

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
            throw new TypeMismatchException("Cannot add lists of items of mismatched types (" + otherList.getSubType().getTypeName() + " to " + list.getSubType().getTypeName() + ")");
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
        return new ListType(list.getSubType(), subList);
    }
    
}
