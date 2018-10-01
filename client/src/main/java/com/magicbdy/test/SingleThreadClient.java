package com.magicbdy.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Run the client HTTP Get and Post request in a single thread
 */
public class SingleThreadClient extends Thread {

    private int iterations;
    private WebTarget target;
    private Stats statistics;

    public SingleThreadClient(WebTarget target, int iterations, Stats stats) {
        this.iterations = iterations;
        this.target = target;
        this.statistics = stats;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {

            //post
            long startTime = System.currentTimeMillis();
            Response postResponse = target
                    .path("hello")
                    .request()
                    .post(Entity.entity("test", MediaType.TEXT_PLAIN_TYPE));
            long currentTime = System.currentTimeMillis();
            statistics.add(currentTime - startTime);
            statistics.requestAdd();
            String s = postResponse.readEntity(String.class);
            if (postResponse.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL
                    && Integer.parseInt(s) == 4) {
                statistics.successAdd();
            }
            postResponse.close();

            //get
            startTime = System.currentTimeMillis();
            String response = target
                    .path("hello")
                    .request(MediaType.TEXT_PLAIN)
                    .get(String.class);
            currentTime = System.currentTimeMillis();
            statistics.add(currentTime - startTime);
            statistics.requestAdd();
            if (response.equals("Got it")) {
                statistics.successAdd();
            }

        }

    }
}

