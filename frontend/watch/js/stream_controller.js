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

  if (msg.isGift) {
    addGiftMsg(sender.username, msg.gift.name);
    if (msg.gift.isAnimated) {
      playGiftAnimation(sender, msg.gift);
    }
  } else {
    addChatMsg(sender.username, messageTxt);
  }

  addChatMsg(sender.username, messageTxt);
}

let isAnimationPlaying = false;
let animationQueue = [];

function playGiftAnimation(sender, gift) {
  animationQueue.push({ sender, gift });
  processQueue();
}

function processQueue() {
  if (isAnimationPlaying) return;

  if (animationQueue.length === 0) return;

  const { sender, gift } = animationQueue.shift();

  startAnimation(sender, gift);
}

function startAnimation(sender, gift) {
  const animation_container = document.getElementById("animation-container");

  isAnimationPlaying = true;
  animation_container.classList.add("active");

  animation_container.innerHTML = "";
  const el = document.createElement("dotlottie-wc");

  el.className = "animation-lottie";
  el.setAttribute("src", gift.animation);
  el.setAttribute("autoplay", "");
  el.setAttribute("mode", "forward");

  animation_container.appendChild(el);

  const dot = el.dotLottie;

  dot.addEventListener("play", () => {
    log("Animation started");
  });

  dot.addEventListener("complete", () => {
    log("Animation completed");

    animation_container.innerHTML = "";
    animation_container.classList.remove("active");

    isAnimationPlaying = false;

    processQueue();
  });

  dot.addEventListener("loadError", (event) => {
    console.error("Error loading animation:", event.detail.error);

    isAnimationPlaying = false;
    processQueue();
  });
}


function addGiftMsg(user, gift) {
  const div = document.createElement("div");
  div.classList.add("gift-msg");
  div.textContent = `${user} sent a ${gift}`;
  chatOverlay.appendChild(div);
}

function addChatMsg(user, msg) {
  const div = document.createElement("div");
  div.classList.add("chat-msg");
  div.textContent = `${user}: ${msg}`;
  chatOverlay.appendChild(div);

  // Auto-scroll to bottom if user is near the bottom
  if (
    chatOverlay.scrollHeight -
      chatOverlay.scrollTop -
      chatOverlay.clientHeight <
    50
  ) {
    chatOverlay.scrollTop = chatOverlay.scrollHeight;
  }
}

//server broadcast msg optional
setTimeout(
  () => addChatMsg("Server", "Hey 👋 please be nice to everyone"),
  1000
);
