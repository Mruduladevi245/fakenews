package com.fakenews;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NewsDAO {

    // Save news with all fields
    public void saveNews(String title, String content, String source,
                         String result, int trustScore,
                         String label, boolean isRealNews) throws Exception {

        Connection con = DataBaseConnection.getConnection();
        String sql = "INSERT INTO news " +
                     "(title, content, source, result, trust_score, label, is_real_news) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, content);
        ps.setString(3, source);
        ps.setString(4, result);
        ps.setInt(5, trustScore);
        ps.setString(6, label);
        ps.setBoolean(7, isRealNews);
        ps.executeUpdate();
        con.close();
    }

    // Get all checked news
    public List<String[]> getAllNews() throws Exception {
        Connection con = DataBaseConnection.getConnection();
        List<String[]> list = new ArrayList<>();
        ResultSet rs = con.createStatement().executeQuery(
            "SELECT * FROM news ORDER BY checked_at DESC"
        );
        while (rs.next()) {
            list.add(new String[]{
                rs.getString("title"),
                rs.getString("source"),
                rs.getString("result"),
                rs.getString("label"),
                String.valueOf(rs.getInt("trust_score")),
                String.valueOf(rs.getBoolean("is_real_news")),
                rs.getString("checked_at")
            });
        }
        con.close();
        return list;
    }
}
