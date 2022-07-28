const SocketServer = require("websocket").server;
const http = require("http");
const PORT = 8000;

const uri = "mongodb://127.0.0.1:27017";
const { MongoClient } = require("mongodb");
const client = new MongoClient(uri);
var ObjectId = require("mongodb").ObjectId;

const server = http.createServer((req, res) => {});
const coll = client.db("shopeer_database").collection("room_collection");

server.listen(PORT, () => {
  console.log(`Listening on port ${PORT}...`);
});

wsServer = new SocketServer({ httpServer: server });

const connections = [];

wsServer.on("request", (req) => {
  const connection = req.accept();
  console.log("new connection");
  connections.push(connection);

  // when new message is received from client
  connection.on("message", async (mes) => {
    // add the message to room_id
    var mssg_id = ObjectId();
    var data = JSON.parse(mes.utf8Data);
    var email = data.email;
    var text = data.text;
    var time = data.time;
    var room_id = data.room_id;
    console.log(data);

    try {
      // searches for a document with the following fields
      //appends an object to the "chathistory" array
      var doc = await coll.updateOne(
        { _id: ObjectId(room_id) },
        { $push: { chathistory: { mssg_id, email, text, time } } }
      );
      if (!doc) {
        console.log("Room not found.");
        return;
      }
      // console.log(doc);
      if (doc.modifiedCount === 1) {
        console.log(await coll.findOne({ _id: ObjectId(room_id) }));
        console.log("Message successfully posted.");
      } else {
        console.log("failed");
      }
    } catch (err) {
      console.log(err);
    }
    // notifies all other listeners
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
