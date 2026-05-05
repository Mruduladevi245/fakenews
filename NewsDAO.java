package com.fakenews;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NewsDAO {

    // Save a checked news item
    public void saveNews(String title, String content, String source, String result) throws Exception {
        Connection con = DataBaseConnection.getConnection();
        String sql = "INSERT INTO news (title, content, source, result) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, content);
        ps.setString(3, source);
        ps.setString(4, result);
        ps.executeUpdate();
        con.close();
    }

    // Get all checked news
    public List<String[]> getAllNews() throws Exception {
        Connection con = DataBaseConnection.getConnection();
        List<String[]> list = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM news ORDER BY checked_at DESC");
        while (rs.next()) {
            list.add(new String[]{
                rs.getString("title"),
                rs.getString("source"),
                rs.getString("result"),
                rs.getString("checked_at")
            });
        }
        con.close();
        return list;
    }
}