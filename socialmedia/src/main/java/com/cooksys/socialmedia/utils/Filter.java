package com.cooksys.socialmedia.utils;

import com.cooksys.socialmedia.utils.interfaces.Deletable;

import java.util.List;

public class Filter {

    // This is so we can call filter on anything that implements Deletable
    // and get a list of only the items that are not deleted.
    public static <T extends Deletable> List<T> byNotDeleted(List<T> list) {
        return list.stream().filter(item -> !item.getDeleted()).toList();
    }
}
