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
  animation_container.innerHTML = `<dotlottie-wc src="${gift.animation}" speed="1" style="width: 300px; height: 300px" mode="forward" loop autoplay></dotlottie-wc>`;
  const dotlottie = animation_container.querySelector('dotlottie-wc');
  dotlottie.addEventListener('loaded', () => {
    console.log('loaded');
    dotlottie.addEventListener('loopComplete', () => {
      console.log('loop complete');
      animation_container.classList.remove('active');
      animation_container.innerHTML = '';
    });
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
