package com.smart_learn.core;

import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Getter
public class ThreadPoolService {

    private final ExecutorService pool = Executors.newCachedThreadPool();

    @PostConstruct
    private void init(){
        setNewPoolSize(10);
    }

    public void execute(Runnable task){
        pool.execute(task);
    }

    public void shutdownPool(){
        pool.shutdown();
    }

    // TODO: make this task a scheduled task and can check how many connection are active at some moment
    public synchronized void setNewPoolSize(int newSize){
        ((ThreadPoolExecutor)pool).setMaximumPoolSize(newSize);
    }

}