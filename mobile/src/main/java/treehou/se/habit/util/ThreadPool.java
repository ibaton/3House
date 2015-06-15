package treehou.se.habit.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private ThreadPool() {}

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService instance(){
        return executorService;
    }
}
