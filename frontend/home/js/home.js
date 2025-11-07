window.addEventListener("load", function () {
  if (validateSession()) {
    loadLiveStreams();
  } else {
    showSnackbar(false, "Login expired: please login again");
    setTimeout(() => {
      this.window.location.assign("../");
    }, 500);
  }
});

//variables
let streamsArray = Array();

//Loading livestream list using REST
function loadLiveStreams() {
  apiRequest(
    "get",
    LIVESTREAM_LIST_URL,
    {},
    {
      onSuccess(result) {
        //streamsArray.push(result.response.data);
        renderLiveStreams(result.response.data);
        connectToLiveSocket();
      },
      onError(err) {
        try {
          showSnackbar(false, err.responseJSON.msg);
          return;
        } catch (err) {}

        showSnackbar(false, "something went wrong");
      },
    }
  );
}

function renderLiveStreams(streams) {
  const container = document.getElementById("live-streams");
  container.innerHTML = "";

  if (!streams || streams.length === 0) {
    container.innerHTML = "<p>No live streams right now.</p>";
    return;
  }

  streams.forEach((stream) => {
    addStreamToList(stream);
  });
}

function connectToLiveSocket(retryCount = 0) {
  const socket = new SockJS(LIVESTREAMLIST_SOCKET_URL);
  const stomp = Stomp.over(socket);

  stomp.connect(
    {},
    function () {
      stomp.subscribe("/updates/streams", function (message) {
        const stream = JSON.parse(message.body);
        log("New stream update:", stream);
        if (stream.active == true) {
          //stream is active
          addStreamToList(stream);
        } else {
          //stream closed
          removeStreamFromList(stream.id);
        }
      });
    },
    function () {
      if (retryCount < 5) {
        setTimeout(() => connectToLiveSocket(retryCount + 1), 3000);
        log("Reconnecting socket... Attempt " + (retryCount + 1));
      } else {
        showSnackbar(false, "Failed to reconnect to live updates");
      }
    }
  );
}

function addStreamToList(stream) {
  if (streamExists(stream.id)) {
    log("stream already added id: " + stream.id);
    return;
  }

  const container = document.getElementById("live-streams");
  const card = document.createElement("div");
  card.classList.add("stream-card");
  card.classList.add("card_id_" + stream.id);

  card.innerHTML = `
      <div class="banner"><img class="banner-img" src="${stream.banner}"></div>
      <div class="stream-info">
        <img src="../assets/img/pfp-default.png" class="pfp" alt="pfp">
        <span class="username">${stream.user.username}</span>
      </div>
    `;

  // On click → open stream viewer page
  card.addEventListener("click", () => {
    window.location.assign("../watch?id=" + stream.id);
  });

  streamsArray.push({ id: stream.id, element: card });
  container.appendChild(card);
}

function removeStreamFromList(streamId) {
  const item = streamsArray.find((i) => i.id === streamId);
  if (item) {
    item.element.remove();
    streamsArray = streamsArray.filter((i) => i.id !== streamId);
  }
}

// function streamExists(stream){
//   if(streamsArray.some(item => item === stream)){
//     //log("item id"+item.id+"  stream id "+ stream.id);
//     return true;
//   }

//   return false;

// }

function streamExists(streamId) {
  return streamsArray.some((item) => item.id === streamId);
}
