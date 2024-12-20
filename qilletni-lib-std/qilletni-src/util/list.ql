/**
 * Gets the size of the list.
 *
 * @returns[@type int] The number of items in the list
 */
native fun size() on list

/**
 * Checks if the list is empty.
 *
 * @returns[@type boolean] If the list is empty
 */
fun isEmpty(lst) on list {
    return lst.size() == 0
}

/**
 * Adds an item to the list.
 *
 * @param item The item to add
 */
native fun add(item) on list

/**
 * Adds an item to the list at the specified index.
 *
 * @param index The index to add the item at
 * @param item  The item to add
 * @errors If the index is out of bounds
 */
 
native fun add(index, item) on list

/**
 * Adds all items of the given list to the current list.
 *
 * @param itemList The items to add
 */
 
native fun addAll(itemList) on list

/**
 * Removes an item from the list at the given index.
 *
 * @param index The index of the item to remove
 * @returns The item that was removed
 * @errors If the index is out of bounds
 */
native fun remove(index) on list

/**
 * Checks if the given item is contained in the list.
 *
 * @param index The index of the item to remove
 * @returns[@type boolean] If the specified item is in the list
 */
native fun contains(item) on list

/**
 * Returns a portion of the current list from the specified starting index to the end of the list.
 *
 * @param fromIndex The inclusive starting index of the list
 * @param toIndex The exclusive ending index of the list
 * @returns[@type list] The created sublist
 */
native fun subList(item) on list

/**
 * Checks the index of the given item in the list.
 *
 * @param item The item to check the location of
 * @returns[@type int] The index of the item, or -1 if it is not in the list
 */
native fun indexOf(item) on list

/**
 * Joins the items of the list into a single string.
 *
 * @param delimiter The string to join the items with
 * @returns[@type string] The joined string
 */
native fun join(delimiter) on list
