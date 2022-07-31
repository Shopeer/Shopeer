// chat.js - chat route module

const express = require("express");
const router = express.Router();
const uri = "mongodb://127.0.0.1:27017";
const { MongoClient } = require("mongodb");
const client = new MongoClient(uri);
var ObjectId = require("mongodb").ObjectId;

module.exports = router;
const coll = client.db("shopeer_database").collection("room_collection");
var user_collection = require('../config/mongodb_connection')
/**************** Room submodule **************** */

/**
 * Get Chatrooms GET https://shopeer.com/chat/room/all?email=[email]
 * Gets an array of all chat rooms that a user is present in
 * Body: {"email": <user email>}

 * Response: array of room_objects

 */
// curl -X "GET" -H "Content-Type: application/json" -d '{"email": "gracemyzhang@gmail.com" }' localhost:8081/chat/room/all
router.get("/all", async (req, res) => {
  var roomArr = []
  let userCursor = await user_collection.findOne({ email: req.query.email }) 
  if (!userCursor) {
    res.status(404).json({ response: "User not found." })
    return
  }
  let roomsCursor = await coll.find({peerslist: { $in: [req.query.email] },})
  await roomsCursor.forEach((getRooms = (room) => {roomArr.push(room);}))
  res.status(200).send(roomArr)
  
});

/**

 * Get Chatroom History GET https://shopeer.com/chat/room/history?room_id=[room_id]
 * Gets the chat history of a specific chat room
 * Param: room_id
 * Body: none (M4: user email)
 * Response: array containing Message Objects
 */
// curl -X "GET" -H "Content-Type: application/json" -d localhost:8081/chat/room?room_id=62c4bb1ba6c3f54d76bdf6f8

router.get("/history", async (req, res) => {

  // gets message history of a particular room
  // consider some form of authentication?

  if (!ObjectId.isValid(req.query.room_id)) {
    res.status(400).json({ response: "Invalid room id." })
    return
  }
  var doc = await coll.findOne({ _id: ObjectId(req.query.room_id) });
  if (!doc) {
    res.status(404).json({ response: "Room not found." })
    return
  }
  // doc.chathistory.forEach(
  //   (printMssgs = (mssg) => {
  //     console.log("Message: " + mssg.text);
  //     console.log("Time: " + mssg.time + "\n");
  //   })
  // );

  res.status(200).send(doc.chathistory);
  })
/**
 * Add peers to group chat PUT https://shopeer.com/chat/room?room_id=[room_id]
 * Adds new peer to existing group
 * Param: room_id
 * Body: user id token AND peer Ids (to be added to chat)
 * Returns: success/fail
 */
// we could insert only the email, or insert the entire user object. For now it inserts the email.
// curl -X "PUT" -H "Content-Type: application/json" -d '{"email": "hello@gmail.com" }' localhost:8081/chat/room?room_id=62c4d5c76896713a30649546
// router.put("/", async (req, res) => {
//   var doc = null
//   try {
//     doc = await coll.updateOne(
//       { _id: ObjectId(req.query.room_id) }, { $addToSet: { peerslist: req.body.email } }
//     );
//     if (doc.matchedCount === 0) {
//       res.status(200).send("\nCould not find this room.\n");
//     } else if (doc.modifiedCount === 0) {
//       res
//         .status(200)
//         .send("\n" + req.body.email + " is already a member of this room\n");
//     } else {
//       res.status(200).send("\n" + req.body.email + " added to room\n");
//     }
//   } catch (err) {
//     console.log(err);
//     res.status(400).send(err);
//   }
// });

/**
 * Delete peer from group chat DELETE https://shopeer.com/chat/room/remove_user?room_id=[room_id]
 * Deletes peer from group
 * Body: peer email
 * Returns: success/fail
 */
// curl -X "DELETE" -H "Content-Type: application/json" -d '{"email": "sally@gmail.com" }' localhost:8081/chat/room/remove_user?room_id=62c4d5c76896713a30649546
// router.delete("/remove_user", async (req, res) => {
//   var doc = null
//   try {
//     doc = await coll.updateOne(
//       { _id: ObjectId(req.query.room_id) }, { $pull: { peerslist: req.body.email } }
//     );
//     if (doc.matchedCount === 0) {
//       res.status(200).send("Could not find this room.");
//     } else if (doc.modifiedCount === 0) {
//       res
//         .status(200)
//         .send("\n" + req.body.email + " is not a member of this room\n");
//     } else {
//       res.status(200).send("\n" + req.body.email + " removed from room\n");
//     }
//   } catch (err) {
//     console.log(err);
//     res.status(400).send(err);
//   }
// });

