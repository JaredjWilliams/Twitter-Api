package com.cooksys.socialmedia.utils;

import com.cooksys.socialmedia.utils.interfaces.Sortable;

import java.util.Comparator;
import java.util.List;

public class Sort {

    public static <T extends Sortable> List<T> filterNotDeletedAndSortDesc(List<T> list) {
        return Filter.byNotDeleted(list.stream().sorted(Comparator.comparing(Sortable::getPosted).reversed()).toList());
    }
}
