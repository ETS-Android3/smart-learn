package com.smart_learn.core.services.helpers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Use to manage thread pool.
 * */
public class ThreadExecutorService {

    private static final int INITIAL_POOL_SIZE = 10;

    private static ThreadExecutorService instance;

    private final ExecutorService executorService;

    private ThreadExecutorService() {
        executorService = Executors.newCachedThreadPool();
        setNewPoolSize(INITIAL_POOL_SIZE);
    }

    public static ThreadExecutorService getInstance() {
        if(instance == null){
            instance = new ThreadExecutorService();
        }
        return instance;
    }

    /**
     * Use to execute task in a new thread.
     *
     * @param task Task to be executed.
     * */
    public void execute(Runnable task){
        executorService.execute(task);
    }

    public void shutdownPool(){
        executorService.shutdown();
    }

    public synchronized void setNewPoolSize(int newSize) {
        // TODO: make this task a scheduled task and can check how many threads are needed at some moment
        ((ThreadPoolExecutor) executorService).setMaximumPoolSize(newSize);
    }
}