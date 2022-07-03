const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()


const router = express.Router()
// router.get('/', handleRouting);



// Profile Submodule

// Get Profile GET https://shopeer.com/user/profile?user_id=[user_id]
// Returns the user details (profile, name, bio, weights) of a user
// Param: user_id
// Body: user id token
// Response: User details (profile, bio, name)

router.get("/getprofile", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:ObjectId(req.query._id)})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)
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

router.put("/updateprofile", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({_id:ObjectId(req.query._id)})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



// Delete User DELETE https://shopeer.com/user/registration?user_id=[user_id]
// Removes the user from User Database and clears all info regarding the user
// Param: user id
// Body: User Id Token
// Response: success/fail

router.delete("/remove_user", async (req, res) => {
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find({_id:ObjectId(req.query._id)})
        // var find_cursor = await mongoClient.db("shopeer_database").collection("room_collection").find()
        // var objectId = req.body._id; 
        res.status(200).send("yes")
        var temp = await find_cursor.toArray()
        console.log(temp)

    } catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



// Register User POST https://shopeer.com/user/register
// Assuming user has already authenticated their Gmail with Google Authentication on the frontend, calling this interface checks User Database if account with user’s Gmail exists. If it does not exist and is not banned, calls User Profile submodule to create profile, and calls User Database to add new user.
// Body: user_email
// Response: user_id, user id token 


router.post("/register", async (req, res) => {
    try {
        // var result_debug = await mongoClient.db("shopeer_database").collection("user_collection").insertOne(req.body)
        
        console.log(req.body)

        var user_object = create_user_object(req.body)
        
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