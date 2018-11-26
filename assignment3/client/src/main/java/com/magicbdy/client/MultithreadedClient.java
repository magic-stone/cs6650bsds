package com.magicbdy.client;

import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class MultithreadedClient {
    private int numOfThreads;
    private int phaseID;
    private String url;
    private List<Stats> statsList;
    private int numOfTests = 100;

    public MultithreadedClient(int threads, int phaseID, String url, List<Stats> statsList) {
        this.numOfThreads = threads;
        this.url = url;
        this.statsList = statsList;
        this.phaseID = phaseID;
    }

    public void multiRequest() {
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        ClientConfig config = new ClientConfig();
        config.property(ClientProperties.READ_TIMEOUT, 40000);
        config.property(ClientProperties.CONNECT_TIMEOUT, 20000);
        config.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);

        ApacheConnector connector = new ApacheConnector(config);
        config.connector(connector);
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(url);
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        Long startTime = System.currentTimeMillis();
        System.out.println(String.format("Client start at: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(startTime)));

        CountDownLatch latch = new CountDownLatch(numOfThreads - 2);
        for(int i = 0; i < numOfThreads; i++){
            Stats currentStat = new Stats();
            statsList.add(currentStat);
            int numOfIteration = getNumOfIteration();
            SingleThreadClient individual = new SingleThreadClient(target, phaseID, numOfIteration,currentStat, i, latch);
            executor.execute(individual);
            latch.countDown();
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Something wrong with the multithreaded client");
        }
        Long endTime = System.currentTimeMillis();
        Long wallTime = endTime - startTime;
        System.out.println("Wall Time: " + wallTime + " ms");

    }

    private int getNumOfIteration() {
        switch (phaseID) {
            case 1 :
                return numOfTests * 3;
            case 2:
                return numOfTests * 5;
            case 3:
                return numOfTests * 11;
            default:
                return numOfTests * 5;
        }
    }

}
