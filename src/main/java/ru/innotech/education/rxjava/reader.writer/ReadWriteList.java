package ru.innotech.education.rxjava.reader.writer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ReadWriteList<T>
        implements Collection<T> {
    private final List<T> list = new ArrayList<>();

    public static <T> ReadWriteList<T> create() {
        /*IList rwl = (IList) Proxy.newProxyInstance(
                ReadWriteList.class.getClassLoader(),
                ReadWriteList.class.getInterfaces(),
                new AnnotationHandler(ReadWriteList.class));
        return (ReadWriteList<T>) rwl;*/
        Semaphore semaphore = new Semaphore(1);

        ReadWriteList<T> list = new ReadWriteList<>();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(list.getClass());
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                Object invoke = null;
                Method[] methods = ReadWriteList.class.getClass().getDeclaredMethods();
                Method method1 = Arrays.stream(methods).filter(t -> t.getName().equals(method.getName())).findFirst().orElse(method);
                //System.out.println("method 1: " + method1);
                //if(Arrays.stream(methods).findFirst().filter(t -> t.getName().equals(method.getName())).isPresent()) {
                //System.out.println("method in invoke: " + method.getName());
//      if (!method1.getName().equals(method.getName())) break;
                if (method1.getDeclaredAnnotation(ReadOperation.class) != null) { // TODO READ OPERATION LOGIC – несколько потоков могут выполнять этот метод параллельно.
                    System.out.println("ReadOperataion - " + method.getName());

                }
                if (method1.getDeclaredAnnotation(WriteOperation.class) != null) { // TODO WRITE OPERATION LOGIC – только один поток в один момент времени может выполнять этот метод.
                    System.out.println("WriteOperataion - " + method.getName());
                    semaphore.acquire();
                    invoke = method.invoke(list, args);
                    semaphore.release();
                }

                return invoke == null ? method.invoke(list, args) : invoke;
            }
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
