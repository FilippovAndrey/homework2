package ru.innotech.education.rxjava.reader.writer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadWriteList<T>
        implements Collection<T> {
    private final List<T> list = new ArrayList<>();

    public static <T> ReadWriteList<T> create() {

        final Semaphore semaphore = new Semaphore(1);
        final AtomicInteger countReader = new AtomicInteger(0);

        final ReadWriteList<T> list = new ReadWriteList<>();
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(list.getClass());
        enhancer.setCallback((InvocationHandler) (proxy, method, args) -> {
            Object invoke = null;
            final Method[] methods = ReadWriteList.class.getDeclaredMethods();
            final Method originMethod = Arrays.stream(methods)
                    .filter(t -> t.getName().equals(method.getName()))
                    .findFirst()
                    .orElse(method);

            if (originMethod.getDeclaredAnnotation(ReadOperation.class) != null) { // – несколько потоков могут выполнять этот метод параллельно.
                countReader.incrementAndGet();
                if(countReader.get() == 1) {
                    semaphore.acquire();
                }

                invoke = method.invoke(list, args);

                countReader.decrementAndGet();
                if(countReader.get() == 0) {
                    semaphore.release();
                }
            }
            if (originMethod.getDeclaredAnnotation(WriteOperation.class) != null) { // – только один поток в один момент времени может выполнять этот метод.
                semaphore.acquire();
                invoke = method.invoke(list, args);
                semaphore.release();
            }

            return invoke == null ? method.invoke(list, args) : invoke;
        });
        return (ReadWriteList<T>) enhancer.create();
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
