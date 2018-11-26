package com.magicbdy.client;

import com.magicbdy.client.model.WearableData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Run the client HTTP Get and Post request in a single thread
 */
public class SingleThreadClient extends Thread {

    private int phaseID;
    private int numOfIterations;
    private WebTarget target;
    private Stats statistics;
    private int threadID;
    private CountDownLatch latch;

    public SingleThreadClient(WebTarget target, int phaseID, int numOfIterations, Stats stats, int threadID, CountDownLatch latch) {
        this.phaseID = phaseID;
        this.target = target;
        this.numOfIterations = numOfIterations;
        this.statistics = stats;
        this.threadID = threadID;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await();
            for (int i = 0; i < numOfIterations; i++) {
                // generate random num for inputs
                int[] userIDs = new int[3];
                int[] timeIntervals = new int[3];
                int[] stepCount = new int[3];
                for (int j = 0; j < 3; j++) {
                    userIDs[j] = ThreadLocalRandom.current().nextInt(2147483647) + 1;
                    timeIntervals[j] = generateRandomTimeInterval(phaseID);
                    stepCount[j] = (int) (Math.random() * 5001);
                }
                //post for first userID
                long startTime = System.currentTimeMillis();

                Response postResponse = target
                        .path(getPostRequestPath(userIDs[0], 1, timeIntervals[0], stepCount[0]))
                        .request()
                        .post(null);
                postResponse.close();

                long currentTime = System.currentTimeMillis();
                statistics.addLatency(currentTime - startTime);
                statistics.addLatencyMapping(startTime, currentTime - startTime);
                statistics.addRequest();


                //post for second userID
                startTime = System.currentTimeMillis();;

                postResponse = target
                        .path(getPostRequestPath(userIDs[1], 1, timeIntervals[1], stepCount[1]))
                        .request()
                        .post(null);
                postResponse.close();

                currentTime = System.currentTimeMillis();
                statistics.addLatency(currentTime - startTime);
                statistics.addLatencyMapping(startTime, currentTime - startTime);
                statistics.addRequest();

                //get request 1
                startTime = System.currentTimeMillis();
                String request = "current/" + Integer.toString(userIDs[0]);
                String response = target
                        .path(request)
                        .request(MediaType.TEXT_PLAIN)
                        .get(String.class);
                currentTime = System.currentTimeMillis();

                statistics.addLatency(currentTime - startTime);
                statistics.addLatencyMapping(startTime, currentTime - startTime);
                statistics.addRequest();

                //get request 2
                startTime = System.currentTimeMillis();
                request = "single/" + Integer.toString(userIDs[1]) + "/1";
                response = target
                        .path(request)
                        .request(MediaType.TEXT_PLAIN)
                        .get(String.class);
                currentTime = System.currentTimeMillis();
                statistics.addLatency(currentTime - startTime);
                statistics.addLatencyMapping(startTime, currentTime - startTime);
                statistics.addRequest();

                //post for third userID
                startTime = System.currentTimeMillis();

                postResponse = target
                        .path(getPostRequestPath(userIDs[2], 1, timeIntervals[2], stepCount[2]))
                        .request()
                        .post(null);
                postResponse.close();

                currentTime = System.currentTimeMillis();
                statistics.addLatency(currentTime - startTime);
                statistics.addLatencyMapping(startTime, currentTime - startTime);
                statistics.addRequest();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // generate random time interval given the phaseID, for example, for phase ID 2, generate
    // random time interval between 3 and 7
    private int generateRandomTimeInterval(int phaseID) {
        switch (phaseID) {
            case 1 :
                return ThreadLocalRandom.current().nextInt(3);
            case 2:
                return ThreadLocalRandom.current().nextInt(5) + 3;
            case 3:
                return ThreadLocalRandom.current().nextInt(11) + 8;
            default:
                return ThreadLocalRandom.current().nextInt(5) + 19;
        }
    }

    private String getPostRequestPath(int userID, int day, int time, int count) {
        StringBuilder path = new StringBuilder();
        path.append("create/");
        path.append(Integer.toString(userID));
        path.append('/');
        path.append(Integer.toString(day));
        path.append('/');
        path.append(Integer.toString(time));
        path.append('/');
        path.append(Integer.toString(count));
        return path.toString();
    }
}

