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
    var profile = req.query
    if (!validator.isEmail(profile.email)) {
        res.status(400).send("Error: Invalid email")
    } else {
        var find_cursor = await user_collection.findOne({ email: profile.email })
        if (!find_cursor) {
            res.status(404).json({ response: "User does not exist" })
        } else {
            res.status(200).send(find_cursor)
        }
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


    if (profile_email == null) {
        res.status(400).send("Error: Invalid email")

    } 
    else if (!validator.isEmail(profile_email)) {
        res.status(400).send("Error: Invalid email")

    } 
    // else if (!error_check_registration(profile_name)) {
    //     res.status(400).send("Error: Invalid name")
    // } 
    else {
        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (!find_cursor) {
            res.status(404).json({ response: "User not found." })
            return
        }
        if (profile_name) {
            if (profile_name.length === 0 || !error_check_registration(profile_name)) {
                res.status(400).send("Error: Invalid name")
                return
            } 
            await user_collection.updateOne({ email: profile_email }, { $set: { name: profile_name } })
        }
        // if (profile_email) {
        //     await user_collection.updateOne({ email: profile_email }, { $set: { email: profile_email } })
        // }
        if (profile_description) {
            await user_collection.updateOne({ email: profile_email }, { $set: { description: profile_description } })
        }
        if (profile_photo) {
            await user_collection.updateOne({ email: profile_email }, { $set: { photo: profile_photo } })
        }
        res.status(200).send("Success")
    }
})


// Register User POST https://shopeer.com/user/register
// Body (Parameter): {"name":<user_name>, "email":<user_email>}
// Response: user_id
user_profile_router.post("/registration", async (req, res) => {
    var profile
    if (req.query.email != null && req.query.name != null) {
        profile = {
            name: req.query.name,
            email: req.query.email
        }
    } else if (req.body != null) {
        profile = {
            name: req.body.name,
            email: req.body.email,
            photo: req.body.photo
        }
    } 
    if (!validator.isEmail(profile.email)) {
        res.status(400).send("Error: Invalid email")
    } else if (!error_check_registration(profile.name)) {
        res.status(400).send("Error: Invalid name")
    } else {
        profile_email = profile.email

        var find_cursor = await user_collection.findOne({ email: profile_email })
        if (find_cursor) {
            res.status(409).send("User already exists")
        }  else {
            var user_object = create_user_object(profile)
            var result_debug = await user_collection.insertOne(user_object)
            res.status(201).send("Success")
        }
    }
})



// Delete User DELETE https://shopeer.com/user/registration?user_id=[user_id]
// Removes the user from User Database and clears all info regarding the user
// Body (Parameter): <user_email>
// Response: success/fail
user_profile_router.delete("/registration", async (req, res) => {
    var profile_email = req.query.email
    var profile = req.query
    
    if (!validator.isEmail(profile.email)) {
        res.status(400).send("Error: Invalid email")
    } else {
            var status_code
            var text_res
            var delete_return = await user_collection.deleteMany({ email: profile_email })
                if (delete_return.deletedCount > 0) {
                    status_code = 200
                    text_res = "User deleted"
                } else {
                    status_code = 404
                    text_res = "User does not exist"
                }
            res.status(status_code).send(text_res)
    }
})

// function does_user_exist(){

// }

function create_user_object(body) {
    var user_object = {
        name: body.name,
        email: body.email,
        description: body.description,
        photo: body.photo,
        searches: [],
        peers: [],
        invites: [],
        received_invites: [],
        blocked: []
    }
    return user_object
}


async function getUser(profile_email) {
    return await user_collection.findOne({ email: profile_email })
}

function error_check_registration(field) {

    if (!onlyLettersAndSpaces(field) || !field) {
        return false
    }
    return true
}
function onlyLettersAndSpaces(str) {
    return /^[A-Za-z\s]*$/.test(str);
}


module.exports = {user_profile_router, getUser};


