const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const searches_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)

const user_collection = user_collection


// Get Active searches GET https://shopeer.com/match/searches
// Gets a list of all active_searches under the user from User Database
// Body: User Id Token
// Response: list of search ids
searches_router.get("/searches", async (req, res) => {
    var profile_email = req.query.email
    try {
        var find_cursor = await user_collection.findOne({email:profile_email})
        
        var temp_arry = await find_cursor.searches
        console.log(temp_arry)
        res.status(200).send(temp_arry)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})



// Add New Active Search POST https://shopeer.com/match/searches
// Adds a new active search for suggested matches to User Database
// Body: User Id Token, location, distance, activity, budget
// Response: search id
searches_router.post("/searches", async (req, res) => {
    console.log(req.query)
    var profile_email = req.query.email
    var search = req.body.search
    try {
        var find_cursor = await user_collection.findOne({email:profile_email})
        // console.log(find_cursor)
        // res.status(200).send("ok")

        if (find_cursor.searches.includes(search)){
            console.log("Search already added")
            res.status(200).send(find_cursor)
        } else {
            var debug_res = await user_collection.updateOne({email:profile_email},{$push: {searches: search}})
            var find_cursor = await user_collection.findOne({email:profile_email})
            res.status(200).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})



// Delete active search DELETE https://shopeer.com/match/searches?search_id=[id]
// Removes a current active search from active_searches to past_searches in User Database
// Param: search id
// Body: User Id Token
// Response: success/ fail
searches_router.delete("/searches", async (req, res) => {
    var profile_email = req.query.email
    var search = req.query.search
    try {
        var find_cursor = await user_collection.findOne({email:profile_email})
        
        if (find_cursor.searches.includes(search)){
            var debug_res = await user_collection.updateOne({email:profile_email},{$pull:{searches:search}})
            var find_cursor = await user_collection.findOne({email:profile_email})
            res.status(200).send(find_cursor)
        } else {
            // var debug_res = await user_collection.updateOne({email:profile_email},{ $pull: { searches: { $match: search } } } )
            var find_cursor = await user_collection.findOne({email:profile_email})
            console.log("Search already deleted")
            res.status(200).send(find_cursor)
        }
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }
})





module.exports = searches_router;

