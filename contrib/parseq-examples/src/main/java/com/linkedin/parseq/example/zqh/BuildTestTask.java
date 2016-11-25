package com.linkedin.parseq.example.zqh;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

/**
 * Created by zqh on 2016/11/23
 */
public class BuildTestTask {

    public static Task<String> fetchTestTask(String name) {
        Task<String> test = Task.async(() -> {
            final SettablePromise<String> result = Promises.settable();
            try {
                System.out.println(Thread.currentThread().getId() + "\t" + name);
                Thread.sleep(1000);
                result.done(name + " done");
            } catch (Exception ex) {
                result.fail(ex);
            }
            return result;
        });
        return test;
    }

}
