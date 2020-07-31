var connections = 0;
var sources = {};

onconnect = function (e) {
    let port = e.ports[0];
    connections++;

    port.addEventListener("message", function (e) {
        let url = e.data;

        if (!sources.hasOwnProperty(url)) {
            sources[url] = new EventSource(url);
        }

        sources[url].addEventListener("message", (message) => {
            console.clear();
            console.log(message);
            port.postMessage({url: url, data: message.data});
        }, false);

    }, false);

    port.start();
}

onbeforeunload = function () {
    connections--;
    if (connections === 0) {
        self.terminate();
    }
}