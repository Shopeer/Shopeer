const SocketServer = require("websocket").server;
const http = require("http");
const PORT = 8000;

const server = http.createServer((req, res) => {});

server.listen(PORT, () => {
  console.log(`Listening on port ${PORT}...`);
});

wsServer = new SocketServer({ httpServer: server });

const connections = [];

wsServer.on("request", (req) => {
  const connection = req.accept();
  console.log("new connection");
  connections.push(connection);

  connection.on("message", (mes) => {
    connections.forEach((element) => {
      //add another filter to only send to people with same roomId
      if (element != connection) element.sendUTF(mes.utf8Data);
    });
  });

  connection.on("close", (resCode, des) => {
    console.log("connection closed");
    connections.splice(connections.indexOf(connection), 1);
  });
});
