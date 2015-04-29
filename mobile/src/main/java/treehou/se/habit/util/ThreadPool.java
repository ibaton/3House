package treehou.se.habit.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ibaton on 2015-04-29.
 */
public class ThreadPool {

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService instance(){
        return executorService;
    }
}
