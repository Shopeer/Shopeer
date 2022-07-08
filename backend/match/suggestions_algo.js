const http = require('http');
var express = require("express")
const { IPv4 } = require("ipaddr.js")
const app = express()

const suggestions_algo_router = express.Router()

const { MongoClient, ObjectId } = require("mongodb")  // this is multiple return
const uri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.5.0"
const mongoClient = new MongoClient(uri)


// Get Suggested Matches GET https://shopeer.com/match/suggestions?user_id=[id]
// Returns a list of peer_ids based on the active searches of the current user taking into account the blocked list (let the database do most of the filtering) AND executes the match and recommendation algorithm to compute the peer_ids list 
// Param: user id
// Body: User Id Token, location, activity, distance, budget
// Response: List of peer objects {peer_id, name, bio, profile_picture} / empty list on fail
suggestions_algo_router.get("/suggestions", async (req, res) => {
    var profile_email = req.query.email
    console.log(req.query)
    try {
        var find_cursor = await mongoClient.db("shopeer_database").collection("user_collection").findOne({email:profile_email})
        console.log(find_cursor.searches)
        var target_searches = find_cursor.searches


        var all_users_cursor = await mongoClient.db("shopeer_database").collection("user_collection").find({email: { $ne: profile_email }})
        

        var temp_arry = await all_users_cursor.toArray()
        // console.log(temp_arry)
        // console.log(temp_arry.length)

        var match_list = []
        
        console.log("----------")
        for (let i = 0; i < temp_arry.length; i++){
            var each_search = temp_arry[i].searches
            var each_email = temp_arry[i].email
            console.log(each_search)
            var each_score = 0;
            for (let j = 0; j < each_search.length; j++){
                if (target_searches.includes(each_search[j])){
                    each_score++
                }
            }
            match_list.push([each_email, each_score])
            console.log(each_score)
            console.log(match_list)
        }

        // Bubble sort
        for (let i = 0; i < match_list.length-1; i++) {
            console.log(match_list[i][1])
            console.log(match_list[i+1][1])
            if (match_list[i][1] < match_list[i+1][1]) {
                temp = match_list[i+1]
                match_list[i+1] = match_list[i]
                match_list[i] = temp
            }
        }

        const array = match_list
        customSlice = array => array.slice(0, 1),
        result = array.map(customSlice);

        console.log(result)

        res.status(200).send(result)
    }
    catch (err) {
        console.log(err)
        res.status(400).send(err)
    }


})

// function generate_best_matches(all_users_array) {
//     var best_match_list
//     var score

//     console.log(best_match_list)


//     return best_match_list
// }


// search pref
// location (long, lat)
// range (max km)
// activity [most prefer, ... , least prefer]
// budget (max)


// Write mocking tests
// User module
// Match module


// Front end test
// Browse users
// Manage peers
// Manage blocking

module.exports = suggestions_algo_router;

