package com.cooksys.socialmedia.utils;

import com.cooksys.socialmedia.utils.interfaces.Deletable;

import java.util.List;

public class Filter {

    public static <T extends Deletable> List<T> byNotDeleted(List<T> list) {
        return list.stream().filter(item -> !item.getDeleted()).toList();
    }
}
