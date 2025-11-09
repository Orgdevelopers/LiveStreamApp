const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let stream_id = 0;


window.addEventListener("load",()=>{
    if (validateSession() && urlParams.has("id")) {
        //load
        stream_id = urlParams.get("id");
        joinLiveStream();

        return;
    }

    showSnackbar(false, "Login expired: please login again");

    setTimeout(() => {
        window.location.assign("../");
    }, 300);
});


function joinLiveStream(){
    connectToLiveSocket();

}

let socket = null;
let stomp = null;
function connectToLiveSocket(retryCount = 0) {
  socket = new SockJS(LIVESTREAMLIST_SOCKET_URL);
  stomp = Stomp.over(socket);

  stomp.connect(
    {},
    function () {
      stomp.subscribe(`/liveStreams/${stream_id}/chat`, function (message) {
        const msg = JSON.parse(message.body);
        if (msg && msg.liveStream.id == stream_id && msg.sender.id != getSessionId()) {
          handleServerMsg(msg);
        }else{
          log("something wrong");
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


function sendChatMsg(user_id,msg){
  stomp.send(`/app/liveStreams/${stream_id}/chat`,{},JSON.stringify({
    id: stream_id,
    sender: {id: user_id},
    msg: msg
  }));
}