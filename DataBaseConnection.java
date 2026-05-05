package com.fakenews;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/fakenews_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";
    private static final String USER = "root";
    private static final String PASSWORD = "";  // ← empty password

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
