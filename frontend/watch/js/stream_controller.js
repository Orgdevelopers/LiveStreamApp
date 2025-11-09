const chatOverlay = document.getElementById("chatOverlay");
const chatInputContainer = document.getElementById("chatInputContainer");
const chatBtn = document.getElementById("chatBtn");
const sendChatBtn = document.getElementById("sendChatBtn");
const chatInput = document.getElementById("chatInput");

chatBtn.onclick = () => {
  chatInputContainer.classList.toggle("active");
  if (chatInputContainer.classList.contains("active")) {
    chatInput.focus();
  }
};

sendChatBtn.onclick = () => {
  sendChatTrigger();
};

chatInput.addEventListener("keydown", function (e) {
  if (e.key == "Enter") {
    e.preventDefault();
    sendChatTrigger();
  }
});

function sendChatTrigger() {
  const msg = chatInput.value.trim();
  if (!msg) return;
  chatInput.value = "";
  sendChatMsg(getSessionId(), msg);
  setTimeout(() => {
    addChatMsg("You", msg);
  }, 100);
}

function handleServerMsg(msg) {
  const sender = msg.sender;
  const messageTxt = msg.msg;

  addChatMsg(sender.username, messageTxt);
}

function addChatMsg(user, msg) {
  const div = document.createElement('div');
  div.classList.add('chat-msg');
  div.textContent = `${user}: ${msg}`;
  chatOverlay.appendChild(div);

  // Auto-scroll to bottom if user is near the bottom
  if (chatOverlay.scrollHeight - chatOverlay.scrollTop - chatOverlay.clientHeight < 50) {
    chatOverlay.scrollTop = chatOverlay.scrollHeight;
  }
}


//server broadcast msg optional
setTimeout(
  () => addChatMsg("Server", "Hey 👋 please be nice to everyone"),
  1000
);
