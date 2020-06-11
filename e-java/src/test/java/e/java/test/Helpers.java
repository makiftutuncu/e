package e.java.test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Helpers {
    private Helpers() {}

    @SafeVarargs
    public static <A> List<A> listOf(A... as) {
        return Arrays.asList(as);
    }

    public static <K, V> AbstractMap.SimpleImmutableEntry<K, V> mapEntry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(AbstractMap.SimpleImmutableEntry<K, V>... entries) {
        return Stream.of(entries).collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, AbstractMap.SimpleImmutableEntry::getValue));
    }

    public static <A> List<A> added(List<A> l, A a) {
        List<A> list = new ArrayList<>(l);
        list.add(a);
        return list;
    }

    public static <K, V> Map<K, V> added(Map<K, V> m, K key, V value) {
        Map<K, V> map = new HashMap<>(m);
        map.put(key, value);
        return map;
    }
}
