const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const user_peers_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

// Peers Submodule
// Get Peers GET https://shopeer.com/user/peers?user_id=[user_id]
// Returns a list of all peers
// Param: User Id
// Response: List of peer objects {peer_id, name, bio, profile_picture}
user_peers_router.get("/get_all_peers", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// Remove Peer DELETE https://shopeer.com/user/peers?peer_id=[id]
// Deletes the peer id from the peers_id of the user in UserDatabase
// Param: peer id to be removed
// Body: User Id Token
// Response: success/fail
user_peers_router.get("/get_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// Block Peer POST https://shopeer.com/user/peers/blocked?peer_id=[id]
// Adds user to the blocked list, does not appear in peer list, suggested, or invitations
// Param: peer id to be blocked
// Body: User Id Token
// Response: success fail
user_peers_router.get("/get_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// Unblock Peer DELETE https://shopeer.com/user/peers/blocked?peer_id=[id]
// Removes user from the blocked list
// Param: peer id to be unblocked
// Body: User Id Token
// Response: success/ fail
user_peers_router.get("/get_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// Get Invitations GET  https://shopeer.com/match/invitations?user_id=[id]
// Returns a list of peer_ids from Received_invitations_id in UserDatabase
// Param: user id
// Body: User Id Token
// Response: list of peer ids
user_peers_router.get("/get_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


// Send Peer Invitation POST https://shopeer/match/invitations?peer_id=[id]
// Sends an invitation to the selected peer IF user is not in peer’s Blocked_users_id. The user’s invitation is stored in peer’s “recived invitations” list in the User Collection. 
// Param: peer id to send the invitation to
// Body: User Id Token
// Response: success/ fail
user_peers_router.get("/get_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})

// Accept/Decline Peer Invitation POST https://shopeer.com/match/invitations/accept?peer_id=[id]+accept=[true/false]
// Accept: will move the peer from received_invitations_id to peers_id in User Database + move user from sent_invitations_id to peers_id of the peer + call New group chatDecline: remove peer from  received_invitations_id of current user + remove user from sent_invitations_id of peers
// Param: peer_id AND accept / decline
// Response: success / fail
user_peers_router.get("/get_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:profile_id})
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp_arry = await find_cursor.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


// Add peer
user_peers_router.post("/add_peer", async (req, res) => {
    var profile_email = req.query.email
    var new_peer_email = req.query.new_peer_email
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
        
        if (find_cursor.peers.includes(new_peer_email)){
            console.log("Peer already in added")
            res.status(200).send(find_cursor)
        } else {
            var debug_res = await mongoClient.db("shopeer_database").collection("user_collection").updateOne({email:profile_email},{$push: {peers: new_peer_email}})
            var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
            res.status(200).send(find_cursor)
        }
        
        


    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



module.exports = user_peers_router;