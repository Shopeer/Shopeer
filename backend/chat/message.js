
const express = require('express');
const router = express.Router();
const uri = "mongodb://127.0.0.1:27017"
const {MongoClient} = require("mongodb")
const client = new MongoClient(uri)
var ObjectId = require('mongodb').ObjectId;
// var admin = require("firebase-admin");

// var serviceAccount = require("../cpen-shopeer-firebase-adminsdk-i0ot8-ffcaa4fefb.json");

// admin.initializeApp({
//   credential: admin.credential.cert(serviceAccount),
//  // databaseURL: "<your database URL here>"
// });



module.exports = router;
const coll = client.db("shopeer_database").collection("room_collection")

/**
 * Post message POST  https://shopeer.com/chat/message?chat_id=[chat_id]
 * Send a chat message to peer/ group
 * Param: room id (M4: message id)
 * Body:  sender email AND message text AND send time
 * Response: message id
*/


//curl -X "POST" -H "Content-Type: application/json" -d '{"email": "nick@gmail.com", "text": "sup!", "time": "rn"}' localhost:8081/chat/message?room_id=62c4d5c76896713a30649546

router.post("/", async (req, res) => {
    try {
        var mssgid = ObjectId();
        // var FCM_token = req.body.FCM_token
        var email = req.body.email
        var text = req.body.text
        var time = req.body.time
        var doc = await coll.updateOne(
            // searches for a document with the following fields
            {_id: ObjectId(req.query.room_id)},
            //appends an object to the "chathistory" array
            {$push: {
                "chathistory": 
                    {
                        "mssg_id": mssgid,
                        "email": email,
                        "text": text,
                        "time": time // frontend will send as long
                    }
                }
            }
        )
        if (!doc) {
            res.status(404).json({response: "Room not found."})
            return
        }
        if (doc.upsertedCount == 1) {
            res.status(201).json({response: "Message successfully posted."})

        } 
        

        // FCM stuff  https://www.techotopia.com/index.php?title=Sending_Firebase_Cloud_Messages_from_a_Node.js_Server&mobileaction=toggle_view_mobile
    //     var payload = {
    //         notification: {
    //           title: email,
    //           body: text
    //         },
    //         data: {
    //           mssgid: mssgid,
    //           time: time
    //         }
    //       };
    //       var options = {
    //         priority: "normal",
    //         timeToLive: 60 * 60
    //       };
        
    //       admin.messaging().sendToDevice(FCM_token, payload, options)
    //       .then(function(response) {
    //         console.log("Successfully sent message:", response);
    //       })
    //       .catch(function(error) {
    //         console.log("Error sending message:", error);
    //       });
    //     //
    //     console.log("\n mssg from " + req.body.email + " added to group chat with id " + mssgid)
    //     res.status(200).send(mssgid)
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }

})