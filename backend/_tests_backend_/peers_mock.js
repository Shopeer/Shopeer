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

// // Get Received Invites 
// // Param: User email
// // Response: List of peer objects {peer_id, name, bio, profile_picture}
// user_peers_router.get("/invitations/received", async (req, res) => {
//     var profile_email = req.query.email
//     try {
//         var find_cursor = await user_collection.findOne({ email: profile_email })
//         if (!find_cursor) {
//             throw "Error: Invalid email"
//         }
//         ret_array = await get_object_array_from_email_array(find_cursor.received_invites)
//         console.log(ret_array)
//         res.status(200).send(ret_array)
//     }
//     catch (err) {
//         console.log(err)
//         res.status(400).send(err)
//     }
// })

// async function get_object_array_from_email_array(email_array) {
//     // console.log(email_array)
//     var array = []
//     for (let i = 0; i < email_array.length; i++) {
//         var return_cursor = await user_collection.findOne({ email: email_array[i] })
//         if (!return_cursor) {
//             throw "Error: Invalid email"
//         }
//         // console.log(return_cursor)
//         array.push(return_cursor)
//     }
//     // console.log(array)
//     return array
// }

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