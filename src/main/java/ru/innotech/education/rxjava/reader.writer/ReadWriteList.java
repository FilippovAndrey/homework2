package ru.innotech.education.rxjava.reader.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ReadWriteList<T>
        implements Collection<T> {
    private final List<T> list = new ArrayList<>();

    public static <T> ReadWriteList<T> create() {
        return null;
    }

    ReadWriteList() {
    }

    @Override
    @ReadOperation
    public int size() {
        return list.size();
    }

    @Override
    @ReadOperation
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    @ReadOperation
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return (cursor + 1) < list.size();
            }

            @Override
            @ReadOperation
            public T next() {
                return list.get(cursor++);
            }
        };
    }

    @Override
    @ReadOperation
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    @ReadOperation
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    @WriteOperation
    public boolean add(T t) {
        return list.add(t);
    }

    @Override
    @WriteOperation
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    @ReadOperation
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    @WriteOperation
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    @WriteOperation
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    @WriteOperation
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    @WriteOperation
    public void clear() {
        list.clear();
    }
}