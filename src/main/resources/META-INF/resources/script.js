<!-- ✅ script.js -->
function login() {
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;
  const message = document.getElementById("loginMessage");

  fetch("/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password })
  })
    .then(res => {
      if (!res.ok) throw new Error("Invalid email or password");
      return res.text();
    })
    .then(text => {
      localStorage.setItem("currentEmail", email);
      if (text.includes("validate your email")) {
        localStorage.setItem("emailVerified", "false");
        document.getElementById("validateBtn").style.display = "inline-block";
        message.textContent = text;
      } else {
        localStorage.setItem("emailVerified", "true");
        document.getElementById("validateBtn").style.display = "none";
        window.location.href = "accessportal.html";
      }
    })
    .catch(err => message.textContent = err.message);
}

function signup() {
  const email = document.getElementById("signupEmail").value;
  const password = document.getElementById("signupPassword").value;

  fetch("/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  })
    .then(res => res.ok ? res.json() : res.text().then(text => { throw new Error(text); }))
    .then(data => {
      localStorage.setItem("token", data.token);
      localStorage.setItem("currentEmail", email);
      localStorage.setItem("emailVerified", "false");
      alert("Signup successful. Now validate your email.");
      window.location.href = "index.html";
    })
    .catch(err => alert("Signup failed: " + err.message));
}

function validateEmail() {
  const token = localStorage.getItem("token");
  if (!token) return alert("No token found. Please signup again.");

  fetch(`/verify?token=${token}`)
    .then(res => res.text())
    .then(text => {
      alert(text);
      localStorage.setItem("emailVerified", "true");
      document.getElementById("validateBtn").style.display = "none";
      window.location.href = "accessportal.html";
    })
    .catch(err => alert("Validation failed: " + err.message));
}

async function logoutUser() {
  await fetch("/logout", { method: "GET", credentials: "include" });
  localStorage.clear();
  window.location.href = "index.html";
}

function checkLoginStatus(visibleId, hiddenId) {
  fetch("/profile", { credentials: "include", cache: "no-store" })
    .then(res => res.ok ? res.json() : Promise.reject())
    .then(data => {
      if (!data.email) throw new Error();
    })
    .catch(() => {
      document.getElementById(visibleId).style.display = "none";
      document.getElementById(hiddenId).style.display = "block";
      localStorage.clear();
    });
}

function loadUserProfile() {
  fetch("/profile", { credentials: "include", cache: "no-store" })
    .then(res => res.ok ? res.json() : Promise.reject())
    .then(data => {
      document.getElementById("user-email").innerText = data.email || "N/A";
    })
    .catch(() => {
      document.getElementById("profileCard").style.display = "none";
      document.getElementById("notLoggedInCard").style.display = "block";
      localStorage.clear();
    });
}

window.onload = () => {
  const validateBtn = document.getElementById("validateBtn");
  if (validateBtn) {
    const verified = localStorage.getItem("emailVerified");
    validateBtn.style.display = verified === "true" ? "none" : "inline-block";
  }
};