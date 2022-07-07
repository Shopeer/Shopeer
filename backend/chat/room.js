// chat.js - chat route module

const express = require('express');
const router = express.Router();
const uri = "mongodb://127.0.0.1:27017"
const {MongoClient} = require("mongodb")
const client = new MongoClient(uri)
var ObjectId = require('mongodb').ObjectId;

module.exports = router;
const coll = client.db("shopeer_database").collection("room_collection")
/**************** Room submodule **************** */

/**
 * Get Chatrooms GET https://shopeer.com/chat/room/all
 * Gets an array of all chat rooms that a user is present in
 * Body: {"email": <user email>}
<<<<<<< HEAD
<<<<<<< HEAD
 * Response: array of room_objects
=======
 * Response: array of room_ids
>>>>>>> 19de8ec... fixed message id
=======
 * Response: array of room_objects
>>>>>>> ebc87f9... deleted empty file
 */
// curl -X "GET" -H "Content-Type: application/json" -d '{"email": "gracemyzhang@gmail.com" }' localhost:8081/chat/room/all
router.get("/all", async (req, res) => {
    try {
        var roomsCursor = coll.find({
            "peerslist":{"$in":[req.body.email]} 
        })
        roomArr = []
        await roomsCursor.forEach(getRooms = (room) => {
<<<<<<< HEAD
<<<<<<< HEAD
            // roomArr.push(room._id)
            roomArr.push(room)
        })
    console.log(roomArr)
=======
            roomArr.push(room._id)
        })
    console.log("\n User " + req.body.email + " is present in the following rooms: \n " + roomArr)
>>>>>>> 19de8ec... fixed message id
=======
            // roomArr.push(room._id)
            roomArr.push(room)
        })
    console.log(roomArr)
>>>>>>> ebc87f9... deleted empty file
    res.status(200).send(roomArr)
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

/**
<<<<<<< HEAD
<<<<<<< HEAD
 * Get Chatroom History GET https://shopeer.com/chat/room/history?room_id=[room_id]
=======
 * Get Chatroom History GET https://shopeer.com/chat/room?room_id=[room_id]
>>>>>>> 19de8ec... fixed message id
=======
 * Get Chatroom History GET https://shopeer.com/chat/room/history?room_id=[room_id]
>>>>>>> 2284192... added GET room_summary request
 * Gets the chat history of a specific chat room
 * Param: room_id
 * Body: none (M4: user email)
 * Response: array containing Message Objects
 */
// curl -X "GET" -H "Content-Type: application/json" -d localhost:8081/chat/room?room_id=62c4bb1ba6c3f54d76bdf6f8
<<<<<<< HEAD
<<<<<<< HEAD
 router.get("/history", async (req, res) => {
=======
 router.get("/", async (req, res) => {
>>>>>>> 19de8ec... fixed message id
=======
 router.get("/history", async (req, res) => {
>>>>>>> 2284192... added GET room_summary request
    try {
        var doc = await coll.findOne({ _id: ObjectId(req.query.room_id)})
        doc.chathistory.forEach(printMssgs = (mssg) => {
            console.log("Message: " + mssg.text)
            console.log("Time: " + mssg.time + "\n")
        })

        res.status(200).send(doc.chathistory)
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
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
router.put("/", async (req, res) => {
    try {
        var doc = await coll.updateOne(
            {_id: ObjectId(req.query.room_id)},
            {$addToSet: {"peerslist": req.body.email}}
        )
        if (doc.matchedCount == 0) {
            res.status(200).send("\nCould not find this room.\n")
        } else if (doc.modifiedCount == 0) {
            res.status(200).send("\n" + req.body.email + " is already a member of this room\n")
        } else {
            res.status(200).send("\n" + req.body.email + " added to room\n")
        }
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

/**
 * Delete peer from group chat DELETE https://shopeer.com/chat/room/remove_user?room_id=[room_id]
 * Deletes peer from group
 * Body: peer email
 * Returns: success/fail
 */
// curl -X "DELETE" -H "Content-Type: application/json" -d '{"email": "sally@gmail.com" }' localhost:8081/chat/room/remove_user?room_id=62c4d5c76896713a30649546
router.delete("/remove_user", async (req, res) => {
    try {
        var doc = await coll.updateOne(
            {_id: ObjectId(req.query.room_id)},
            {$pull: {"peerslist": req.body.email}}
        )
        if (doc.matchedCount == 0) {
            res.status(200).send("\nCould not find this room.\n")
        } else if (doc.modifiedCount == 0) {
            res.status(200).send("\n" + req.body.email + " is not a member of this room\n")
        } else {
            res.status(200).send("\n" + req.body.email + " removed from room\n")
        }
        
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})


/**  
 * Create New Chatroom POST https://shopeer.com/chat/room
 * Creates a new group with the suggested members
 * Body: user id token, peer_id list
 * Response: room_id
*/
//curl -X "POST" -H "Content-Type: application/json" -d '{"name": "anotherRoom", "peerslist": ["nick@gmail.com", "hellbb@msn.com", "grace@gmail.com"], "chathistory": [{"123213": "hey", "text": "hello world", "time": "3pm"}, {"id": "14214", "text": "my message", "time": "5pm"}] }' localhost:8081/chat/room
//curl -X "POST" -H "Content-Type: application/json" -d '{"name": "room", "peerslist": ["nando@gmail.com","grace@gmail.com"], "chathistory": []}' localhost:8081/chat/room
router.post("/", async (req, res) => {
    try {

        var doc = await coll.insertOne({
            "name": req.body.name,
<<<<<<< HEAD
<<<<<<< HEAD
            // "picture": req.body.picture,
=======
>>>>>>> 19de8ec... fixed message id
=======
            // "picture": req.body.picture,
>>>>>>> 2284192... added GET room_summary request
            "peerslist": req.body.peerslist,
            "chathistory": req.body.chathistory
        })
        console.log("\n New chatroom " + req.body.name + " created with id " + doc.insertedId)
        res.status(200).send(doc.insertedId)
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 2284192... added GET room_summary request
/**  
 * Get chatroom summary GET https://shopeer.com/chat/room/summary?room_id=[room_id]
 * Body: none
 * Response: roomSummaryObject containing name, picture, and last message object
*/
//curl -X "GET" -H "Content-Type: application/json" -d '' localhost:8081/chat/room/summary?room_id=62c4bb1ba6c3f54d76bdf6f8
router.get("/summary", async (req, res) => {
    try {
        var doc = await coll.findOne({ _id: ObjectId(req.query.room_id)})
        var roomSummary = {
            "name": doc.name,
            //"picture": doc.picture,
            "lastmessage": doc.chathistory[doc.chathistory.length-1]
        }
        res.status(200).send(roomSummary)
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

<<<<<<< HEAD
=======
>>>>>>> 19de8ec... fixed message id
=======
>>>>>>> 2284192... added GET room_summary request
/** 
 * Remove Chatroom DELETE https://shopeer.com/chat/room?room_id=[room_id]
 * Deletes the chatroom and its history from the Room Collection
 * Response:success / fail
 */
// curl -X "DELETE" -H "Content-Type: application/json" -d '' localhost:8081/chat/room?room_id=62c4ac94ee79eff89f8ac0bc
 router.delete("/", async (req, res) => {
    try {
        var doc = await coll.deleteOne({
            _id: ObjectId(req.query.room_id)
        })
        if (doc.deletedCount == 1) {
            res.status(200).send("\nRoom deleted\n")
        } else {
            res.status(200).send("\nroom does not exist.\n")
        }
        
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

