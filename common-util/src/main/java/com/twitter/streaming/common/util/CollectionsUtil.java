package com.twitter.streaming.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * initialization on demand holder idiom to avoid synchronization overhead
 * singleton pattern
 */
public class CollectionsUtil {
    private CollectionsUtil() {
    }

    private static class CollectionsUtilHolder {
        private static final CollectionsUtil INSTANCE = new CollectionsUtil();
    }

    public static CollectionsUtil getInstance() {
        return CollectionsUtilHolder.INSTANCE;
    }

    public <T> List<T> getListFromIterable(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
