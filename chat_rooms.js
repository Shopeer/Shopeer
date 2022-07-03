// chat.js - chat route module

// const express = require('express');
// const router = express.Router();
const { Router } = require('express');
const { ObjectId } = require('mongodb');
const router = Router();
router.get('/', handleRouting);
module.exports = router;

/**************** Room submodule **************** */

/**
 * Get Chatrooms GET https://shopeer.com/chat/room
 * Returns a list of all existing chat rooms
 * Body: user id token
 * Response: room_id list
 */
// what is the structure of the collection? how do we access all existing chat rooms of a user?
router.get("/", async (req, res) => {
    try {
        const result = client.db("shopeer_database").collection("room_collection").find({
            _id:ObjectId(req.query._id)
        })
        await result.forEach(console.dir)
        res.status(200).send("List of chat rooms retrieved successfully\n")
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

/**
 * Get Chatroom History GET https://shopeer.com/chat/room?room_id=[room_id]
 * To get the chat history of a specific chat room
 * Param: room_id
 * Body: user id token
 * Response: Chat history
 */
// what is the structure of a "room" document in the collection?
 router.get("/", async (req, res) => {
    try {
        const result = client.db("shopeer_database").collection("room_collection").find({
            _id:ObjectId(req.query._id)
        })
        await result.forEach(console.dir)
        res.status(200).send("Chat room history retrieved successfully \n")
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})


/**
 * Add peers to group chat PUT https://shopeer.com/chat/room?room_id=[chat_id]
 * Adds new peer to existing group
 * Param: room_id
 * Body: user id token AND peer Ids (to be added to chat)
 * Returns: success/fail
 */

 router.put("/", async (req, res) => {
    try {
        await client.db("shopeer_database").collection("room_collection").insertOne({
            "userid": req.body.userid,
            "message": req.body.message
        })
        res.status(200).send("New peer added to group chat\n")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

/**  
 * Create New Chatroom POST https://shopeer.com/chat/room
 * Creates a new group with the suggested members
 * Body: user id token, peer_id list
 * Response: room_id
*/
router.post("/", async (req, res) => {
    try {
        await client.db("shopeer_database").collection("room_collection").insertOne({
            "userid": req.body.userid,
            "peers": req.body.peers
        })
        res.status(200).send("New chatroom created\n")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

/** 
 * Remove Chatroom DELETE https://shopeer.com/chat/room?room_id=[room_id]
 * Deletes the chatroom and its history from the Room Collection
 * Response:success / fail
 */
 router.delete("/", async (req, res) => {
    try {
        await client.db("shopeer_database").collection("room_collection").deleteOne({
            "roomid": req.body.roomid
        })
        res.status(200).send("Room deleted/n")
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})