
var express = require("express")
const user_profile_router = express.Router()
const validator = require('validator')

var user_collection = require('../config/mongodb_connection')


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
        if (!find_cursor) {
            res.status(404).json({ response: "User not found." })
            return
        }
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
    var profile_name = req.body.name
    var profile_description = req.body.description
    var profile_photo = req.body.photo

    try {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            res.status(404).json({ response: "User not found." })
            return
        }
        res.status(200).send(find_cursor)
        if (profile_name) {
            await user_collection.updateOne({ email: profile_email }, { $set: { name: profile_name } })
        }
        if (profile_email) {
            await user_collection.updateOne({ email: profile_email }, { $set: { email: profile_email } })
        }
        if (profile_description) {
            await user_collection.updateOne({ email: profile_email }, { $set: { description: profile_description } })
        }
        if (profile_photo) {
            await user_collection.updateOne({ email: profile_email }, { $set: { photo: profile_photo } })
        }
        // var find_cursor = await user_collection.findOne({ email: profile_email })
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

    if (profile.email == null || profile.name == null) {
        res.status(400).send("Error")
    } else if (!validator.isEmail(profile.email)) {
        res.status(400).send("Error: Invalid email")
    } else if (!validator.isAlpha(profile.name)) {
        res.status(400).send("Error: Invalid name")
    } else if (validator.isEmpty(profile.email)) {
        res.status(400).send("Error: Invalid email")
    } else if (validator.isEmpty(profile.name)) {
        res.status(400).send("Error: Invalid name")
    } else {
        try {
            profile_email = profile.email
            var find_cursor = await user_collection.findOne({ email: profile_email })
            if (find_cursor) {
                res.status(409).send("User already exists")
            } else {
                var user_object = create_user_object(profile)
                var result_debug = await user_collection.insertOne(user_object)
                if (!result_debug) {
                    res.status(400).json({ response: "Failed to register user." })
                    return
                } else {
                    res.status(200).send("Success")
                }
            }
        } catch (err) {
            console.log(err)
            res.status(400).send(err)
        }
    }
})



// Delete User DELETE https://shopeer.com/user/registration?user_id=[user_id]
// Removes the user from User Database and clears all info regarding the user
// Body (Parameter): <user_email>
// Response: success/fail


user_profile_router.delete("/registration", async (req, res) => {
    var profile_email = req.query.email
    try {
        var status_code
        var text_res
        // var find_cursor = await user_collection.find({email:profile_email})
        var delete_return = await user_collection.deleteMany({ email: profile_email })
        if (!delete_return) {
            status_code = 404
            text_res = "User not found"
            // res.status(404).json({response: "User not found."})
        } else {
            if (delete_return.deletedCount > 0) {
                status_code = 200
                text_res = "User deleted"
                // res.status(200).send("User deleted")
            } else {
                status_code = 404
                text_res = "User does not exist"
                // res.status(404).send("User does not exist")
            }
        }
        res.status(status_code).send(text_res)
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
        // FCM_token: body.FCM_token,
        searches: [],
        peers: [],
        invites: [],
        received_invites: [],
        blocked: []
    }
    return user_object
}
/**
 * Add FCM token to user profile object PUT https://shopeer.com/user/registration/FCM?email=[email]
 * Body: FCM_token
 * Returns: success/fail
 */
//curl -X "PUT" -H "Content-Type: application/json" -d '{"FCM_token": "test token" }' localhost:8081/user/registration/FCM?email="hello@gmail.com"
// user_profile_router.put("/registration/FCM", async (req, res) => {
//     try {
//         var doc = await mongoClient.db("shopeer_database").collection("user_collection").updateOne(
//             {email:req.query.email}, 
//             {$set:{FCM_token:req.body.FCM_token}}
//         )
//         if (doc.matchedCount == 0) {
//             res.status(200).send("\nThis user does not exist yet.\n")
//         } else if (doc.modifiedCount == 0) {
//             res.status(200).send("\nFailed to update token\n")
//         } else {
//             res.status(200).send("\nUpdated user's token\n")
//         }
//     } catch (err) {
//         console.log(err)
//         res.status(400).send(err)
//     }
// })



module.exports = user_profile_router;

