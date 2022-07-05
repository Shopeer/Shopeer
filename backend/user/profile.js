const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)


// Profile Submodule

// Get Profile GET https://shopeer.com/user/profile?user_id=[user_id]
// Returns the user details (profile, name, bio, weights) of a user
// Param: user_id
// Body: user id token
// Response: User details (profile, bio, name)

router.get("/get_profile", async (req, res) => {
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


// Edit Profile PUT https://shopeer.com/user/profile?user_id=[user_id]
// Edits fields in the profile
// Body: user id token AND New profile info {profile_pic, name, bio}
// Response: success/fail

router.put("/update_profile", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    var profile_name = req.query.name
    var profile_peers = req.query.peers
    var profile_invites = req.query.invites
    var profile_blocked = req.query.blocked

    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").updateOne({email:profile_email}, {$set:{name:profile_name}})
        
        res.status(200).send(find_cursor)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



// Delete User DELETE https://shopeer.com/user/registration?user_id=[user_id]
// Removes the user from User Database and clears all info regarding the user
// Body (Parameter): <user_email>
// Response: success/fail

router.delete("/delete_user", async (req, res) => {
    // var profile_id = ObjectId(req.query._id)
    var profile_email = req.query.email

    try {
        // var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email:profile_email})
        var delete_return = await mongoClient.db("shopeer_database").collection("user_collection").deleteOne({email:profile_email})
        res.status(200).send(delete_return)
    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



// Register User POST https://shopeer.com/user/register
// Body (Parameter): {"name":<user_name>, "email":<user_email>}
// Response: user_id

router.post("/register", async (req, res) => {


    var profile = req.body

    console.log(profile)

    try {
        var user_object = create_user_object(profile)
        
        var result_debug = await mongoClient.db("shopeer_database").collection("user_collection").insertOne(user_object)
        var user_object_id = user_object._id

        res.status(200).send(user_object)

    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


function create_user_object(body) {
    var user_object = {name: body.name,
                        email: body.email,
                        peers: null,
                        invites: null,
                        blocked: null}
    return user_object
}



module.exports = router;