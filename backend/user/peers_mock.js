var express = require("express")
const user_peers_router = express()

// Peers Submodule
// Get Peers GET https://shopeer.com/user/peers?user_id=[user_id]
// Returns a list of all peers
// Param: User Id
// Response: List of peer objects {peer_id, name, bio, profile_picture}
user_peers_router.get("/peers", async (req, res) => {
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

// Remove Peer DELETE https://shopeer.com/user/peers?peer_id=[id]
// Deletes the peer id from the peers_id of the user in UserDatabase
// Param: peer id to be removed
// Body: User Id Token
// Response: success/fail
user_peers_router.delete("/peers", async (req, res) => {
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

// Get Peers GET https://shopeer.com/user/blocked?user_id=[user_id]
// Returns a list of blocked peers
// Param: User email
// Response: List of peer objects {peer_id, name, bio, profile_picture}
user_peers_router.get("/blocked", async (req, res) => {
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


// Block Peer POST https://shopeer.com/user/peers/blocked?peer_id=[id]
// Adds user to the blocked list, does not appear in peer list, suggested, or invitations
// Param: peer id to be blocked
// Body: User Id Token
// Response: success fail
user_peers_router.post("/blocked", async (req, res) => {
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

// Unblock Peer DELETE https://shopeer.com/user/peers/blocked?peer_id=[id]
// Removes user from the blocked list
// Param: peer id to be unblocked
// Body: User Id Token
// Response: success/ fail
user_peers_router.delete("/blocked", async (req, res) => {
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

// Get Invitations GET  https://shopeer.com/match/invitations?user_id=[id]
// Returns a list of peer_ids from Received_invitations_id in UserDatabase
// Param: user id
// Body: User Id Token
// Response: list of peer ids
user_peers_router.get("/invitations", async (req, res) => {
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


// Send Peer Invitation POST https://shopeer/match/invitations?peer_id=[id]
// Sends an invitation to the selected peer IF user is not in peer’s Blocked_users_id. The user’s invitation is stored in peer’s “recived invitations” list in the User Collection. 
// Param: peer id to send the invitation to
// Body: User Id Token
// Response: success/ fail
user_peers_router.post("/invitations", async (req, res) => {
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

user_peers_router.delete("/invitations", async (req, res) => {
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




module.exports = user_peers_router;