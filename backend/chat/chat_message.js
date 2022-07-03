// chat.js - chat route module

// const express = require('express');
// const router = express.Router();
const { Router } = require('express');
const router = Router();
router.get('/', handleRouting);
module.exports = router;




/**************** Message submodule **************** / 
/*
Post message POST https://shopeer.com/chat/message?chat_id=[chat_id]
Send a chat message to peer / group
Param: Chat Id
Body: user id token AND Message
Response: success/fail
*/ 
// this probably inserts a new room instead of posting a message
router.post("/", (req, res) => {
    try{
        await client.db("shopeer_database").collection("room_collection").insertOne({
            sender: req.body.sender,
            message: req.body.message
        })
        res.status(200).send("message sent")
    } catch {
        console.log(err)
        res.send(400).send(err)
    }
    
})