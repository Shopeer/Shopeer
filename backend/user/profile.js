const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const user_profile_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = mongoClient.db("shopeer_database").collection("user_collection")

// Profile Submodule

// Get Profile GET https://shopeer.com/user/profile?user_id=[user_id]
// Returns the user details (profile, name, bio, weights) of a user
// Param: user_id
// Body: user id token
// Response: User details (profile, bio, name)

user_profile_router.get("/profile", async (req, res) => {
    var profile_email = req.query.email
    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        res.status(200).send(find_cursor)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})


// Edit Profile PUT https://shopeer.com/user/profile?user_id=[user_id]
// Edits fields in the profile
// Body: user id token AND New profile info {profile_pic, name, bio}
// Response: success/fail

user_profile_router.put("/profile", async (req, res) => {
    var profile_email = req.query.email
    var profile_name = req.query.name
    var profile_description = req.query.description
    var profile_photo = req.query.photo

    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (profile_name) {
            var find_cursor = await user_collection.updateOne({ email: profile_email }, { $set: { name: profile_name } })
        }
        if (profile_email) {
            var find_cursor = await user_collection.updateOne({ email: profile_email }, { $set: { email: profile_email } })
        }
        if (profile_description) {
            var find_cursor = await user_collection.updateOne({ email: profile_email }, { $set: { description: profile_description } })
        }
        if (profile_photo) {
            var find_cursor = await user_collection.updateOne({ email: profile_email }, { $set: { photo: profile_photo } })
        }
        var find_cursor = await user_collection.findOne({ email: profile_email })
        res.status(200).send(find_cursor)
        // res.status(200).send("Success")
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})

// Register User POST https://shopeer.com/user/register
// Body (Parameter): {"name":<user_name>, "email":<user_email>}
// Response: user_id

user_profile_router.post("/registration", async (req, res) => {
    var profile = req.query
    try {
        profile_email = req.query.email
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (find_cursor) {
            res.status(200).send("User already exists")
        } else {
            var user_object = create_user_object(profile)
            var result_debug = await user_collection.insertOne(user_object)
            res.status(200).send(user_object)
        }

    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})


function create_user_object(body) {
    var user_object = {
        name: body.name,
        email: body.email,
        description: body.description,
        photo: body.photo,
        FCM_token: body.FCM_token,
        searches: [],
        peers: [],
        invites: [],
        received_invites: [],
        blocked: []
    }
    return user_object
}

// Delete User DELETE https://shopeer.com/user/registration?user_id=[user_id]
// Removes the user from User Database and clears all info regarding the user
// Body (Parameter): <user_email>
// Response: success/fail


user_profile_router.delete("/registration", async (req, res) => {
    var profile_email = req.query.email
    try {
        // var find_cursor = await user_collection.find({email:profile_email})
        var delete_return = await user_collection.deleteOne({ email: profile_email })
        if (delete_return.deletedCount == 1) {
            res.status(200).send("User deleted")
        } else {
            res.status(200).send("User does not exist")
        }
    } catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})



module.exports = user_profile_router;

