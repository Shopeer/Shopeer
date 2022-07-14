const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const user_peers_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

// Peers Submodule
// Get Peers GET https://shopeer.com/user/peers?user_id=[user_id]
// Returns a list of all peers
// Param: User Id
// Response: List of peer objects {peer_id, name, bio, profile_picture}
user_peers_router.get("/peers", async (req, res) => {
    var profile_email = req.query.email
    try {
        var array = []
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {throw "user not found"}
        for (let i = 0; i < find_cursor.peers.length; i++) {
            var return_cursor = await user_collection.findOne({ email: find_cursor.peers[i] })
            array.push(return_cursor)
        }
        res.status(200).send(array)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

// Add peer
user_peers_router.post("/peers", async (req, res) => {
    var profile_email = req.query.email
    var target_peer_email = req.query.target_peer_email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })

        if (find_cursor.peers.includes(target_peer_email)) {
            console.log("This user is already a peer")
            res.status(200).send(find_cursor)
        } else {
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { peers: target_peer_email } })
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(200).send(find_cursor)
        }

    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})


// Remove Peer DELETE https://shopeer.com/user/peers?peer_id=[id]
// Deletes the peer id from the peers_id of the user in UserDatabase
// Param: peer id to be removed
// Body: User Id Token
// Response: success/fail
user_peers_router.delete("/peers", async (req, res) => {
    var profile_email = req.query.email
    var target_peer_email = req.query.target_peer_email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })

        if (find_cursor.peers.includes(target_peer_email)) {
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { peers: target_peer_email } })
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(200).send(find_cursor)
        } else {
            console.log("Peer already not in existence")
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(404).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

// Get Peers GET https://shopeer.com/user/blocked?user_id=[user_id]
// Returns a list of blocked peers
// Param: User email
// Response: List of peer objects {peer_id, name, bio, profile_picture}
user_peers_router.get("/blocked", async (req, res) => {
    var profile_email = req.query.email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        ret_array = await get_object_array_from_email_array(find_cursor.blocked)
        console.log(ret_array)
        res.status(200).send(ret_array)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})


// Block Peer POST https://shopeer.com/user/peers/blocked?peer_id=[id]
// Adds user to the blocked list, does not appear in peer list, suggested, or invitations
// Param: peer id to be blocked
// Body: User Id Token
// Response: success fail
user_peers_router.post("/blocked", async (req, res) => {
    var profile_email = req.query.email
    var target_peer_email = req.query.target_peer_email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })

        if (find_cursor.blocked.includes(target_peer_email)) {
            console.log("Peer already in blocklist")
            res.status(409).send(find_cursor)
        } else {
            
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { invites: target_peer_email } })
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { peers: target_peer_email } })
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { blocked: target_peer_email } })
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(201).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

// Unblock Peer DELETE https://shopeer.com/user/peers/blocked?peer_id=[id]
// Removes user from the blocked list
// Param: peer id to be unblocked
// Body: User Id Token
// Response: success/ fail
user_peers_router.delete("/blocked", async (req, res) => {
    var profile_email = req.query.email
    var target_peer_email = req.query.target_peer_email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })

        if (find_cursor.blocked.includes(target_peer_email)) {
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { blocked: target_peer_email } })
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(200).send(find_cursor)
            // res.status(200).send("Success")
        } else {
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(404).send(find_cursor)
            // res.status(200).send("Fail")
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

// Get Invitations GET  https://shopeer.com/match/invitations?user_id=[id]
// Returns a list of peer_ids from Received_invitations_id in UserDatabase
// Param: user id
// Body: User Id Token
// Response: list of peer ids
user_peers_router.get("/invitations", async (req, res) => {
    var profile_email = req.query.email

    try {
        var array = []
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            throw "Error: Invalid email"
        }
        for (let i = 0; i < find_cursor.invites.length; i++) {
            var return_cursor = await user_collection.findOne({ email: find_cursor.invites[i] })
            array.push(return_cursor)
        }
        res.status(200).send(array)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

// Get Received Invites 
// Param: User email
// Response: List of peer objects {peer_id, name, bio, profile_picture}
user_peers_router.get("/invitations/received", async (req, res) => {
    var profile_email = req.query.email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            throw "Error: Invalid email"
        }
        ret_array = await get_object_array_from_email_array(find_cursor.received_invites)
        console.log(ret_array)
        res.status(200).send(ret_array)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

async function get_object_array_from_email_array(email_array) {
    // console.log(email_array)
    var array = []
    for (let i = 0; i < email_array.length; i++) {
        var return_cursor = await user_collection.findOne({ email: email_array[i] })
        if (!return_cursor) {
            throw "Error: Invalid email"
        }
        // console.log(return_cursor)
        array.push(return_cursor)
    }
    // console.log(array)
    return array
}


// Send Peer Invitation POST https://shopeer/match/invitations?peer_id=[id]
// Sends an invitation to the selected peer IF user is not in peer’s Blocked_users_id. The user’s invitation is stored in peer’s “recived invitations” list in the User Collection. 
// Param: peer id to send the invitation to
// Body: User Id Token
// Response: success/ fail
user_peers_router.post("/invitations", async (req, res) => {
    var profile_email = req.query.email
    var target_peer_email = req.query.target_peer_email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            throw "Error: Invalid email"
        }
        if (find_cursor.invites.includes(target_peer_email)) {
            console.log("This user is already in the peerlist")
            res.status(409).send(find_cursor)
        } else {
            var find_cursor = await user_collection.findOne({ email: target_peer_email })
            if (!find_cursor) {
                throw "Error: Invalid email"
            }
            if (find_cursor.invites.includes(profile_email)) {
                var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { peers: target_peer_email } })
                var debug_res = await user_collection.updateOne({ email: target_peer_email }, { $push: { peers: profile_email } })
                var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { invites: target_peer_email } })
                var debug_res = await user_collection.updateOne({ email: target_peer_email }, { $pull: { invites: profile_email } })
                var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { received_invites: target_peer_email } })
                var debug_res = await user_collection.updateOne({ email: target_peer_email }, { $pull: { received_invites: profile_email } })

                res.status(201).send("Success, both are now peers")
            } else {
                var debug_res = await user_collection.updateOne({ email: profile_email }, { $push: { invites: target_peer_email } })
                var debug_res = await user_collection.updateOne({ email: target_peer_email }, { $push: { received_invites: profile_email } })
                var find_cursor = await user_collection.findOne({ email: profile_email })
                res.status(200).send(find_cursor)
            }
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

user_peers_router.delete("/invitations", async (req, res) => {
    var profile_email = req.query.email
    var target_peer_email = req.query.target_peer_email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })

        if (find_cursor.invites.includes(target_peer_email)) {
            var debug_res = await user_collection.updateOne({ email: profile_email }, { $pull: { invites: target_peer_email } })
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(200).send(find_cursor)
            // res.status(200).send("Success")
        } else {
            var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(404).send(find_cursor)
            // res.status(200).send("Fail")
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})




module.exports = user_peers_router;