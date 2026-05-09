// ✅ GLOBAL FUNCTIONS
function goToForm() {
    window.location.href = "form.html";
}

// ✅ CHECK REAL NEWS API
async function checkRealNewsAPI(title) {
    try {
        let apiKey   = "66911c6b04da4bfdbc88b4103cb289dd";
        let query    = encodeURIComponent(title);
        let url      = "https://newsapi.org/v2/everything?q=" + query +
                       "&apiKey=" + apiKey + "&pageSize=5&language=en";

        let response = await fetch(url);
        let data     = await response.json();

        console.log("NewsAPI response:", data);

        if (data.status === "ok" && data.totalResults > 0) {
            console.log("Found " + data.totalResults + " real articles!");
            return true;
        }
        return false;

    } catch (error) {
        console.error("NewsAPI Error:", error);
        return false;
    }
}

// ✅ ANALYZE NEWS
async function analyzeNews() {
    let title   = document.getElementById("title")?.value.trim();
    let content = document.getElementById("content")?.value.trim();
    let source  = document.getElementById("source")?.value?.trim() || "Unknown";

    if (!title || !content) {
        alert("Please enter both Title and Content!");
        return;
    }

    // Show loading message
    let btn = document.querySelector("button[onclick='analyzeNews()']");
    if (btn) {
        btn.innerText = "Analyzing...";
        btn.disabled  = true;
    }

    try {
        // ✅ Step 1 — Check real news API
        let isRealNews = await checkRealNewsAPI(title);
        console.log("Is Real News from API:", isRealNews);

        // ✅ Step 2 — Send to Java backend
        let response = await fetch("http://localhost:8080/fakenewsdetector-1.0/api/news", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                title,
                content,
                source,
                isRealNews: isRealNews
            })
        });

        if (!response.ok) {
            throw new Error("Server error: " + response.status);
        }

        let data = await response.json();
        console.log("Backend response:", JSON.stringify(data));

        // ✅ Store all data
        localStorage.setItem("newsTitle",   title);
        localStorage.setItem("newsContent", content);
        localStorage.setItem("result",      data.result     || "UNKNOWN");
        localStorage.setItem("label",       data.label      || "Unknown");
        localStorage.setItem("trustScore",  data.trustScore !== undefined ? data.trustScore : 50);
        localStorage.setItem("apiVerified", isRealNews ? "YES" : "NO");
        localStorage.setItem("explanation",
            isRealNews ?
            "This news was VERIFIED in real published news sources online!" :
            data.result === "FAKE" ?
            "This news was NOT found in any verified source and contains suspicious keywords." :
            data.result === "SUSPICIOUS" ?
            "This news contains suspicious elements. Could not verify in news sources." :
            "This news appears legitimate based on keyword analysis.");

        window.location.href = "result.html";

    } catch (error) {
        console.error("Fetch Error:", error);
        alert("Backend not running or connection failed! Error: " + error.message);
    } finally {
        if (btn) {
            btn.innerText = "Check News";
            btn.disabled  = false;
        }
    }
}

// ✅ RUN PAGE-SPECIFIC CODE SAFELY
document.addEventListener("DOMContentLoaded", () => {

    if (window.location.pathname.includes("result.html")) {
        startLoader();
    }

    if (window.location.pathname.includes("dashboard.html")) {
        loadDashboard();
    }
});

// ✅ LOADER FUNCTION
function startLoader() {
    let progress  = document.getElementById("progress");
    let loader    = document.getElementById("loader");
    let resultBox = document.getElementById("result");

    if (!progress || !loader || !resultBox) return;

    let width = 0;
    let interval = setInterval(() => {
        width += 5;
        progress.style.width = width + "%";

        if (width >= 100) {
            clearInterval(interval);
            loader.style.display    = "none";
            resultBox.style.display = "block";
            showResult();
        }
    }, 100);
}

