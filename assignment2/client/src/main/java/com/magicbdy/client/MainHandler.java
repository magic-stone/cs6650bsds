package com.magicbdy.client;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MainHandler {
    public static void main(String[] args) throws FileNotFoundException {
        String ip = "distribute-460644142.us-west-2.elb.amazonaws.com";
        String ip2 = "localhost";
        String port = "8080";
        int numOfThreads = 128;

        System.out.println("thread number is: " + numOfThreads);

        String url = "http://" + ip + ":" + port + "/1234/api";
        System.out.println("url=" + url);
        List<Stats> statsList = new ArrayList<>();

        System.out.println("--------------------<>------------------");
        System.out.println("Warmup phase!");
        System.out.println("--------------------<>------------------");
        MultithreadedClient clients = new MultithreadedClient(numOfThreads / 10, 1, url, statsList);
        clients.multiRequest();
        System.out.println("Warmup phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Loading phase!");
        System.out.println("--------------------<>------------------");
        clients = new MultithreadedClient(numOfThreads / 2, 2, url, statsList);
        clients.multiRequest();
        System.out.println("Loading phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Peak phase!");
        System.out.println("--------------------<>------------------");
        clients = new MultithreadedClient(numOfThreads, 3, url, statsList);
        clients.multiRequest();
        System.out.println("Peak phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Cooldown phase!");
        System.out.println("--------------------<>------------------");
        clients = new MultithreadedClient(numOfThreads / 4, 4, url, statsList);
        clients.multiRequest();
        System.out.println("Cooldown phase completed!");

        System.out.println("--------------------<>------------------");
        System.out.println("Overall analysis");
        System.out.println("--------------------<>------------------");
        getStats(statsList);
    }

    public static void getStats(List<Stats> statsList) throws FileNotFoundException {
        int numRequest = 0;
        int latencySum = 0;
        Map<Long, Integer> timeToRequestMapping = new HashMap<>();
        List<Long> secondsIntervalList = new ArrayList<>();
        List<Long> latencyList = new ArrayList<Long>();
        for (int i = 0; i < statsList.size(); i++) {
            numRequest += statsList.get(i).getNumOfAllRequests();
            latencyList.addAll(statsList.get(i).getLatencies());
            latencySum += statsList.get(i).getTotalLatency();
            Map<Long, Long> timeToLatencies = statsList.get(i).getTimeToLatencies();
            // calculate number of requests at each second interval
            for (long timestamp : timeToLatencies.keySet()) {
                double second = timestamp / 1000.0;
                long time = (long) second;
                if (timeToRequestMapping.containsKey(time)) {
                    timeToRequestMapping.put(time, timeToRequestMapping.get(time) + 1);
                } else {
                    timeToRequestMapping.put(time, 1);
                    secondsIntervalList.add(time);
                }
            }
        }

        // output the stats for number of requests per second interval
        PrintWriter pw = new PrintWriter(new File("stats.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append("SecondInterval");
        sb.append(',');
        sb.append("RequestCount");
        sb.append('\n');
        Collections.sort(secondsIntervalList);
        Long startSeconds = secondsIntervalList.get(0);
        for (long sec : secondsIntervalList) {
            long cur_x_index = sec - startSeconds;
            long cur_y_index = timeToRequestMapping.get(sec);
            sb.append(Long.toString(cur_x_index));
            sb.append(',');
            sb.append(Long.toString(cur_y_index));
            sb.append('\n');
        }
        pw.write(sb.toString());
        pw.close();

        System.out.println("Total number of request sent: " + numRequest);

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
