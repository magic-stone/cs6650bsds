package com.magicbdy.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Stats {
    private int numOfAllRequests;
    private long totalLatency;
    private boolean isSorted;
    private List<Long> latencies;
    private Map<Long, Long> timeToLatencies;

    public Stats() {
        this.numOfAllRequests = 0;
        this.totalLatency =0;
        this.isSorted = false;
        this.latencies = new ArrayList<>();
        this.timeToLatencies = new HashMap<>();
    }

    public void addLatency(long latency) {
        totalLatency += latency;
        latencies.add(latency);
    }

    public void addRequest() {
        numOfAllRequests += 1;
    }


    public int getNumOfAllRequests() {
        return numOfAllRequests;
    }

    public long getTotalLatency() {
        return totalLatency;
    }

    public boolean isSorted() {
        return isSorted;
    }

    public List<Long> getLatencies() {
        return latencies;
    }

    public Map<Long, Long> getTimeToLatencies() {
        return timeToLatencies;
    }

    public void addLatencyMapping(long cur_time, long cur_latency) {
        timeToLatencies.put(cur_time, cur_latency);
    }

    public long getMeanLatency() {
        return totalLatency / numOfAllRequests;
    }

    public long getMedianLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }

        return latencies.get(latencies.size() / 2);
    }

    public long get95thLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }

        return latencies.get((int)Math.floor(latencies.size() * 0.95));
    }

    public long get99thLatency() {
        if (!isSorted) {
            Collections.sort(latencies);
            isSorted = true;
        }

        return latencies.get((int)Math.floor(latencies.size() * 0.99));
    }


}
