package com.magicbdy.client.model;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Simple model for wearable step counter
 */
@XmlType(name="")
public class WearableData implements Serializable {
    private int userID;
    private int dayIndex;
    private int timeInterval;
    private int stepCount;

    public WearableData() {

    }

    public WearableData(int userID, int dayIndex, int timeInterval, int stepCount) {
        this.userID = userID;
        this.dayIndex = dayIndex;
        this.timeInterval = timeInterval;
        this.stepCount = stepCount;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }
}