/**
 * Create New Chatroom POST https://shopeer.com/chat/room
 * Creates a new group with the suggested members
 * Body: user id token, peer_id list
 * Response: room_id
 */
//curl -X "POST" -H "Content-Type: application/json" -d '{"name": "anotherRoom", "peerslist": ["nick@gmail.com", "hellbb@msn.com", "grace@gmail.com"], "chathistory": [{"123213": "hey", "text": "hello world", "time": "3pm"}, {"id": "14214", "text": "my message", "time": "5pm"}] }' localhost:8081/chat/room
//curl -X "POST" -H "Content-Type: application/json" -d '{"name": "room", "peerslist": ["nando@gmail.com","grace@gmail.com"], "chathistory": []}' localhost:8081/chat/room
router.post("/", async (req, res) => {
  var name = req.body.name
  var peerslist = req.body.peerslist
  var chathistory = req.body.chathistory
  if ( peerslist == null || peerslist.length !== 2 ) {
    res.status(400).json({ response: "Invalid peerslist." });
    return;
  }
  if ( name == null || name == "" ) {
    res.status(400).json({ response: "Missing room name." });
    return;
  }

  var user0 = await user_collection.findOne({ email: peerslist[0] })
  var user1 = await user_collection.findOne({ email: peerslist[1] })
  if (!user0 || !user1) {
    res.status(404).json({ response: "Users not found." });
    return;
  }
  if (!user0.peers.includes(peerslist[1])) {
    res.status(400).json({ response: "Users are not peers." });
    return;
  }

  var doc = await coll.insertOne({
    name,
    peerslist,
    chathistory
  });

  res.status(201).send(doc);
    
});

/**
 * Get chatroom summary GET https://shopeer.com/chat/room/summary?room_id=[room_id]
 * Body: none
 * Response: roomSummaryObject containing name, picture, and last message object
 */
//curl -X "GET" -H "Content-Type: application/json" -d '' localhost:8081/chat/room/summary?room_id=62c4bb1ba6c3f54d76bdf6f8
router.get("/summary", async (req, res) => {
  if (!ObjectId.isValid(req.query.room_id)) {
    res.status(400).json({ response: "Invalid room id." });
    return
  }
  var doc = await coll.findOne({ _id: ObjectId(req.query.room_id) });
  if (!doc) {
    res.status(404).json({ response: "Room not found." });
    return
  }
  var roomSummary = {
    name: doc.name,
    //"picture": doc.picture,
    lastmessage: doc.chathistory[doc.chathistory.length - 1],
  };
  res.status(200).send(roomSummary);

});

/**
 * Remove Chatroom DELETE https://shopeer.com/chat/room?room_id=[room_id]
 * Deletes the chatroom and its history from the Room Collection
 * Response:success / fail
 */
// curl -X "DELETE" -H "Content-Type: application/json" -d '' localhost:8081/chat/room?room_id=62c4ac94ee79eff89f8ac0bc
router.delete("/", async (req, res) => {
  
  if (!ObjectId.isValid(req.query.room_id)) {
    res.status(400).json({ response: "Invalid room id." });
    return
    // if the query is not a room id, check if it is a user email
    // var user = await user_collection.findOne({ email: req.query.room_id })
    // if (!user) {
    //   res.status(400).json({ response: "Invalid room id." });
    //   return
    // }
    // var update = await coll.deleteMany(
    //   {peerslist: {$elemMatch: {$in: [req.query.room_id]}}}
    // )
    // var count = update.deletedCount
    // res.status(200).send("deleted " + count + " rooms")
    // return
  }
  var doc = await coll.deleteOne({
    _id: ObjectId(req.query.room_id),
  });
  
  if (doc.deletedCount === 1) {
    res.status(200).json({ response: "Deleted room." });
    
  } else {
    res.status(404).json({ response: "Room not found." });

  }

});

