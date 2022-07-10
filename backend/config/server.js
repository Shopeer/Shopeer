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


const searches_router = require('../match/searches');
app.use('*', searches_router);
app.use('/match', searches_router)

const suggestions_algo_router = require('../match/suggestions_algo.js');
app.use('*', suggestions_algo_router);
app.use('/match', suggestions_algo_router)

const roomsRouter = require('../chat/room');
app.use('/chat/room', roomsRouter)

const mssgRouter = require('../chat/message');
app.use('/chat/message', mssgRouter)

// // local vm
/// const IP = '192.168.64.15';
// const PORT = 3000;

// azure vm
const IP = "20.230.148.126";
const PORT = "8080";




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

//-------------------------------------------------------------------------------
// Database Module: User Collection Submodule

// Add New User POST https://shopeer.com/userDB/newUser
// Creates and adds a new user into the User Database along with a new user_id
// Body: User gmail
// Response: New user_id, new user id token



// router.post("/user/register", async (req, res) => {
//     try {
//         // var result_debug = await mongoClient.db("shopeer_database").collection("user_collection").insertOne(req.body)
        
//         console.log(req.body)
        
//         var objectId = req.body._id; 
//         res.status(200).send(objectId)
//     } catch (err) {
//         console.log(err)
//         res.send(400).send(err)
//     }
// })


// app.post("/user/newuser", async (req, res) => {
//     try {
//         var result_debug = await mongoClient.db("shopeer_database").collection("user_collection").insertOne(req.body)
//         var objectId = req.body._id; 
//         res.status(200).send(objectId)
//     } catch (err) {
//         console.log(err)
//         res.send(400).send(err)
//     }
// })

// Check authorization POST https://shopeer.com/userDB/auth
// Checks authorization of user
// Body: User Id Token AND User details
// Response: Pass/fail
// NOTE: skipping as we decided to do google account auth


// Retrieve User GET https://shopeer.com/userDB?user_id=[id]
// Retrieves user from User Database with user_id
// Body: User Id Token AND User details
// Response: Success/Fail

app.get("/user/getuser", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find({_id:ObjectId(req.query._id)})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


// Retrieve Queried User List GET https://shopeer.com/userDB?searchQuery=[query] 
// Retrieves list of users objects from User Database that match the  query parameter
// Param: query
// Body: User Id Token
// Response: user object list
// NOTE: Not needed as mongodb has query functionality


// Add New Search POST https://shopeer.com/userDB/newSearch
// Adds new search for the user in User Collection
// Body: user_id, location, distance, activity, budget
// Reponse: success/fail
// NOTE: Need to figure out how search algo will work


// Update Profile POST https://shopeer.com/userDB/updateProfile?user_id=[id]
// Updates user object in the User Collection with the new profile information
// Param: user_id
// Body: user id token AND New profile info {profile_pic, name, bio}
// Response: success/fail

app.put("/user/updateprofile", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:ObjectId(req.query._id)})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// TODO
// Remove Peer DELETE https://shopeer.com/userDB/removePeer
// Deletes specified peer from the peers list of the user
// Body: user id token, user_id, peer_id
// Response:success / fail

