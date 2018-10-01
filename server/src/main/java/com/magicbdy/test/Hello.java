package com.magicbdy.test;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Server resource that handle HTTP GET and POST requests.
 */
@Path("hello")
public class Hello {
    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Got it";
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public int postText(String content) {
        return (content.length());
    }
}
