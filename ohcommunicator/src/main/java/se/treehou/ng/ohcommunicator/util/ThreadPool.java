package se.treehou.ng.ohcommunicator.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private ThreadPool() {}

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService instance(){
        return executorService;
    }
}
