package com.linkedin.parseq.example.zqh;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tuple2Task;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zqh on 2016/11/23
 */
public class TestApp1 {

    public static Task<String> fetchTestTask(String name) {
        Task<String> test = Task.async(name,() -> {
            final SettablePromise<String> result = Promises.settable();
            try {
//                System.out.println(Thread.currentThread().getId() + "\t" + name);
                long i=0;

                while(i++<10000000);

                result.done(name + " done");
            } catch (Exception ex) {
                result.fail(ex);
            }
            return result;
        });
        return test;
    }

    public static void main(String[] args) throws InterruptedException {

//        final int numCores = Runtime.getRuntime().availableProcessors();
//        final ExecutorService taskScheduler = Executors.newFixedThreadPool(10);
//        final ScheduledExecutorService timerScheduler = Executors.newSingleThreadScheduledExecutor();
//        System.out.println(Thread.currentThread().getId() + "\t main thread");


        final int numCores = Runtime.getRuntime().availableProcessors();
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numCores + 1);
        final EngineBuilder builder = new EngineBuilder().setTaskExecutor(scheduler).setTimerScheduler(scheduler);
        System.out.println(Thread.currentThread().getId() + "\t main thread");

        final Engine engine = builder.build();



//        final Engine engine = new EngineBuilder()
//                .setTaskExecutor(taskScheduler)
//                .setTimerScheduler(timerScheduler)
//                .build();

        Task<String> zqh1 = fetchTestTask("zqh1");
        Task<String> zqh2 = fetchTestTask("zqh2");
        Task<String> zqh3 = fetchTestTask("zqh3");
        Task<String> zqh4 = fetchTestTask("zqh4");

        final Task<String> sumLengths=Task.par(zqh1,zqh2,zqh3,zqh4).map("sum",(p1,p2,p3,p4)->p1+p2+p3+p4);



        engine.run(sumLengths);

        sumLengths.await();


        System.out.println(sumLengths.getTrace());
        System.out.println(sumLengths.get());
        engine.shutdown();
        try {
            engine.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.shutdown();
        scheduler.shutdown();

    }

}
