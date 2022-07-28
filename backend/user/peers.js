require('http');
var express = require("express")
//express()

const user_peers_router = express.Router()

// const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const {MongoClient} = require("mongodb")
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
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }
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
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }

        if (find_cursor.peers.includes(target_peer_email)) {
            console.log("Peer already in added")
            res.status(409).send(find_cursor)
        } else {
            await user_collection.updateOne({ email: profile_email }, { $push: { peers: target_peer_email } })
            // var find_cursor = await user_collection.findOne({ email: profile_email })
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
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }

        if (find_cursor.peers.includes(target_peer_email)) {
            var profile = await user_collection.updateOne({ email: profile_email }, { $pull: { peers: target_peer_email } })
            var target = await user_collection.updateOne({ email: target_peer_email }, { $pull: { peers: profile_email } })
            if (profile.modifiedCount === 1 && target.modifiedCount === 1) {
                res.status(200).json({response: "Peers removed from each others' peerlists."})

            }
            
        } else {
            console.log("Peer already not in existence")
            // var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(404).json({response: "Target peer not found."})
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
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }
        ret_array = await get_object_array_from_email_array(find_cursor.blocked)
        // console.log(ret_array)
        if (!ret_array) {
            res.status(400).json({response: "Failed to get blocked list."})
            return
        }
        // if (ret_array.length > 0 ) {
        //     res.status(200).send(find_cursor.blocked)
        // } else {
        //     res.status(404).send("could not find specified emails")
        // }
        res.status(200).send(find_cursor.blocked)
        
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
        if (profile_email == target_peer_email) {
            res.status(409).json({response: "Cannot operate on self."})
            return
        }
        var find_cursor = await user_collection.findOne({ email: profile_email })
        var target_cursor = await user_collection.findOne({ email: target_peer_email })
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }
        if (!target_cursor) {
            res.status(404).json({response: "Target user not found."})
            return
        }
        if (find_cursor.blocked.includes(target_peer_email)) {
            res.status(409).json({response: "User already in blocklist."})
        } else {
            
            await user_collection.updateOne({ email: profile_email }, { $pull: { invites: target_peer_email, received_invites: target_peer_email, peers: target_peer_email } })
            await user_collection.updateOne({ email: target_peer_email }, { $pull: { invites: profile_email, received_invites: profile_email, peers: profile_email } })
            await user_collection.updateOne({ email: profile_email }, { $push: { blocked: target_peer_email } })
            res.status(201).send(await user_collection.findOne({ email: profile_email }))
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
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }

        if (find_cursor.blocked.includes(target_peer_email)) {
            await user_collection.updateOne({ email: profile_email }, { $pull: { blocked: target_peer_email } })
            await user_collection.findOne({ email: profile_email })
            res.status(200).send(find_cursor)
            // res.status(200).send("Success")
        } else {
            // var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(404).send("Target peer is not in blocklist")
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
            res.status(404).json({response: "User not found."})
            return
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
            res.status(404).json({response: "User not found."})
            return
        }
        ret_array = await get_object_array_from_email_array(find_cursor.received_invites)
        if (!ret_array) {
            res.status(400).json({response: "Failed to get received invitations."})
            return
        }
        res.status(200).send(ret_array)

        // console.log(ret_array)
        // res.status(200).send(ret_array)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

async function get_object_array_from_email_array(email_array) {
    // console.log(email_array)
    // var array = []
    // for (let i = 0; i < email_array.length; i++) {
    //     var return_cursor = await user_collection.findOne({ email: email_array[i] })
    //     if (!return_cursor) {
    //         throw "Error: Invalid email"
    //     }
    //     // console.log(return_cursor)
    //     array.push(return_cursor)
    // }

    var return_arr = await user_collection.find({ email: { $in: email_array } }).toArray()

    console.log(return_arr)
    if (!return_arr) {
        throw "Error: invalid email"
    }
    return return_arr
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
        if (profile_email == target_peer_email) {
            res.status(409).json({response: "Cannot operate on self."})
            return
        }
        var find_cursor = await user_collection.findOne({ email: profile_email })
        var target_cursor = await user_collection.findOne({ email: target_peer_email})
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }
        if (!target_cursor) {
            res.status(404).json({response: "Target user not found."})
            return
        }
        if (target_cursor.blocked.includes(profile_email)) {
            console.log("This user is blocked.")
            res.status(400).send({response: "The target user cannot be invited."})
            return
        }
        if (find_cursor.peers.includes(target_peer_email)) {
            console.log("Target already in peerlist")
            res.status(409).send({response: "Target already in peerlist."})
            return
        }
        if (find_cursor.invites.includes(target_peer_email)) {
            console.log("Target already in invitation list")
            res.status(409).send({response: "Target already in invitation list."})
            
        } else {
            if (target_cursor.invites.includes(profile_email)) {
                await user_collection.updateOne({ email: profile_email }, { $push: { peers: target_peer_email }, $pull: { invites: target_peer_email, received_invites: target_peer_email } })
                await user_collection.updateOne({ email: target_peer_email }, { $push: { peers: profile_email }, $pull: { invites: profile_email, received_invites: profile_email } })
                res.status(201).json({response: "Success, both are now peers."})
            } else {
                await user_collection.updateOne({ email: profile_email }, { $push: { invites: target_peer_email } })
                await user_collection.updateOne({ email: target_peer_email }, { $push: { received_invites: profile_email } })
                // var find_cursor = await user_collection.findOne({ email: profile_email })
                res.status(200).json({response: "Success, invitation sent."})
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
        if (!find_cursor) {
            res.status(404).json({response: "User not found."})
            return
        }

        if (find_cursor.invites.includes(target_peer_email)) {
            await user_collection.updateOne({ email: profile_email }, { $pull: { invites: target_peer_email } })
            await user_collection.updateOne({ email: target_peer_email }, { $pull: { received_invites: profile_email } })
            // var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(200).send(await user_collection.findOne({ email: profile_email }))
            // res.status(200).send("Success")
        } else {
            //var find_cursor = await user_collection.findOne({ email: profile_email })
            res.status(404).json({response: "Target user not found."})
            // res.status(200).send("Fail")
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})




module.exports = user_peers_router;