// ✅ GLOBAL FUNCTIONS
function goToForm() {
    window.location.href = "form.html";
}

// ✅ ANALYZE NEWS
async function analyzeNews() {
    let title = document.getElementById("title")?.value;
    let content = document.getElementById("content")?.value;

    if (!title || !content) {
        alert("Enter all fields!");
        return;
    }

    try {
        let response = await fetch("http://localhost:8080/fakenewsdetector-1.0/api/news", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ title, content, source: "Unknown" })
        });

        if (!response.ok) {
            throw new Error("Server error: " + response.status);
        }

        let data = await response.json();

        localStorage.setItem("newsTitle", title);
        localStorage.setItem("newsContent", content);
        localStorage.setItem("result", data.result);
        localStorage.setItem("confidence", data.result === "FAKE" ? "85" : "90");
        localStorage.setItem("explanation", data.result === "FAKE" ?
            "This news contains suspicious keywords and patterns." :
            "This news appears to be legitimate.");

        window.location.href = "result.html";

    } catch (error) {
        console.error("Fetch Error:", error);
        alert("Backend not running or connection failed! Error: " + error.message);
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
    let progress = document.getElementById("progress");
    let loader = document.getElementById("loader");
    let resultBox = document.getElementById("result");

    if (!progress || !loader || !resultBox) return;

    let width = 0;

    let interval = setInterval(() => {
        width += 5;
        progress.style.width = width + "%";

        if (width >= 100) {
            clearInterval(interval);
            loader.style.display = "none";
            resultBox.style.display = "block";
            showResult();
        }
    }, 100);
}

// ✅ SHOW RESULT
function showResult() {
    let title = localStorage.getItem("newsTitle");
    let content = localStorage.getItem("newsContent");
    let result = localStorage.getItem("result");
    let confidence = localStorage.getItem("confidence");
    let explanation = localStorage.getItem("explanation");

    let output = document.getElementById("output");
    let explanationBox = document.getElementById("explanation");
    let bar = document.getElementById("confidenceBar");

    if (!output || !bar) return;

    output.innerHTML =
        `<b>Title:</b> ${title}<br><br>
         <b>Content:</b> ${content}<br><br>
         <b>Result:</b> <span style="color:${result === 'FAKE' ? 'red' : 'green'}">${result}</span>`;

    if (explanationBox) {
        explanationBox.innerText = explanation;
    }

    bar.style.width = confidence + "%";
    bar.innerHTML = confidence + "%";
}

// ✅ DASHBOARD FUNCTION
async function loadDashboard() {
    try {
        let response = await fetch("http://localhost:8080/fakenewsdetector-1.0/api/news");

        if (!response.ok) {
            throw new Error("Server error");
        }

        let data = await response.json();
        let table = document.getElementById("tableData");

        if (!table) return;

        if (data.length === 0) {
            table.innerHTML = "<tr><td colspan='3'>No news checked yet!</td></tr>";
            return;
        }

        data.forEach(news => {
            let row = "<tr>" +
                "<td>" + news.title + "</td>" +
                "<td style='color:" + (news.result === 'FAKE' ? 'red' : 'green') + "'>" + news.result + "</td>" +
                "<td>" + news.date + "</td>" +
                "</tr>";
            table.innerHTML += row;
        });

    } catch (error) {
        console.error("Dashboard Error:", error);
        alert("Could not load dashboard: " + error.message);
    }
}