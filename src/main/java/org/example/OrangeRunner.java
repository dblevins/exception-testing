/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.example;

import org.tomitribe.util.PrintString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class OrangeRunner {

    final CountDownLatch ready;
    final CountDownLatch go = new CountDownLatch(1);
    final CountDownLatch completion;

    public OrangeRunner(final int count) {
        ready = new CountDownLatch(count);
        completion = new CountDownLatch(count);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService pool = Executors.newFixedThreadPool(20);
        try {
            go(pool);
        } finally {
            pool.shutdown();
        }
    }

    private static void go(final ExecutorService pool) throws InterruptedException, ExecutionException {
        final int count = 10000;
        final OrangeRunner runner = new OrangeRunner(count);

        final long start = System.nanoTime();
        final List<Future<Result>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            futures.add(pool.submit(() -> runner.run(new Orange(20, 5))));
        }

        runner.completion.await();
        final long elapsed = System.nanoTime() - start;

        final List<Result> results = new ArrayList<>();
        for (final Future<Result> future : futures) {
            results.add(future.get());
        }

        final long linear = results.stream()
                .mapToLong(Result::getNanoseconds)
                .sum();

        System.out.printf("real time  : %s nanoseconds%n", elapsed);
        System.out.printf("linear time: %s nanoseconds%n", linear);

        // pause so we can take a heap dump
        new Semaphore(0).acquire();
    }

    public Result run(final Orange orange) {
        final long start = System.nanoTime();
        try {
            orange.method(100);
            throw new IllegalStateException();
        } catch (OrangeException e) {
            e.printStackTrace(new PrintString());
            return new Result(e, System.nanoTime() - start);
        } finally {
            completion.countDown();
        }
    }
}
