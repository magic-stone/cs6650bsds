package com.magicbdy.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainHandler {
    public static void main(String[] args) {
        String ip = "localhost";
        String port = "8080";
        int numOfThreads = 100;
        int numOfIterations = 100;

        if (args.length == 4) {
            numOfThreads = Integer.parseInt(args[0]);
            numOfIterations = Integer.parseInt(args[1]);
            ip = args[2];
            port = args[3];
        }

        System.out.println("thread number is: " + numOfThreads);
        System.out.println("iteration number is: " + numOfIterations);

        String url = "http://" + ip + ":" + port + "/api";
        System.out.println("url=" + url);
        List<Stats> statsList = new ArrayList<>();

        System.out.println("--------------------<>------------------");
        System.out.println("Warmup phase!");
        System.out.println("--------------------<>------------------");
        MultithreadedClient clients = new MultithreadedClient(numOfThreads / 10, numOfIterations, url, statsList);
        clients.multiRequest();
        System.out.println("Warmup phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Loading phase!");
        System.out.println("--------------------<>------------------");
        clients = new MultithreadedClient(numOfThreads / 2, numOfIterations, url, statsList);
        clients.multiRequest();
        System.out.println("Loading phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Peak phase!");
        System.out.println("--------------------<>------------------");
        clients = new MultithreadedClient(numOfThreads, numOfIterations, url, statsList);
        clients.multiRequest();
        System.out.println("Peak phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Cooldown phase!");
        System.out.println("--------------------<>------------------");
        clients = new MultithreadedClient(numOfThreads / 4, numOfIterations, url, statsList);
        clients.multiRequest();
        System.out.println("Cooldown phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Overall analysis");
        System.out.println("--------------------<>------------------");
        getStats(statsList);
    }

    public static void getStats(List<Stats> statsList) {

        int numRequest = 0;
        int numResponse = 0;
        int latencySum = 0;
        List<Long> latencyList = new ArrayList<Long>();
        for (int i = 0; i < statsList.size(); i++) {
            numRequest += statsList.get(i).getRequestCount();
            numResponse += statsList.get(i).getSuccessCount();
            latencyList.addAll(statsList.get(i).getLatencyList());
            latencySum += statsList.get(i).getLatencySum();
        }
        System.out.println("Total number of request sent: " + numRequest);
        System.out.println("Total number of successful responses: " + numResponse);

        Long[] latencyArray = latencyList.toArray(new Long[latencyList.size()]);
        int size = latencyArray.length;

        double meanLatency = latencySum / size;
        System.out.println("Mean latencies for all requests is: " + meanLatency + " ms");

        Arrays.sort(latencyArray);
        double mediaLatency;
        if (size %2 == 0) {
            mediaLatency = (latencyArray[size / 2 - 1] + latencyArray[size / 2 ]) / 2;
        } else {
            mediaLatency = latencyArray[size / 2];
        }
        System.out.println("Median latencies for all request is: " + mediaLatency + " ms");

        long p99 = latencyArray[(int) (0.99 * size - 1)];
        long p95 = latencyArray[(int) (0.95 * size - 1)];

        System.out.println("95th percentile latency is: " + p95 + " ms");
        System.out.println("99th percentile latency is: " + p99 + " ms");
        System.out.println("task done");

    }
}