// ✅ SHOW RESULT
function showResult() {
    let title       = localStorage.getItem("newsTitle")   || "N/A";
    let content     = localStorage.getItem("newsContent") || "N/A";
    let result      = localStorage.getItem("result")      || "UNKNOWN";
    let label       = localStorage.getItem("label")       || "Unknown";
    let trustScore  = parseInt(localStorage.getItem("trustScore")) || 0;
    let explanation = localStorage.getItem("explanation") || "";
    let apiVerified = localStorage.getItem("apiVerified") || "NO";

    let output         = document.getElementById("output");
    let explanationBox = document.getElementById("explanation");
    let bar            = document.getElementById("confidenceBar");
    let labelText      = document.getElementById("labelText");
    let verifiedBox    = document.getElementById("apiVerified");

    if (!output) return;

    // ✅ Color based on trust score
    let color;
    if      (trustScore <= 25) color = "#e74c3c";
    else if (trustScore <= 50) color = "#e67e22";
    else if (trustScore <= 75) color = "#f1c40f";
    else                       color = "#27ae60";

    // ✅ Display result
    output.innerHTML =
        "<b>Title:</b> " + title + "<br><br>" +
        "<b>Content:</b> " + content + "<br><br>" +
        "<b>Result:</b> " +
        "<span style='color:" + color + "; font-size:24px; font-weight:bold;'>" +
        result + "</span>";

    // ✅ Trust score bar
    if (bar) {
        bar.style.width           = trustScore + "%";
        bar.style.backgroundColor = color;
        bar.style.height          = "30px";
        bar.style.borderRadius    = "10px";
        bar.style.textAlign       = "center";
        bar.style.lineHeight      = "30px";
        bar.style.color           = "white";
        bar.style.fontWeight      = "bold";
        bar.style.transition      = "width 1s";
        bar.innerHTML             = trustScore + "% Trust";
    }

    // ✅ Label
    if (labelText) {
        labelText.style.color      = color;
        labelText.style.fontSize   = "22px";
        labelText.style.fontWeight = "bold";
        labelText.style.textAlign  = "center";
        labelText.style.marginTop  = "10px";
        labelText.innerText        = label;
    }

    // ✅ API Verification Status
    if (verifiedBox) {
        if (apiVerified === "YES") {
            verifiedBox.style.backgroundColor = "#d4edda";
            verifiedBox.style.border          = "2px solid #27ae60";
            verifiedBox.style.borderRadius    = "8px";
            verifiedBox.style.padding         = "10px";
            verifiedBox.style.textAlign       = "center";
            verifiedBox.innerHTML =
                "<span style='color:green; font-size:18px; font-weight:bold;'>" +
                "VERIFIED in Real News Sources!" +
                "</span>";
        } else {
            verifiedBox.style.backgroundColor = "#f8d7da";
            verifiedBox.style.border          = "2px solid #e74c3c";
            verifiedBox.style.borderRadius    = "8px";
            verifiedBox.style.padding         = "10px";
            verifiedBox.style.textAlign       = "center";
            verifiedBox.innerHTML =
                "<span style='color:red; font-size:18px; font-weight:bold;'>" +
                "NOT FOUND in Real News Sources!" +
                "</span>";
        }
    }

    // ✅ Explanation
    if (explanationBox) {
        explanationBox.innerText = explanation;
    }
}

// ✅ DASHBOARD FUNCTION
async function loadDashboard() {
    try {
        let response = await fetch("http://localhost:8080/fakenewsdetector-1.0/api/news");

        if (!response.ok) {
            throw new Error("Server error");
        }

        let data  = await response.json();
        let table = document.getElementById("tableData");

        if (!table) return;

        if (data.length === 0) {
            table.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No news checked yet!</td></tr>";
            return;
        }

        data.forEach(news => {
            let color;
            let score = parseInt(news.trustScore);
            if      (score <= 25) color = "red";
            else if (score <= 50) color = "orange";
            else if (score <= 75) color = "goldenrod";
            else                  color = "green";

            let verified = news.isRealNews === "true" ?
                "<span style='color:green;'>Verified</span>" :
                "<span style='color:red;'>Not Verified</span>";

            let row = "<tr>" +
                "<td>" + news.title + "</td>" +
                "<td style='color:" + color + "; font-weight:bold;'>" + news.result + "</td>" +
                "<td style='color:" + color + ";'>" + news.label + "</td>" +
                "<td style='color:" + color + "; font-weight:bold;'>" + news.trustScore + "%</td>" +
                "<td>" + verified + "</td>" +
                "<td>" + news.date + "</td>" +
                "</tr>";

            table.innerHTML += row;
        });

    } catch (error) {
        console.error("Dashboard Error:", error);
        alert("Could not load dashboard: " + error.message);
    }
}
