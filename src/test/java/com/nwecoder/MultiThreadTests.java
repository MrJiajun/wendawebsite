package com.nwecoder;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/5/6 0006.
 */
class MyThread extends Thread{
    private int tid;

    public MyThread(int tid){
        this.tid = tid;
    }

    @Override
    public void run() {
            try{
                for(int i = 0; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println(String.format("%d : %d", tid, i));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
}

class Consumer implements Runnable{
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try{
            while (true){
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable{
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try{
            for(int i = 0; i < 100; i++){
                Thread.sleep(10);
                q.put(String.valueOf(i));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}


public class MultiThreadTests {

    public static void testThread1(){
        for(int i =0; i < 10; i++){
            new MyThread(i).start();
        }
    }

    public static void testThread2(){
        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; ++j) {
                            Thread.sleep(1000);
                            System.out.println(String.format("T2 %d: %d:", finalI, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static Object obj = new Object();

    public static void testSynchronized1(){
        synchronized (obj){
            try{
                for(int j = 0; j < 10; ++j){
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 %d",  j));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2(){
        synchronized (obj){
            try{
                for(int j = 0; j < 10; ++j){
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 %d",  j));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized(){
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue(){
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }


    //

    //
    public static int userId;
    private static ThreadLocal<Integer> threadLocalUserId = new ThreadLocal<>();

    public static void testThreadLoacl(){
        for(int i = 0; i < 10; i++){
            final int fianlId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocalUserId.set(fianlId);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal: " + threadLocalUserId.get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for(int i = 0; i < 10; i++){
            final int fianlId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userId = fianlId;
                        Thread.sleep(1000);
                        System.out.println("UserId: " + userId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //单线程的
    public static void teatExecutor1(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1: " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2: " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.shutdown();
    }

    //多线程的
    public static void teatExecutor2(){
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1: " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2: " + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.shutdown();
        while (!service.isTerminated()){
            try {
                Thread.sleep(1000);
                System.out.println("Wait for termination.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void testWithoutAtomic(){
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(100);
                        for(int j = 0; j < 10; j++){
                            counter++;
                            System.out.println(counter);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testWithAtomic(){
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(100);
                        for(int j = 0; j < 10; j++){

                            System.out.println(atomicInteger.incrementAndGet());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void testAtomic(){
        //testWithoutAtomic();
        testWithAtomic();//原子性操作
    }

    public static void tsetFuture(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception{
                Thread.sleep(1000);
                throw new IllegalArgumentException("异常");
                //return 1;
            }
        });

        service.shutdown();

        try{
           // System.out.println(future.get());
            System.out.println(future.get(100, TimeUnit.MILLISECONDS));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] argv){
        //testThread1();
        //testThread2();
        //testSynchronized();
        //testBlockingQueue();//异步处理
        //testThreadLoacl();
        //teatExecutor1();
        //teatExecutor2();
        //testAtomic();
        tsetFuture();
    }
}
