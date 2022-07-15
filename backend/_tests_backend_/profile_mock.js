const express = require('express');
const user_profile_router = express()



// Profile Submodule

// Get Profile GET https://shopeer.com/user/profile?user_id=[user_id]
// Returns the user details (profile, name, bio, weights) of a user
// Param: user_id
// Body: user id token
// Response: User details (profile, bio, name)

user_profile_router.get("/profile", async (req, res) => {
  if (req.query.email == "jimothy@gmail.com") {
    res
      .status(200)
      .json({
        email: req.query.email
      });
  } else {
    res
      .status(400)
      .send("Error")
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

  if (req.query.email == "jimothy@gmail.com") {
    res
      .status(200)
      .json({
        email: req.query.email,
        // name: req.query.name,
        name: "Jimothy",
        description: req.query.description,
        photo: req.query.photo
      });
  } else {
    res
      .status(400)
      .send("Error")
  }
})

// Register User POST https://shopeer.com/user/register
// Body (Parameter): {"name":<user_name>, "email":<user_email>}
// Response: user_id

user_profile_router.post("/registration", async (req, res) => {
  if (req.query.email == "jimothy@gmail.com") {
    res
      .status(200)
      .send("Success");
  } else {
    res
      .status(400)
      .send("Error")
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
  if (req.query.email == "jimothy@gmail.com") {
    res
      .status(200)
      .send("Success");
  } else {
    res
      .status(400)
      .send("Error")
  }
})

/**
 * Add FCM token to user profile object PUT https://shopeer.com/user/registration/FCM?email=[email]
 * Body: FCM_token
 * Returns: success/fail
 */
//curl -X "PUT" -H "Content-Type: application/json" -d '{"FCM_token": "test token" }' localhost:8081/user/registration/FCM?email="hello@gmail.com"
user_profile_router.put("/registration/FCM", async (req, res) => {
  if (req.query.email == "jimothy@gmail.com" & req.query.FCM_token == "asdfqwer") {
    res
      .status(200)
      .send("Success");
  } else {
    res
      .status(400)
      .send("Error")
  }
})




module.exports = user_profile_router;

