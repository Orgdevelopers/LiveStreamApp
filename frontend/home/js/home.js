window.addEventListener("load", function () {
  if (validateSession()) {
    loadLiveStreams();
  } else {
    showSnackbar(false, "Login expired: please login again");
    setTimeout(() => {
      this.window.location.assign("../");
    }, 1000);
  }
});

//Loading livestream list using REST
function loadLiveStreams() {
  apiRequest(
    "get",
    LIVESTREAM_LIST_URL,
    {},
    {
      onSuccess(result) {
        renderLiveStreams(result.data);
        connectToLiveSocket(); 
      },
      onError(error) {},
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

  streams.forEach(stream => {
    const card = document.createElement("div");
    card.classList.add("stream-card");

    card.innerHTML = `
      <div class="banner"></div>
      <div class="stream-info">
        <img src="assets/pfp-default.png" class="pfp" alt="pfp">
        <span class="username">${stream.username}</span>
      </div>
    `;

    // On click → open stream viewer page
    card.addEventListener("click", () => {
      window.location.assign("./watch.html?id=" + stream.id);
    });

    container.appendChild(card);
  });
}


function connectToLiveSocket() {
  const socket = new WebSocket("ws://localhost:8080/liveStreams");

  socket.onopen = () => console.log("Connected to live updates socket.");

  socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    console.log("Update:", data);

    // Examples of backend events
    if (data.event === "STREAM_STARTED") {
      addStreamToList(data.stream);
    } else if (data.event === "STREAM_ENDED") {
      removeStreamFromList(data.streamId);
    }
  };

  socket.onclose = () => console.log("Socket closed. Reconnecting in 3s...");
  socket.onerror = (err) => console.error("Socket error:", err);
}


function addStreamToList(stream) {
  const container = document.getElementById("live-streams");
  const card = document.createElement("div");
  card.classList.add("stream-card");
  card.innerHTML = `
    <div class="banner"></div>
    <div class="stream-info">
      <img src="assets/pfp-default.png" class="pfp" alt="pfp">
      <span class="username">${stream.username}</span>
    </div>`;
  container.appendChild(card);
}

function removeStreamFromList(streamId) {
  const container = document.getElementById("live-streams");
  const cards = container.querySelectorAll(".stream-card");
  cards.forEach(card => {
    if (card.dataset.id === streamId) card.remove();
  });
}
