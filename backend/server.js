const http = require('http');


var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()
app.use(express.json())


const { MongoClient } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

// const mongoose = require("mongoose")
// mongoose.connect(uri)
// const userSchema = new mongoose.Schema({
// })
// mongoose.model("namedb", userSchema)

// address of this vm: 192.168.64.8:8081
// address of Azure vm is: 40.122.233.185


const IP = '192.168.64.15';
const PORT = 3000;

// const IP = "40.122.233.185";
// const PORT = "8081";

var server = null;

app.use(express.json())

app.get("/", (req, res) => {
    res.send("Hello world\n")
})

app.post("/", (req, res) => {
    res.send(req.body.text)
})


let obj_user1 = {username: "test_user1"}
let obj_user2 = {username: "test_user2"}

let obj_chat1 = {chatname: "test_chatname1", chathistory: null}
let obj_chat2 = {chatname: "test_chatname2", chathistory: null}


app.post("/insertOne", async (req, res) => {
    try {
        await mongoClient.db("shopeer_database").collection("chat_collection").insertOne(req.body)
        res.status(200).send("item added successfully\n")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(rr)
    }
})

app.put("/replaceOne", async (req, res) => {
    try {
        await mongoClient.db("shopeer_database").collection("chat_collection").replaceOne({ "task": 'Tutorial' }, req.body)
        res.status(200).send("item modified successfully\n")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(rr)
    }
})

app.delete("/deleteOne", async (req, res) => {
    try {
        await mongoClient.db("shopeer_database").collection("chat_collection").deleteOne({ "task": req.body.task })
        res.status(200).send("item deleted successfully\n")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(rr)
    }
})

app.get("/find", async (req, res) => {
    try {
        const result = await mongoClient.db("shopeer_database").collection("chat_collection").find(req.body)
        await result.forEach(console.dir)
        res.send("items retrieved\n")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(rr)
    }
})

// For debug only
app.get("/test", async (req, res) => {
  try {
      const result_debug = await mongoClient.db("shopeer_database").collection("chat_collection").find({}).toArray()
      console.log(result_debug[0]["testkey"])
      res.send(result_debug[0]["testkey"] + "\n");
  }
  catch (err) {
      console.log(err)
      res.send(400).send(rr)
  }
})

app.get("/server_ip", async (req, res) => {
  try {
      var host = server.address().address
      // res.status(400).send("Server IP: " + IP + "\n")
      res.send(IP)
  }
  catch (err) {
      console.log(err)
      res.send(400).send(rr)
  }
})

app.get("/server_time", async (req, res) => {
  try {
      let date = new Date()
      date = date.toLocaleString('en-US', {timeZone: 'America/Los_Angeles'})

      let local_date = new Date(date)
      let local_time = local_date.getHours() + ":" + local_date.getMinutes() + ":" + local_date.getSeconds();
      res.send(local_time + "\n")
  }
  catch (err) {
      console.log(err)
      res.send(400).send(rr)
  }
})

async function run() {
  try {
      await mongoClient.connect()  // waits for sync op to finish
      console.log("Successfully connected to the database")
      // var server = app.listen(8081, '0.0.0.0', function () {
      server = app.listen(PORT, function () {
          var host = server.address().address
          var port = server.address().port
          console.log("Example app running at http://%s:%s", host, port)
      })


  }
  catch (err) {
      console.log(err)
      await mongoClient.close()
  }

  // Ensure that a test document exists in database
  try {
      count_result = await mongoClient.db("shopeer_database").collection("chat_collection").countDocuments({ testkey: "testdefinition" })
      if (count_result < 1) {
          const insertResult = await mongoClient.db("shopeer_database").collection("chat_collection").insertOne({ testkey: "testdefinition" })
          console.log(insertResult)
      }
  } catch (err) {
      console.log(err)
  }
}

run()

