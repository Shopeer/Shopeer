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

async function getUser(profile_email) {
  require("dotenv").config()
  const { MongoClient } = require("mongodb")
  // const uri = "mongodb://admin:shopeer@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
  const uri = "mongodb://" + process.env.DB_USER + ":" + process.env.DB_PASS + "@20.230.148.126:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
  const mongoClient = new MongoClient(uri)
  // const user_collection = mongoClient.db("shopeer_database").collection("user_collection")
  const user_test_collection = mongoClient.db("shopeer_database").collection("test_collection")
  mongoClient.connect()

  var find_cursor = user_test_collection.findOne({ email: profile_email })

  return find_cursor
}


module.exports = { user_profile_router, getUser };