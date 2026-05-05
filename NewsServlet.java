package com.fakenews;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/news")
public class NewsServlet extends HttpServlet {

    private NewsDAO dao = new NewsDAO();

    // ─────────────────────────────────────────
    // Handle OPTIONS (fixes CORS preflight)
    // ─────────────────────────────────────────
    @Override
   protected void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_OK);
    }

    // ─────────────────────────────────────────
    // Handle POST — submit news for checking
    // ─────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        try {
            // Read request body
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Check if body is empty
            if (sb.toString().trim().isEmpty()) {
                out.write("{\"error\": \"Request body is empty\"}");
                return;
            }

            // Parse JSON input
            JSONObject input = new JSONObject(sb.toString());

            // Get values safely
            String title   = input.optString("title", "").trim();
            String content = input.optString("content", "").trim();
            String source  = input.optString("source", "Unknown").trim();

            // Validate required fields
            if (title.isEmpty() || content.isEmpty()) {
                out.write("{\"error\": \"Title and content are required\"}");
                return;
            }

            // ── Fake detection logic ──
            // You can improve this logic later with ML or keyword lists
            String contentLower = content.toLowerCase();
            String result;

            if (contentLower.contains("fake") ||
                contentLower.contains("hoax") ||
                contentLower.contains("false") ||
                contentLower.contains("misleading") ||
                contentLower.contains("fabricated")) {
                result = "FAKE";
            } else {
                result = "REAL";
            }

            // Save to database
            dao.saveNews(title, content, source, result);

            // Send response
            JSONObject response = new JSONObject();
            response.put("result", result);
            response.put("title", title);
            response.put("source", source);
            response.put("message", "News checked and saved successfully!");

            out.write(response.toString());

        } catch (org.json.JSONException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    // ─────────────────────────────────────────
    // Handle GET — fetch all checked news
    // ─────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        setCorsHeaders(res);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        try {
            List<String[]> newsList = dao.getAllNews();
            JSONArray arr = new JSONArray();

            for (String[] n : newsList) {
                JSONObject obj = new JSONObject();
                obj.put("title",  n[0]);
                obj.put("source", n[1]);
                obj.put("result", n[2]);
                obj.put("date",   n[3]);
                arr.put(obj);
            }

            out.write(arr.toString());

        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ─────────────────────────────────────────
    // Helper — set CORS headers on every response
    // ─────────────────────────────────────────
    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}