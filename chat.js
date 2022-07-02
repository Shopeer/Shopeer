// chat.js - chat route module

const express = require('express');
const router = express.Router();


/**************** Room submodule **************** */

/**
 * Get Chatrooms GET https://shopeer.com/chat/room
 * Returns a list of all existing chat rooms
 * Body: user id token
 * Response: room_id list
 */
router.get("/room", async (req, res) => {
    try {
        const result = client.db("database").collection("message").find(req.body)
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
 router.get("/room", async (req, res) => {
    try {
        const result = client.db("database").collection("message").find(req.body)
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
 router.put("/room", async (req, res) => {
    try {
        await client.db("database").collection("message").insertOne(req.body)
        // status 200: success
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

/** 
 * Remove Chatroom DELETE https://shopeer.com/chat/room?room_id=[room_id]
 * Deletes the chatroom and its history from the Room Collection
 * Response:success / fail
 */





/**************** Message submodule **************** / 
/*
Post message POST https://shopeer.com/chat/message?chat_id=[chat_id]
Send a chat message to peer / group
Param: Chat Id
Body: user id token AND Message
Response: success/fail
*/ 
// curl -X "POST" -H "Content-Type: application/json" -d '{"task":"finish this tutorial", "status":"rip"}' localhost:8081/todolist
router.post("/message", (req, res) => {
    res.status(200).send(req.body.text)
})