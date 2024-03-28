package com.cooksys.socialmedia.utils;

import com.cooksys.socialmedia.utils.interfaces.Sortable;

import java.util.Comparator;
import java.util.List;

public class Sort {

    // This is so we can call filterNotDeletedAndSortDesc on anything that implements Sortable
    // and get a list of only the items that are not deleted and sorted in descending order.
    public static <T extends Sortable> List<T> filterNotDeletedAndSortDesc(List<T> list) {
        return Filter.byNotDeleted(list.stream().sorted(Comparator.comparing(Sortable::getPosted).reversed()).toList());
    }
}
