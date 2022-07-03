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
router.post("/", (req, res) => {
    try{
        res.status(200).send(req.body.text)
    } catch {
        console.log(err)
        res.send(400).send(err)
    }
    
})