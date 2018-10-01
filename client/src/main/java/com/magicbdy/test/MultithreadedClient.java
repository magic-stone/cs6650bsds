package com.magicbdy.test;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultithreadedClient {
    private int numOfThreads;
    private int numOfIterations;
    private String url;
    private List<Stats> statsList;

    public MultithreadedClient(int threads, int iterations, String url, List<Stats> statsList) {
        this.numOfThreads = threads;
        this.numOfIterations = iterations;
        this.url = url;
        this.statsList = statsList;
    }

    public void multiRequest() {
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        Long startTime = System.currentTimeMillis();
        System.out.println(String.format("Client start at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(startTime)));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        for(int i = 0; i < numOfThreads; i++){
            Stats currentStat = new Stats();
            statsList.add(currentStat);
            SingleThreadClient individual = new SingleThreadClient(target, numOfIterations, currentStat);
            executor.execute(individual);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Something wrong with the multithreaded client");
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(String.format("All threads complete at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(endTime)));
        Long wallTime = endTime - startTime;
        System.out.println("Wall Time: " + wallTime + " ms");
    }

}
