package com.magicbdy.server.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariPool {
    private static HikariPool instance = null;
    private HikariDataSource ds = null;

    static {
        try {
            instance = new HikariPool();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    //ALTERADO
    private HikariPool() {
        ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://rdsinstanceforassignment.cmhhhhcoknci.us-west-2.rds.amazonaws.com:3306/wearabledevice");
        ds.setUsername("magicbdy");
        ds.setPassword("xy031431");
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds.addDataSourceProperty("useServerPrepStmts", "true");
        ds.addDataSourceProperty("useSSL",false);
    }

    public static HikariPool getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            ds.setIdleTimeout(60000);
            ds.setConnectionTimeout(60000);
            ds.setValidationTimeout(3000);
            ds.setLoginTimeout(5);
            ds.setMaxLifetime(60000);
            ds.setMaximumPoolSize(60);
            ds.setLeakDetectionThreshold(5000);
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

