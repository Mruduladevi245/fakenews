package com.fakenews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/news")
public class NewsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private NewsDAO dao = new NewsDAO();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = req.getReader();
             PrintWriter out = res.getWriter()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.toString().trim().isEmpty()) {
                out.print("{\"status\":\"error\",\"message\":\"Empty request body\"}");
                return;
            }

            JSONObject input = new JSONObject(sb.toString());

            String title      = input.optString("title",   "No Title").trim();
            String content    = input.optString("content", "").trim();
            String source     = input.optString("source",  "Unknown").trim();
            boolean isRealNews = input.optBoolean("isRealNews", false);

            if (content.isEmpty()) {
                out.print("{\"status\":\"error\",\"message\":\"Content is required\"}");
                return;
            }

            // ── Trust Score Logic ──
            String contentLower = content.toLowerCase();
            int fakeScore = 0;

            if (isRealNews) {
                // Verified by NewsAPI → trust score 100
                fakeScore = 0;
            } else {
                // Keyword analysis
                if (contentLower.contains("fake"))        fakeScore += 25;
                if (contentLower.contains("hoax"))        fakeScore += 25;
                if (contentLower.contains("false"))       fakeScore += 20;
                if (contentLower.contains("misleading"))  fakeScore += 20;
                if (contentLower.contains("fabricated"))  fakeScore += 25;
                if (contentLower.contains("rumor"))       fakeScore += 15;
                if (contentLower.contains("unverified"))  fakeScore += 15;
                if (contentLower.contains("clickbait"))   fakeScore += 20;
                if (contentLower.contains("shocking"))    fakeScore += 10;
                if (contentLower.contains("viral"))       fakeScore += 10;
            }

            if (fakeScore > 100) fakeScore = 100;
            int trustScore = 100 - fakeScore;

            String result;
            String label;

            if (isRealNews) {
                result = "REAL";
                label  = "Verified Real News";
            } else if (trustScore <= 25) {
                result = "FAKE";
                label  = "Highly Fake";
            } else if (trustScore <= 50) {
                result = "FAKE";
                label  = "Likely Fake";
            } else if (trustScore <= 75) {
                result = "SUSPICIOUS";
                label  = "Suspicious";
            } else {
                result = "REAL";
                label  = "Likely Real";
            }

            // Save to database
            dao.saveNews(title, content, source, result,
                         trustScore, label, isRealNews);

            // Send response
            JSONObject response = new JSONObject();
            response.put("status",      "success");
            response.put("result",      result);
            response.put("label",       label);
            response.put("trustScore",  trustScore);
            response.put("isRealNews",  isRealNews);
            response.put("title",       title);
            response.put("source",      source);
            response.put("message",     "News checked and saved successfully!");

            out.print(response.toString());

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("status",  "error");
            error.put("message", e.getMessage());
            res.getWriter().print(error.toString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        try (PrintWriter out = res.getWriter()) {

            List<String[]> newsList = dao.getAllNews();
            JSONArray arr = new JSONArray();

            for (String[] n : newsList) {
                JSONObject obj = new JSONObject();
                obj.put("title",      n[0]);
                obj.put("source",     n[1]);
                obj.put("result",     n[2]);
                obj.put("label",      n[3]);
                obj.put("trustScore", n[4]);
                obj.put("isRealNews", n[5]);
                obj.put("date",       n[6]);
                arr.put(obj);
            }

            out.print(arr.toString());

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject error = new JSONObject();
            error.put("status",  "error");
            error.put("message", e.getMessage());
            res.getWriter().print(error.toString());
        }
    }

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin",  "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
