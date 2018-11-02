package com.magicbdy.server;

import com.magicbdy.server.model.WearableData;
import com.magicbdy.server.dao.WearableDao;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

/**
 * Server resource that handle HTTP GET and POST requests.
 */
@Path("/")
public class WearableServer {
    private static final WearableDao wearableDao = WearableDao.getInstance();

    /**
     * Method handling HTTP POST requests. Given userID, dayIndex, timeInterval, stepCount, add
     * into the table.
     *
     */
    @Path("{userID}/{day}/{timeInterval}/{stepCount}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void getData(@PathParam("userID") int userID, @PathParam("day") int dayIndex,
                        @PathParam("timeInterval") int timeInterval, @PathParam("stepCount") int stepCount) throws SQLException {
        WearableData cur = new WearableData(userID, dayIndex, timeInterval, stepCount);
        wearableDao.insert(cur);
    }

    /**
     * Method handling HTTP GET requests.
     *
     * @return The total step count for the given user
     */
    @Path("current/{userID}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public int getData(@PathParam("userID") int userID) throws SQLException {
        return wearableDao.getStepCountByUserID(userID);
    }

    /**
     * Method handling HTTP GET requests.
     *
     * @return The total step count for the given user on single day
     */
    @Path("single/{userID}/{day}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public int getData(@PathParam("userID") int userID, @PathParam("day") int dayIndex) throws SQLException {
        return wearableDao.getSingleDayStepCount(userID, dayIndex);
    }

    /**
     * Method handling HTTP GET requests.
     *
     * @return The total step count for the given user on multiple day
     */
    @Path("range/{userID}/{startDay}/{numDays}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public int getData(@PathParam("userID") int userID, @PathParam("startDay") int start, @PathParam("numDays") int numDays) throws SQLException {
        return wearableDao.getMultiDayStepCount(userID, start, numDays);
    }

}
