package com.magicbdy.server.dao;
import com.magicbdy.server.model.WearableData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WearableDao {

    protected HikariPool connectionPool;
    private static WearableDao instance = null;

    protected WearableDao() {
        this.connectionPool = HikariPool.getInstance();
    }

    public static WearableDao getInstance() {
        if (instance == null) {
            instance = new WearableDao();
        }
        return instance;
    }

    public WearableData insert(WearableData cur) throws SQLException {

        String insertWearable = "INSERT IGNORE INTO StepCount(UserID, DayIndex, TimeInterval, StepCount)" + "values (?,?,?,?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        try {
            connection = connectionPool.getConnection();
            insertStmt = connection.prepareStatement(insertWearable);
            insertStmt.setInt(1, cur.getUserID());
            insertStmt.setInt(2, cur.getDayIndex());
            insertStmt.setInt(3, cur.getTimeInterval());
            insertStmt.setInt(4, cur.getStepCount());
            insertStmt.executeUpdate();
            return cur;
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if(connection != null) {
                connection.close();
            }
            if(insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    public int getStepCountByUserID(int userID) throws SQLException {
        String selectStepCount = "SELECT SUM(StepCount) FROM StepCount WHERE UserID = ?;";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ResultSet results = null;
        try {
            connection = connectionPool.getConnection();
            PreparedStatement selectStmt = connection.prepareStatement(selectStepCount);
            selectStmt.setInt(1, userID);
            results = selectStmt.executeQuery();

            if (results.next()) {
                int totalCount = results.getInt(1);
                return totalCount;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }
            if (results != null) {
                results.close();
            }
        }
        return -1;
    }

    public int getSingleDayStepCount(int userID, int dayIndex) throws SQLException {
        String selectStepCount = "SELECT SUM(StepCount) FROM StepCount WHERE UserID = ? AND DayIndex = ?;";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ResultSet results = null;

        try {
            connection = connectionPool.getConnection();
            PreparedStatement selectStmt = connection.prepareStatement(selectStepCount);
            selectStmt.setInt(1, userID);
            selectStmt.setInt(2, dayIndex);
            results = selectStmt.executeQuery();
            if (results.next()) {
                int totalCount = results.getInt(1);
                return totalCount;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }
            if (results != null) {
                results.close();
            }
        }
        return -1;
    }

    public int getMultiDayStepCount(int userID, int startDay, int numDays) throws SQLException {
        String selectStepCount = "SELECT SUM(StepCount) FROM StepCount WHERE UserID = ? AND DayIndex >= ? AND DayIndex <= ?;";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        ResultSet results = null;

        try {
            connection = connectionPool.getConnection();
            PreparedStatement selectStmt = connection.prepareStatement(selectStepCount);
            selectStmt.setInt(1, userID);
            selectStmt.setInt(2, startDay);
            selectStmt.setInt(3, numDays);
            results = selectStmt.executeQuery();
            if (results.next()) {
                int totalCount = results.getInt(1);
                return totalCount;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }
            if (results != null) {
                results.close();
            }
        }
        return -1;
    }

}