app.delete("/roomDeleteOne", async (req, res) => {
    try {
        console.log(req.query._id)
        var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({_id:ObjectId(req.query._id)})
        // var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({_id:ObjectId("62c0a5ff4c183de8407cb91b")})
        // var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({roomname: 'test_chatname2'})
        
        // console.log({_id:new mongodb.ObjectId("62bf9de0c9211571d293fdc5")})
        console.log(result_debug)
        res.status(200).send(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// TODO
// Add Sent Invitation POST https://shopeer.com/userDB/addInvite
// Adds the invitation sent by the user in user’s “sent invitation” list and in peer’s “recieved invitation” list
// Body: user id token, user_id, peer_id
// Response:success / fail

app.put("/addInvitation", async (req, res) => {
    try {
        const result_debug = await mongoClient.db("shopeer_database").collection("user_collection").replaceOne({ "task": 'Tutorial' }, req.body)
        res.status(200).send("item modified successfully\n")
        console.log(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// TODO
// Decline Invitation POST https://shopeer.com/userDB/declineInvite
// Declines an invite from a peer by removing the invite from user's “recieved invitation” list and peer’s “sent invitation” list
// Body: user id token, user_id, peer_id
// Response:success / fail

app.put("/declineInvite", async (req, res) => {
    try {
        const result_debug = await mongoClient.db("shopeer_database").collection("user_collection").replaceOne({ "task": 'Tutorial' }, req.body)
        res.status(200).send("item modified successfully\n")
        console.log(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// TODO
// Accept Invitation POST https://shopeer.com/userDB/acceptInvite
// Accepts an invite from a peer by removing the invite from user's “received invitation” list and peer’s “sent invitation” list, and adding peer_id to “peers ids” list
// Body: user id token, user_id, peer_id
// Response:success / fail

app.put("/acceptInvite", async (req, res) => {
    try {
        const result_debug = await mongoClient.db("shopeer_database").collection("user_collection").replaceOne({ "task": 'Tutorial' }, req.body)
        res.status(200).send("item modified successfully\n")
        console.log(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

//-------------------------------------------------------------------------------
// Database Module: Room Collection Submodule

// Add Chatroom PUT https://shopeer.com/roomDB
// Adds a chatroom with the list of users specified in the Room Collection, and returns a room_id if action was successful
// Body: User Id Token, list of user_ids
// Response: room_id
app.post("/roomInsertOne", async (req, res) => {
    try {
        var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").insertOne(req.body)
        var objectId = req.body._id; 
        res.status(200).send(objectId)
        // console.log(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// Get All Chatrooms GET https://shopeer.com/roomDB
// Gets all chat rooms that a particular user is in.
// Body: User Id Token AND User details
// Response: List of room ids

app.get("/getAllRooms", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find({roomname:{$exists: true}})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)


        // for(let i = 0; i < 2; i++){
        //     if (find_cursor.hasNext() == false) {
        //         break;
        //     }
        //     console.log(find_cursor.next())
        // }
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


app.get("/getOneRoom", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find({_id:ObjectId(req.query._id)})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


// Post message POST  https://shopeer.com/roomDB/message?room_id=[room_id]
// Send a chat message to peer/ group
// Param: Chat Id
// Body:  user id token AND Message
// Response: success/fail

app.post("/messageInsertOne", async (req, res) => {
    try {
        var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").insertOne(req.body)
        res.status(200).send("Success")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// let date = new Date()
// date = date.toLocaleString('en-US', {timeZone: 'America/Los_Angeles'})
// let local_date = new Date(date)
// let local_time = local_date.getHours() + ":" + local_date.getMinutes() + ":" + local_date.getSeconds();


// Remove Chatroom DELETE https://shopeer.com/roomDB?room_id=[room_id]
// Param: room_id (passed as req.body)
// Response: success/fail
// http://192.168.64.15:3000/roomDeleteOne?_id=62c0a5fd4c183de8407cb919
app.delete("/roomDeleteOne", async (req, res) => {
    try {
        console.log(req.query._id)
        var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({_id:ObjectId(req.query._id)})
        // var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({_id:ObjectId("62c0a5ff4c183de8407cb91b")})
        // var result_debug = await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({roomname: 'test_chatname2'})
        
        // console.log({_id:new mongodb.ObjectId("62bf9de0c9211571d293fdc5")})
        console.log(result_debug)
        res.status(200).send(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


app.put("/replaceOne", async (req, res) => {
    try {
        const result_debug = await mongoClient.db("shopeer_database").collection("room_collection").replaceOne({ "task": 'Tutorial' }, req.body)
        res.status(200).send("item modified successfully\n")
        console.log(result_debug)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.delete("/deleteOne", async (req, res) => {
    try {
        await mongoClient.db("shopeer_database").collection("room_collection").deleteOne({ "task": req.body.task })
        res.status(200).send("item deleted successfully\n")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

app.get("/find", async (req, res) => {
    try {
        const result = await mongoClient.db("shopeer_database").collection("room_collection").find(req.body)
        await result.forEach(console.dir)
        res.send("items retrieved\n")
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

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
      res.send(400).send(err)
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
      res.send(400).send(err)
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
      res.send(400).send(err)
  }
})

async function run() {
  try {
      await mongoClient.connect()  // waits for sync op to finish
      console.log("Successfully connected to database")
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
      count_result = await mongoClient.db("shopeer_database").collection("room_collection").countDocuments({ testkey: "testdefinition" })
      if (count_result < 1) {
          const insertResult = await mongoClient.db("shopeer_database").collection("room_collection").insertOne({ testkey: "testdefinition" })
          console.log(insertResult)
      }
  } catch (err) {
      console.log(err)
  }
}

run()