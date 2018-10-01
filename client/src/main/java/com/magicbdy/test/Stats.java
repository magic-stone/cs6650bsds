package com.magicbdy.test;

import java.util.ArrayList;
import java.util.List;

public class Stats {
    private List<Long> latencyList = new ArrayList<>();
    private int requestCount = 0;
    private int successCount = 0;

    public void add (long latency) {
        latencyList.add(latency);
    }

    public void requestAdd () {
        requestCount++;
    }

    public void successAdd() {
        successCount++;
    }

    public List<Long> getLatencyList() {
        return latencyList;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getLatencySum() {
        int latencySum = 0;
        for (int i = 0; i < latencyList.size(); i++) {
            latencySum += latencyList.get(i);
        }
        return latencySum;
    }
}
