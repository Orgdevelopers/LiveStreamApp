const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let stream_id = 0;
window.stream_id = stream_id; // Expose globally for gift_logic.js


window.addEventListener("load",()=>{
    if (validateSession() && urlParams.has("id")) {
        //load
        stream_id = urlParams.get("id");
        window.stream_id = stream_id; // Update global reference
        // Fetch initial stream data to get viewer count
        fetchStreamData(stream_id);
        joinLiveStream();

        return;
    }

    showSnackbar(false, "Login expired: please login again");

    setTimeout(() => {
        window.location.assign("../");
    }, 300);
});

function fetchStreamData(streamId) {
  apiRequest(
    "get",
    SERVER_URL + "livestreams/" + streamId,
    {},
    {
      onSuccess(result) {
        if (result.response) {
          updateViewerCount(result.response.viewerCount);
        }
      },
      onError(err) {
        log("Failed to fetch stream data:", err);
      }
    }
  );
}


function joinLiveStream(){
    connectToLiveSocket();

}

let socket = null;
let stomp = null;
window.stomp = stomp; // Expose globally for gift_logic.js
let hasJoined = false;

function connectToLiveSocket(retryCount = 0) {
  socket = new SockJS(LIVESTREAMLIST_SOCKET_URL);
  stomp = Stomp.over(socket);
  window.stomp = stomp; // Update global reference

  stomp.connect(
    {},
    function () {
      // Subscribe to chat messages
      stomp.subscribe(`/liveStreams/${stream_id}/chat`, function (message) {
        const msg = JSON.parse(message.body);

        if (msg.type === 'JOIN' || msg.type === 'LEAVE') {
            displayNotification(msg);
        } else {
            if (msg && msg.liveStream.id == stream_id && msg.sender.id != getSessionId()) {
              handleServerMsg(msg);
            }else{
              console.log("something wrong");
            }
        }
      });

      // Subscribe to stream updates (for viewer count)
      stomp.subscribe(`/updates/streams`, function (message) {
        const stream = JSON.parse(message.body);
        if (stream.id == stream_id) {
          console.log("stream updated", stream);
          updateViewerCount(stream.viewerCount);
        }
      });

      // Send join event
      if (!hasJoined) {
        const user = getSessionUser();
        if (user) {
          stomp.send(`/app/liveStreams/${stream_id}/join`, {}, JSON.stringify({id: user.id}));
          hasJoined = true;
        }
      }
    },
    function () {
      if (retryCount < 5) {
        setTimeout(() => connectToLiveSocket(retryCount + 1), 3000);
        console.log("Reconnecting socket... Attempt " + (retryCount + 1));
      } else {
        showSnackbar(false, "Failed to reconnect to live updates");
      }
    }
  );
}

// Handle page unload - send leave event
window.addEventListener('beforeunload', function() {
  if (stomp && stomp.connected && hasJoined) {
    const user = getSessionUser();
    if (user) {
      stomp.send(`/app/liveStreams/${stream_id}/leave`, {}, JSON.stringify({id: user.id}));
    }
  }
});

// Also handle pagehide for better mobile support
window.addEventListener('pagehide', function() {
  if (stomp && stomp.connected && hasJoined) {
    const user = getSessionUser();
    if (user) {
      stomp.send(`/app/liveStreams/${stream_id}/leave`, {}, JSON.stringify({id: user.id}));
    }
  }
});

function handleServerMsg(msg) {
    displayChatMessage(msg);
}

function displayChatMessage(msg) {
    const chatOverlay = document.getElementById('chatOverlay');
    const messageElement = document.createElement('div');
    messageElement.classList.add('chat-message');
    messageElement.innerHTML = `<span class="sender">${msg.sender.name}:</span> <span class="message">${msg.msg}</span>`;
    chatOverlay.appendChild(messageElement);
    chatOverlay.scrollTop = chatOverlay.scrollHeight;
}

function displayNotification(msg) {
    const chatOverlay = document.getElementById('chatOverlay');
    const notificationElement = document.createElement('div');
    notificationElement.classList.add('chat-notification');
    notificationElement.textContent = `${msg.sender} ${msg.content}`;
    chatOverlay.appendChild(notificationElement);
    chatOverlay.scrollTop = chatOverlay.scrollHeight;
}


function sendChatMsg(user_id,msg){
  stomp.send(`/app/liveStreams/${stream_id}/chat`,{},JSON.stringify({
    id: stream_id,
    sender: {id: user_id},
    msg: msg
  }));
}

function updateViewerCount(count) {
  const viewerCountElement = document.getElementById('viewerCount');
  if (viewerCountElement) {
    const countValue = count || 0;
    viewerCountElement.textContent = `👁️ ${countValue} ${countValue === 1 ? 'viewer' : 'viewers'}`;
  }
}