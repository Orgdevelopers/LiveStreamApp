//constants

const SERVER_URL = "http://localhost:8080/";
const LOGIN_URL = SERVER_URL + "auth/login";
const SIGNUP_URL = SERVER_URL + "auth/signup";
const LIVESTREAM_LIST_URL = SERVER_URL + "livestreams/active";


//
const LOGS_ENABLE = true;
const SUCCESS_COLOR = "#82dd55";
const ERROR_COLOR = "#e23636";
const WARNING_COLOR = "";
//

function apiRequest(method,url, formData, respListener) {
  showLoader();
  $.ajax({
    type: method,
    contentType: "application/json",
    data: JSON.stringify(formData),
    url: url,
    success: function (result) {
      log(result);
      hideLoader();
      respListener.onSuccess(result);
    },
    error: function (error) {
      log(error);
      hideLoader();

      respListener.onError(error);
    },
  });
}

window.addEventListener("load", function () {
  chk.checked = true; // it loads an animation for UI
  setTimeout(() => {
    if(validateSession()){
      if (getSessionId() != null && getSessionUser() != null) {
        //session has login records
        this.window.location.assign("./home");
      }
    }
  }, 500);
});



function log(msg) {
  if (LOGS_ENABLE) {
    console.log(msg);
  }
}

function showSnackbar(success, msg) {
  var snackbar = document.getElementById("snackbar");
  snackbar.innerHTML = msg;
  snackbar.className = "show";
  if (success) {
    //green
    snackbar.style.backgroundColor = SUCCESS_COLOR;

  }else{
    //error
    snackbar.style.backgroundColor = ERROR_COLOR;

  }
  
  setTimeout(function(){ snackbar.className = snackbar.className.replace("show", ""); }, 3500);
}


//
function getSessionUser(){
  const stringUser = sessionStorage.getItem("user");
  if(stringUser != null){
    const user = JSON.parse(stringUser);
    return user;
  }

  return null;
}

function getSessionId(){
  return sessionStorage.getItem("id");
}

function validateSession() {
  if (getSessionId() != null && !isSessionExpired()) {
    return true;
  }
  logOut(); // clear expired session
  return false;
}


function setSessionExpiry(hours = 24) {
  const expiry = Date.now() + hours * 60 * 60 * 1000;
  sessionStorage.setItem("expiry", expiry);
}

function isSessionExpired() {
  const expiry = sessionStorage.getItem("expiry");
  if (!expiry) return true;
  return Date.now() > Number(expiry);
}


function getSessionUser() {
  const stringUser = sessionStorage.getItem("user");
  if (!stringUser) return null;

  try {
    return JSON.parse(stringUser);
  } catch (e) {
    console.error("Invalid session data", e);
    return null;
  }
}


function setSessionId(id){
  sessionStorage.setItem("id",id);
}

function logOut(){
  sessionStorage.clear();
  window.location.reload();
}

//loader script

function showLoader() {
  const loader = document.getElementById("ajax-loader");
  if (!loader) return;
  loader.setAttribute("aria-hidden", "false");
  requestAnimationFrame(() => loader.classList.add("active"));
}

function hideLoader() {
  const loader = document.getElementById("ajax-loader");
  if (!loader) return;
  loader.setAttribute("aria-hidden", "true");
  loader.classList.remove("active");
}
