const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const searches_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)




// Searches Submodule

// Get Active searches GET https://shopeer.com/match/searches
// Gets a list of all active_searches under the user from User Database
// Body: User Id Token
// Response: list of search ids
searches_router.get("/get_all_searches", async (req, res) => {
    var profile_email = req.query.email
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
        res.status(200).send("yes")
        var temp_arry = await find_cursor.searches.toArray()
        console.log(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



// Add New Active Search POST https://shopeer.com/match/searches
// Adds a new active search for suggested matches to User Database
// Body: User Id Token, location, distance, activity, budget
// Response: search id
searches_router.post("/add_search", async (req, res) => {
    var profile_email = req.query.email
    var new_search = req.query.new_search
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
        
        if (find_cursor.peers.includes(new_peer_email)){
            console.log("Search already in added")
            res.status(200).send(find_cursor)
        } else {
            var debug_res = await mongoClient.db("shopeer_database").collection("user_collection").updateOne({email:profile_email},{$push: {searches: new_search}})
            var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
            res.status(200).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})



// Delete active search DELETE https://shopeer.com/match/searches?search_id=[id]
// Removes a current active search from active_searches to past_searches in User Database
// Param: search id
// Body: User Id Token
// Response: success/ fail
searches_router.get("/delete_search", async (req, res) => {
    var profile_email = req.query.email
    var new_search = req.query.new_search
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
        
        if (!find_cursor.searches.includes(new_search)){
            console.log("Search already deleted")
            res.status(200).send(find_cursor)
        } else {
            var debug_res = await mongoClient.db("shopeer_database").collection("user_collection").updateOne({email:profile_email},{ $pull: { searches: { $match: new_search } } } )
            var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
            res.status(200).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})


// Edit Active search PUT https://shopeer.com/match/searches?search_id=[id]
// Edits location/ activity of a current search_id in active search in User Database
// Param: search id
// Body: User Id Token AND  location / activity
// Response: success/fail
searches_router.put("/put_search", async (req, res) => {
    var profile_email = req.query.email
    var new_search = req.query.new_search
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
        
        if (find_cursor.searches.includes(new_search)){
            console.log("Search already added")
            res.status(200).send(find_cursor)
        } else {
            var debug_res = await mongoClient.db("shopeer_database").collection("user_collection").updateOne({email:profile_email},{ $push: { searches: { $match: new_search } } } )
            var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
            res.status(200).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.send(400).send(err)
    }
})









module.exports = searches_router;

