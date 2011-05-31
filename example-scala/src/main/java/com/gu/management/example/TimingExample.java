package com.gu.management.example;

import java.util.concurrent.Callable;

public class TimingExample {
    public void doSomethingInteresting() {
        TimingMetrics.downtime().call(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "hello";
            }
        });
    }
}
