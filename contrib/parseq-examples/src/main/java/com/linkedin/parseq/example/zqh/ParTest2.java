package com.linkedin.parseq.example.zqh;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.introduction.IntroductoryExample;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

/**
 * Created by zqh on 2016/11/25
 */
public class ParTest2 extends AbstractExample {
    public static void main(String[] args) throws Exception {
        new ParTest2().runExample();
    }

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

    @Override
    protected void doRunExample(final Engine engine) throws Exception {

        Task<String> zqh1 = fetchTestTask("zqh1");
        Task<String> zqh2 = fetchTestTask("zqh2");
        Task<String> zqh3 = fetchTestTask("zqh3");
        Task<String> zqh4 = fetchTestTask("zqh4");

        final Task<Integer> sumLengths =
                Task.par(zqh1.map("length", s -> s.length()),
                        zqh2.map("length", s -> s.length()),
                        zqh3.map("length",s -> s.length()))
                        .map("sum", (g, y, b) -> g + y + b);

        engine.run(sumLengths);

        sumLengths.await();

        ExampleUtil.printTracingResults(sumLengths);
    }
}
