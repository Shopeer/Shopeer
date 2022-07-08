const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()


const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

console.log({ MongoClient, ObjectId })

var server = null;

app.use(express.json())

app.get("/", (req, res) => {
    res.send("Hello world\n")
})
app.post("/", (req, res) => {
    res.send(req.body.text)
})

// Express Routers
const user_profile_router = require('../user/profile.js');
app.use('*', user_profile_router);
app.use('/user', user_profile_router)

const user_peers_router = require('../user/peers.js');
app.use('*', user_peers_router);

app.use('/user', user_peers_router)


const searches_router = require('../match/searches.js');
app.use('*', searches_router);
app.use('/match', searches_router)

const suggestions_algo_router = require('../match/suggestions_algo.js');
app.use('*', suggestions_algo_router);
app.use('/match', suggestions_algo_router)

// local vm
const IP = '192.168.64.15';
const PORT = 3000;

// azure vm
// const IP = "20.230.148.126";
// const PORT = "8080";

//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------

// For debug only
app.get("/test", async (req, res) => {
  try {
      const result_debug = await mongoClient.db("shopeer_database").collection("room_collection").find({}).toArray()
      console.log(result_debug[0]["testkey"])
      res.send(result_debug[0]["testkey"] + "\n");
  }
  catch (err) {
      console.log(err)
      res.status(400).send(err)
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
      res.status(400).send(err)
  }
})


// let date = new Date()
// date = date.toLocaleString('en-US', {timeZone: 'America/Los_Angeles'})
// let local_date = new Date(date)
// let local_time = local_date.getHours() + ":" + local_date.getMinutes() + ":" + local_date.getSeconds();
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
      res.status(400).send(err)
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
}

run()
