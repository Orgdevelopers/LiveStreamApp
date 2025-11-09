// only loaded in login/signup page

window.addEventListener("load", function () {
  if (validateSession()) {
    //session is valid redirect user to homepage
    showSnackbar(true, "Login detected");
    setTimeout(() => {
      this.window.location.assign("/home");
    }, 200);

    return;
  }
  chk.checked = true; // it loads an animation for UI
});

function authSuccess(user, expiry, timout) {
  setSessionId(user.id);
  setSessionUser(user);
  setSessionExpiry(expiry);
  setTimeout(() => {
    window.location.assign("/home");
  }, timout);
}
