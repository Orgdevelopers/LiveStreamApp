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

  if(msg.isGift){
    addGiftMsg(sender.username, msg.gift.name);
    if(msg.gift.isAnimated){
      playGiftAnimation(sender,msg.gift);
    }
  }else{
    addChatMsg(sender.username, messageTxt);
  }

  addChatMsg(sender.username, messageTxt);
}

function playGiftAnimation(sender, gift) {
  const animation_container = document.getElementById('animation-container');
  animation_container.classList.add('active');
  animation_container.innerHTML = `<dotlottie-wc class="animation-lottie" src="${gift.animation}" speed="1" mode="forward" autoplay></dotlottie-wc>`;
  const dotLottieElement = animation_container.querySelector('dotlottie-wc').dotLottie;
  dotLottieElement.addEventListener("ready", () => {
    log("DotLottie element is ready.");
    // You can access the DotLottie instance here if needed
    // const dotLottie = dotLottieElement.dotLottie;
  });

  dotLottieElement.addEventListener("load", () => {
    log("DotLottie animation has loaded.");
  });

  dotLottieElement.addEventListener("play", () => {
    log("Animation started playing.");
  });

  dotLottieElement.addEventListener("pause", () => {
    log("Animation paused.");
  });

  dotLottieElement.addEventListener("complete", () => {
    log("Animation completed.");
    
  });

  dotLottieElement.addEventListener("frame", (event) => {
    // const { currentFrame } = event.detail; // Access event details for frame throws error if not autoplay / fram 0 error
    // console.log(`Current frame: ${currentFrame}`);
    //console.log(event);
  });

  dotLottieElement.addEventListener("loadError", (event) => {
    const { error } = event.detail; // Access event details for error
    console.error("Error loading animation:", error);
  });

  dotLottieElement.addEventListener("loop", (event) => {
    const { loopCount } = event.detail; // Access event details for loop count
    log(`Animation looped. Loop count: ${loopCount}`);
  });
}

function addGiftMsg(user, gift) {
  const div = document.createElement('div');
  div.classList.add('gift-msg');
  div.textContent = `${user} sent a ${gift}`;
  chatOverlay.appendChild(div);
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
