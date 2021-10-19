package ru.innotech.education.rxjava.reader.writer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class ReadWriteListThreadTest {
    private static final int READ_THREAD_COUNT = 10;
    private static final int WRITE_THREAD_COUNT = 20;
    private static final int REMOVE_THREAD_COUNT = 10;
    private static final int DURATION = 10;
    private static final int ITERATIONS = 250;

    @Test
    void testParallel()
            throws InterruptedException {
        final ReadWriteList<Integer> list = ReadWriteList.create();

        final List<Thread> readThreads = buildThreads(new Read(list, ITERATIONS), READ_THREAD_COUNT, "read");
        final List<Thread> writeThreads = buildThreads(new Write(list, ITERATIONS), WRITE_THREAD_COUNT, "write");
        final List<Thread> removeThread = buildThreads(new Remove(list, ITERATIONS), REMOVE_THREAD_COUNT, "remove");

        writeThreads.forEach(Thread::start);
        Thread.sleep(50);
        readThreads.forEach(Thread::start);
        removeThread.forEach(Thread::start);

        join(readThreads);
        join(writeThreads);
        join(removeThread);

        int itemsInserted = ITERATIONS * WRITE_THREAD_COUNT;
        int itemsRemoved = ITERATIONS * REMOVE_THREAD_COUNT;
        assertThat(list.size()).isEqualTo(itemsInserted - itemsRemoved);
    }

    @SneakyThrows
    private void join(List<Thread> threads) {
        for (final Thread thread : threads) {
            thread.join();
        }
    }

    private List<Thread> buildThreads(Runnable runnable, int threadCount, String name) {
        return range(0, threadCount)
                .mapToObj(i -> new Thread(runnable, name + "-" + i))
                .collect(toList());
    }

    @RequiredArgsConstructor
    private static class Remove
            implements Runnable {
        private final ReadWriteList<Integer> list;
        private final int iterations;

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                final int iteration = i;
                synchronized (this) {
                    list.stream()
                        .findFirst()
                        .ifPresent(item -> {
                            list.remove(item);
                            System.out.println("Thread '" + Thread.currentThread()
                                                                  .getName() + "' iteration " + iteration);
                            sleep();
                        });
                }
            }
        }

        @SneakyThrows
        void sleep() {
            Thread.sleep(DURATION + (new Random().nextInt(10) - 5));
        }
    }

    @RequiredArgsConstructor
    private static class Write
            implements Runnable {
        private final ReadWriteList<Integer> list;
        private final int iterations;
        private int counter = 0;

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                list.add(counter++);
                System.out.println("Thread '" + Thread.currentThread()
                                                      .getName() + "' iteration " + i);
                sleep();
            }
        }

        @SneakyThrows
        void sleep() {
            Thread.sleep(DURATION + (new Random().nextInt(10) - 5));
        }
    }

    @RequiredArgsConstructor
    private static class Read
            implements Runnable {
        private final ReadWriteList<Integer> list;
        private final int iterations;

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                final List<Integer> newList = new ArrayList<>();
                for (final Integer item : list) {
                    newList.add(item);
                }
                System.out.println("Thread '" + Thread.currentThread()
                                                      .getName() + "' iteration " + i + ", size " + newList.size());
                sleep();
            }
        }

        @SneakyThrows
        void sleep() {
            Thread.sleep(DURATION + (new Random().nextInt(10) - 5));
        }
    }
}