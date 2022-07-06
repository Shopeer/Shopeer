
const express = require('express');
const router = express.Router();
const uri = "mongodb://127.0.0.1:27017"
const {MongoClient} = require("mongodb")
const client = new MongoClient(uri)
var ObjectId = require('mongodb').ObjectId;

module.exports = router;
const coll = client.db("shopeer_database").collection("room_collection")

/**
 * Post message POST  https://shopeer.com/chat/message?chat_id=[chat_id]
 * Send a chat message to peer/ group
 * Param: room id (M4: message id)
 * Body:  sender email AND message text AND send time
 * Response: message id
*/
//curl -X "POST" -H "Content-Type: application/json" -d '{"email": "nick@gmail.com", "text": "sup!", "time": "rn"}' localhost:8081/chat/message?room_id=62c4b6f309f37ad6591198b7
router.post("/", async (req, res) => {
    try {
        var doc = await coll.updateOne(
            // searches for a document with the following fields
            {_id: ObjectId(req.query.room_id)},
            //appends an object to the "chathistory" array
            {$push: {
                "chathistory": 
                    {
                        "email": req.body.email,
                        "text": req.body.text,
                        "time": req.body.time // frontend will send as long
                    }
                }
            }
        )
        console.log("\n mssg from " + req.body.email + " added to group chat with id " + doc.insertedId)
        res.status(200).send(doc.insertedId)
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})